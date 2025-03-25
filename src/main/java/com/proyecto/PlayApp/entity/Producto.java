package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;

@Entity
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
}
