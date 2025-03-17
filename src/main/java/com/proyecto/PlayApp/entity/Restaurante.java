package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private String telefono;
    private String password;
    @Column(name = "correoRestaurante", nullable = false, unique = true)
    private String correoRestaurante;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plato> platos;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bebida> bebidas;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servicio> servicios;

    // Constructor por defecto
    public Restaurante() {
    }

    // Constructor con parámetros
    public Restaurante(String nombre, String direccion, String telefono, String password, String correoRestaurante) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.password = password;
        this.correoRestaurante= correoRestaurante;
    }

    // Getters y setters
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String contrasena) {
        this.password = contrasena;
    }

    public List<Plato> getPlatos() {
        return platos;
    }

    public void setPlatos(List<Plato> platos) {
        this.platos = platos;
    }

    public List<Bebida> getBebidas() {
        return bebidas;
    }

    public void setBebidas(List<Bebida> bebidas) {
        this.bebidas = bebidas;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public String getCorreoRestaurante() {
        return correoRestaurante;
    }

    public void setCorreoRestaurante(String correoRestaurante) {
        this.correoRestaurante = correoRestaurante;
    }

    // Método toString


    @Override
    public String toString() {
        return "Restaurante{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", password='" + password + '\'' +
                ", correoRestaurante='" + correoRestaurante + '\'' +
                ", platos=" + platos +
                ", bebidas=" + bebidas +
                ", servicios=" + servicios +
                '}';
    }
}