package com.proyecto.PlayApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "mensajes_contacto") // Nombre de la colección en MongoDB
public class Mensaje {
    @Id
    private String id;

    private String nombre;
    private String email;
    private String asunto; // "reserva", "consulta", "sugerencia"
    private String mensaje;
    private Date fechaEnvio = new Date(); // Fecha automática


    // Getters y Setters (generados automáticamente o con Lombok)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Date getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(Date fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}