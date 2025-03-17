package com.proyecto.PlayApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Bebida extends MenuItem {

    private int stock;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private TipoBebida tipo;

    public Bebida() {}

    public Bebida(String nombre,String descripcion, Double precio, byte[] imagen, Restaurante restaurante, int stock, TipoBebida tipo) {
        super(nombre, precio, imagen, restaurante);
        this.descripcion = descripcion;
        this.stock = stock;
        this.tipo = tipo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public TipoBebida getTipo() {
        return tipo;
    }

    public void setTipo(TipoBebida tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Bebida{" +
                "stock=" + stock +
                ", descripcion='" + descripcion + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}

