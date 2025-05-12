package com.proyecto.PlayApp.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "envios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Envio {
    @Id
    private String id;
    private Double latitud;
    private Double longitud;
    private String dirreccion;
    private String descripcion;
    private Integer mesa;
    private Integer carpa;
    private LocalDateTime fecha;
    private String clienteId;
}
