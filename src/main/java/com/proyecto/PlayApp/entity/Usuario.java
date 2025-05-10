package com.proyecto.PlayApp.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;

    @Field("nombreCompleto")
    private String nombreCompleto;

    @Field("correo")
    private String correo;

    @Field("password")
    private String password;

    @Field("rol")
    private String rol;
}
