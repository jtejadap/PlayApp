package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;

import java.util.Base64;

@MappedSuperclass
public abstract class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    @Lob
    private byte[] imagen;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    public MenuItem() {}

    public MenuItem(String nombre, Double precio,byte[] imagen, Restaurante restaurante) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.restaurante = restaurante;
    }

    // Getters y setters para los campos comunes
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }
    public String getImagenBase64() {
        return Base64.getEncoder().encodeToString(this.imagen);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", imagen='" + imagen + '\'' +
                ", restaurante=" + restaurante.getNombre() +
                '}';
    }
}
