package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;

@Entity
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "plato_id")
    private Plato plato;

    @ManyToOne
    @JoinColumn(name = "bebida_id")
    private Bebida bebida;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    private int cantidad;

    public CarritoItem() {
    }

    public CarritoItem(Long id, Carrito carrito, Plato plato, Bebida bebida, Servicio servicio, int cantidad) {
        this.id = id;
        this.carrito = carrito;
        this.plato = plato;
        this.bebida = bebida;
        this.servicio = servicio;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "CarritoItem{" +
                "id=" + id +
                ", carrito=" + carrito +
                ", plato=" + plato +
                ", bebida=" + bebida +
                ", servicio=" + servicio +
                ", cantidad=" + cantidad +
                '}';
    }
}
