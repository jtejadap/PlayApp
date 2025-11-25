package com.proyecto.PlayApp.dto;

import lombok.Data;

@Data
public class PrediccionDTO {

    private String fecha;
    private int diaSemana;
    private String producto;
    private String categoria;
    private double precio;
    private int cantidadVendida;
    private String clima;
    private String temporada;
}