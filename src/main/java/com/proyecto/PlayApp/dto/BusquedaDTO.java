package com.proyecto.PlayApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Float precio;
    private Float stock;
    private Integer categoria;
    private Integer page;
    private Integer size;
    private String sort;
}
