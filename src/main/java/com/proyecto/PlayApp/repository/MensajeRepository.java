package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Mensaje;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MensajeRepository extends MongoRepository<Mensaje, String> {
}