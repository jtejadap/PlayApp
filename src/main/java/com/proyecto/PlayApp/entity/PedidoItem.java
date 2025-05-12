package com.proyecto.PlayApp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem {
    private Integer cantidad;
    private Producto producto;
    private Double subtotal;


}
