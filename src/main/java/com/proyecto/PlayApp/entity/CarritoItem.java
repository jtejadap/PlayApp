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
    @JoinColumn(name = "bebida_id")
    private Bebida bebida;

    private int cantidad;

    public CarritoItem() {
    }

    public CarritoItem(Long id, Carrito carrito,  Bebida bebida,  int cantidad) {
        this.id = id;
        this.carrito = carrito;
        this.bebida = bebida;
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


    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
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
                ", plato=" +
                ", bebida=" + bebida +
                ", servicio="  +
                ", cantidad=" + cantidad +
                '}';
    }
}
