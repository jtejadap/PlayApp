package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String descripcion;
    private float precio;
    private float stock;
    private int tipo;
    private int categoria;

    public String obtenerNombreCategoria() {
         return switch (categoria) {
            case 1 -> "Comida";
            case 2 -> "Bebida";
            case 3 -> "Servicio";
            default -> "Sin asignar";
        };
    }

    public String obtenerClaseCategoria() {
        return switch (categoria) {
            case 1 -> "text-bg-primary";
            case 2 -> "text-bg-warning";
            case 3 -> "text-bg-success";
            default -> "text-bg-secondary";
        };
    }
}
