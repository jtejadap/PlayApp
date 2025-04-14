package com.proyecto.PlayApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltrosDTO {
    private String nombre;
    private Float precio;
    private Float stock;
    private Integer categoria;
}
