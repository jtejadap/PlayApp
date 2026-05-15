package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.ChatSendRequest;
import com.proyecto.PlayApp.dto.ChatSendResponse;
import com.proyecto.PlayApp.entity.ChatMessage;
import com.proyecto.PlayApp.entity.ChatSession;
import com.proyecto.PlayApp.repository.ChatMessageRepository;
import com.proyecto.PlayApp.repository.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private GeminiService geminiService;

    @Mock
    private ChatIntentService chatIntentService;

    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        chatbotService = new ChatbotService(chatSessionRepository, chatMessageRepository, geminiService, chatIntentService);

        when(chatSessionRepository.save(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession session = invocation.getArgument(0);
            if (session.getId() == null) {
                session.setId("session-1");
            }
            return session;
        });
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void sendMessage_shouldSanitizeAndPersistUserAndAssistantMessages() {
        ChatSendRequest request = new ChatSendRequest();
        request.setMessage("  <b>Hola</b>\u0000 ");

        when(chatIntentService.resolve(eq("Hola"), anyString()))
                .thenReturn(ChatIntentService.IntentResolution.handled("faq", "Respuesta de reglas"));

        ChatSendResponse response = chatbotService.sendMessage(request);

        assertEquals("session-1", response.getSessionId());
        assertEquals("Respuesta de reglas", response.getReply());

        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository, times(2)).save(messageCaptor.capture());

        ChatMessage savedUserMessage = messageCaptor.getAllValues().get(0);
        assertEquals("user", savedUserMessage.getRole());
        assertEquals("Hola", savedUserMessage.getContent());

        ChatMessage savedAssistantMessage = messageCaptor.getAllValues().get(1);
        assertEquals("assistant", savedAssistantMessage.getRole());
        assertEquals("Respuesta de reglas", savedAssistantMessage.getContent());

        verify(chatIntentService).resolve(eq("Hola"), eq(null));
    }

    @Test
    void sendMessage_shouldRejectInvalidMessages() {
        ChatSendRequest tooLongRequest = new ChatSendRequest();
        tooLongRequest.setMessage("a".repeat(1001));

        IllegalArgumentException tooLongException = assertThrows(IllegalArgumentException.class,
                () -> chatbotService.sendMessage(tooLongRequest));
        assertTrue(tooLongException.getMessage().contains("maximo permitido"));

        ChatSendRequest emptyRequest = new ChatSendRequest();
        emptyRequest.setMessage("<b> </b>");

        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class,
                () -> chatbotService.sendMessage(emptyRequest));
        assertEquals("El mensaje no puede estar vacio", emptyException.getMessage());
    }
}
