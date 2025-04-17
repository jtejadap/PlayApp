package com.proyecto.PlayApp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem implements Serializable {
    private String productoId;
    private String nombre;
    private int cantidad;
    private double precio;
}
