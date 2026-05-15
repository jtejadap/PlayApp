package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.ChatHistoryResponse;
import com.proyecto.PlayApp.dto.ChatSendRequest;
import com.proyecto.PlayApp.dto.ChatSendResponse;
import com.proyecto.PlayApp.entity.ChatMessage;
import com.proyecto.PlayApp.entity.ChatSession;
import com.proyecto.PlayApp.repository.ChatMessageRepository;
import com.proyecto.PlayApp.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {
    private static final int MAX_MESSAGE_LENGTH = 1000;
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final String ROLE_USER = "user";
    private static final String ROLE_ASSISTANT = "assistant";
    private static final String FALLBACK_REPLY = "Ahora mismo tengo una dificultad temporal para responder. Intenta de nuevo en unos segundos, por favor.";
    private static final String GEMINI_CONFIG_REPLY = "El asistente IA esta deshabilitado por configuracion de Gemini. Actualiza GEMINI_API_KEY en config/application-secrets.env y reinicia PlayApp.";
    private static final int CONTEXT_MESSAGES = 4;

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GeminiService geminiService;
    private final ChatIntentService chatIntentService;

    public ChatSendResponse sendMessage(ChatSendRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio");
        }
        String sanitizedMessage = sanitizeMessage(request.getMessage());
        validateRequest(sanitizedMessage);

        ChatSession session = resolveSession(request.getSessionId(), request.getUserId());
        LocalDateTime now = LocalDateTime.now();

        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(session.getId());
        userMessage.setRole(ROLE_USER);
        userMessage.setContent(sanitizedMessage);
        userMessage.setTimestamp(now);
        chatMessageRepository.save(userMessage);

        ChatIntentService.IntentResolution intentResolution = chatIntentService.resolve(userMessage.getContent(), request.getUserId());
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setSessionId(session.getId());
        assistantMessage.setRole(ROLE_ASSISTANT);
        assistantMessage.setContent(resolveAssistantReply(intentResolution, session.getId(), sanitizedMessage));
        assistantMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(assistantMessage);

        session.setLastMessageAt(assistantMessage.getTimestamp());
        chatSessionRepository.save(session);

        return new ChatSendResponse(session.getId(), assistantMessage.getContent(), assistantMessage.getTimestamp());
    }

    public ChatHistoryResponse getHistory(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId es obligatorio");
        }

        chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Sesion de chat no encontrada"));

        List<ChatHistoryResponse.ChatHistoryMessage> messages = chatMessageRepository
                .findBySessionIdOrderByTimestampAsc(sessionId)
                .stream()
                .map(item -> new ChatHistoryResponse.ChatHistoryMessage(
                        item.getRole(),
                        item.getContent(),
                        item.getTimestamp()))
                .toList();

        return new ChatHistoryResponse(sessionId, messages);
    }

    private ChatSession resolveSession(String sessionId, String userId) {
        if (sessionId != null && !sessionId.isBlank()) {
            ChatSession existing = chatSessionRepository.findById(sessionId).orElse(null);
            if (existing != null) {
                if ((existing.getUserId() == null || existing.getUserId().isBlank())
                        && userId != null && !userId.isBlank()) {
                    existing.setUserId(userId.trim());
                }
                return chatSessionRepository.save(existing);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        ChatSession session = new ChatSession();
        session.setUserId(userId == null || userId.isBlank() ? null : userId.trim());
        session.setCreatedAt(now);
        session.setLastMessageAt(now);
        session.setStatus(DEFAULT_STATUS);
        return chatSessionRepository.save(session);
    }

    private void validateRequest(String sanitizedMessage) {
        if (sanitizedMessage == null || sanitizedMessage.isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacio");
        }
        if (sanitizedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("El mensaje supera el maximo permitido de " + MAX_MESSAGE_LENGTH + " caracteres");
        }
    }

    private String sanitizeMessage(String message) {
        if (message == null) {
            return null;
        }
        return message
                .replaceAll("<[^>]*>", "")
                .replaceAll("[\\u0000-\\u0008\\u000B\\u000C\\u000E-\\u001F\\u007F]", "")
                .replace("<", "")
                .replace(">", "")
                .trim();
    }

    private String resolveAssistantReply(ChatIntentService.IntentResolution intentResolution, String sessionId, String userMessage) {
        if (intentResolution.handled()) {
            return intentResolution.response();
        }
        String context = buildShortContext(sessionId);
        return generateAssistantReply(userMessage, context);
    }

    private String generateAssistantReply(String userMessage, String context) {
        String prompt = context == null || context.isBlank()
                ? userMessage
                : "Contexto breve del chat:\n" + context + "\n\nMensaje actual del usuario:\n" + userMessage;
        try {
            return geminiService.generateReply(prompt);
        } catch (Exception ex) {
            log.warn("Fallo al generar respuesta con Gemini. Se devolvera fallback.", ex);
            if (ex.getMessage() != null && ex.getMessage().contains("GEMINI_API_KEY")) {
                return GEMINI_CONFIG_REPLY;
            }
            return FALLBACK_REPLY;
        }
    }

    private String buildShortContext(String sessionId) {
        List<ChatMessage> fullHistory = chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId).stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        int startIndex = Math.max(0, fullHistory.size() - CONTEXT_MESSAGES);
        List<ChatMessage> recent = fullHistory.subList(startIndex, fullHistory.size());

        return recent.stream()
                .map(item -> item.getRole() + ": " + item.getContent())
                .collect(Collectors.joining("\n"));
    }
}
