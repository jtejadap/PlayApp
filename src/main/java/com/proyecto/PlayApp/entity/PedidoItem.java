package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;

@Entity
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

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

    private double precio;

    public PedidoItem() {
    }

    public PedidoItem(Long id, Pedido pedido, Plato plato, Bebida bebida, Servicio servicio, int cantidad, double precio) {
        this.id = id;
        this.pedido = pedido;
        this.plato = plato;
        this.bebida = bebida;
        this.servicio = servicio;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "PedidoItem{" +
                "id=" + id +
                ", pedido=" + pedido +
                ", plato=" + plato +
                ", bebida=" + bebida +
                ", servicio=" + servicio +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                '}';
    }
}
