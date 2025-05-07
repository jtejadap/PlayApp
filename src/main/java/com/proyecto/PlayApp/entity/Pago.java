package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String metodo;
    private double valor;
    private Integer estado;
    private LocalDateTime fecha;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public String obtenerNombreEstado() {
        String [] estados = {"En espera","Terminado","Cancelado"};
        if( estado>=0 && estado<3){
            return estados[estado];
        }
        return "Sin Estado";
    }

    public String obtenerClaseCSSEstado() {
        String [] clases = {"text-bg-info","text-bg-success","text-bg-danger"};
        if( estado>=0 && estado<3){
            return clases[estado];
        }
        return "text-bg-secondary";
    }
}
