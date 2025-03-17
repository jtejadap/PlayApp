package com.proyecto.PlayApp.entity;

import jakarta.persistence.Entity;

@Entity
public class Servicio extends MenuItem {

    private String descripcion;

    public Servicio() {}

    public Servicio(String nombre, String descripcion, Double precio, byte[] imagen, Restaurante restaurante) {
        super(nombre, precio, imagen, restaurante);
        this.descripcion = descripcion;
    }

    // Getters y setters para el campo específico de Servicio
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Servicio{" + super.toString() +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}

