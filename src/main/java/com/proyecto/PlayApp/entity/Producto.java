package com.proyecto.PlayApp.entity;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Document(collection = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Double stock;
    private Integer tipo;
    private Integer categoria;
    private Binary imagen;
    private Usuario entidad;

    public String obtenerNombreCategoria() {
         return switch (categoria) {
            case 1 -> "Comida";
            case 2 -> "Bebida";
            case 3 -> "Servicio";
            default -> "Sin asignar";
        };
    }

    public String obtenerClaseCategoria() {
        return switch (categoria) {
            case 1 -> "text-bg-primary";
            case 2 -> "text-bg-warning";
            case 3 -> "text-bg-success";
            default -> "text-bg-secondary";
        };
    }

    public String getImagenBase64() {
        return Base64.getEncoder().encodeToString(imagen.getData());
    }
}
