package com.proyecto.PlayApp.dto;

import lombok.Data;

@Data
public class PrediccionDTO {
    private String diaSemana;
    private String producto;
    private String categoria;
    private int precio;
    private String clima;
    private String temporada;
}