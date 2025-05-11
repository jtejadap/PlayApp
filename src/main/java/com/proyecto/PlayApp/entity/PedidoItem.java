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
@Table(name = "pedido_items")
public class PedidoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer cantidad;
    /*
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;*/
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;


}
