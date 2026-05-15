package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
}
