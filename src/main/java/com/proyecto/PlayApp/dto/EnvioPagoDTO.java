package com.proyecto.PlayApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioPagoDTO {
    private Double latitud;
    private Double longitud;
    private String direccion;
    private Integer mesa;
    private String descripcion;
    private String metodoPago;
    private Double valor;
}
