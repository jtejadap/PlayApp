package com.proyecto.PlayApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "reseñas") // Nombre de la colección en MongoDB
public class Resena {
    @Id
    private String id;
    private String nombre;
    private int valoracion;
    private String comentario;
    private Date fecha = new Date(); // Fecha automática

    // Getters y Setters (¡IMPORTANTE!)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getValoracion() { return valoracion; }
    public void setValoracion(int valoracion) { this.valoracion = valoracion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}