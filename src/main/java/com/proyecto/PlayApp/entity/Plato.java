package com.proyecto.PlayApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Plato extends MenuItem {

    private String descripcion;
    private int cantidad;

    @Enumerated(EnumType.STRING)
    private TipoPlato tipo;

    public Plato() {}

    public Plato(String nombre, String descripcion, Double precio, byte[] imagen, TipoPlato tipo, int cantidad, Restaurante restaurante) {
        super(nombre, precio, imagen, restaurante);
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    // Getters y setters para los campos específicos de Plato
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public TipoPlato getTipo() {
        return tipo;
    }

    public void setTipo(TipoPlato tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Plato{" + super.toString() +
                ", descripcion='" + descripcion + '\'' +
                ", tipo=" + tipo +
                ", cantidad=" + cantidad +
                '}';
    }
}
