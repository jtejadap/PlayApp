package com.proyecto.PlayApp.Controller.Chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.PlayApp.dto.ChatHistoryResponse;
import com.proyecto.PlayApp.dto.ChatSendRequest;
import com.proyecto.PlayApp.dto.ChatSendResponse;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.service.ChatbotService;
import com.proyecto.PlayApp.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatbotService chatbotService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void send_shouldReturnOk() throws Exception {
        ChatSendRequest request = new ChatSendRequest();
        request.setSessionId("session-1");
        request.setMessage("hola");

        when(chatbotService.sendMessage(any(ChatSendRequest.class)))
                .thenReturn(new ChatSendResponse("session-1", "respuesta", LocalDateTime.now()));

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-1"))
                .andExpect(jsonPath("$.reply").value("respuesta"));
    }

    @Test
    void send_shouldReturnBadRequestWhenValidationFails() throws Exception {
        ChatSendRequest request = new ChatSendRequest();
        request.setMessage(" ");

        when(chatbotService.sendMessage(any(ChatSendRequest.class)))
                .thenThrow(new IllegalArgumentException("El mensaje no puede estar vacio"));

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El mensaje no puede estar vacio"));
    }

    @Test
    void history_shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
        when(chatbotService.getHistory(eq("missing")))
                .thenThrow(new NoSuchElementException("Sesion de chat no encontrada"));

        mockMvc.perform(get("/api/chat/history/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Sesion de chat no encontrada"));
    }

    @Test
    void history_shouldReturnOk() throws Exception {
        ChatHistoryResponse response = new ChatHistoryResponse(
                "session-1",
                List.of(new ChatHistoryResponse.ChatHistoryMessage("user", "hola", LocalDateTime.now()))
        );

        when(chatbotService.getHistory(eq("session-1"))).thenReturn(response);

        mockMvc.perform(get("/api/chat/history/session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-1"))
                .andExpect(jsonPath("$.messages[0].role").value("user"))
                .andExpect(jsonPath("$.messages[0].content").value("hola"));
    }
}
