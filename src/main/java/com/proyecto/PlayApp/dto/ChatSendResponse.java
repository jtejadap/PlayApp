package com.proyecto.PlayApp.dto;

import java.time.LocalDateTime;

public class ChatSendResponse {
    private String sessionId;
    private String reply;
    private LocalDateTime timestamp;

    public ChatSendResponse() {
    }

    public ChatSendResponse(String sessionId, String reply, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.reply = reply;
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
