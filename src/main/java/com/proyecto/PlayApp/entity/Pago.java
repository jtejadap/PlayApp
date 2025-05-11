package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    private String id;
    private String metodo;
    private double valor;
    private Integer estado;
    private LocalDateTime fecha;
    private String clienteId;

    public String obtenerNombreEstado() {
        String[] estados = {"En espera", "Terminado", "Cancelado"};
        if (estado >= 0 && estado < 3) {
            return estados[estado];
        }
        return "Sin Estado";
    }

    public String obtenerClaseCSSEstado() {
        String[] clases = {"text-bg-info", "text-bg-success", "text-bg-danger"};
        if (estado >= 0 && estado < 3) {
            return clases[estado];
        }
        return "text-bg-secondary";
    }
}
