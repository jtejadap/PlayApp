package com.proyecto.PlayApp.Controller.Chat;

import com.proyecto.PlayApp.dto.ChatHistoryResponse;
import com.proyecto.PlayApp.dto.ChatSendRequest;
import com.proyecto.PlayApp.dto.ChatSendResponse;
import com.proyecto.PlayApp.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatbotService chatbotService;

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody ChatSendRequest request) {
        try {
            ChatSendResponse response = chatbotService.sendMessage(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible procesar el mensaje"));
        }
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<?> history(@PathVariable String sessionId) {
        try {
            ChatHistoryResponse response = chatbotService.getHistory(sessionId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible recuperar el historial"));
        }
    }
}
