package com.proyecto.PlayApp.dto;

import com.proyecto.PlayApp.entity.CarritoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    private String correoUsuario;
    private List<CarritoItem> carrito;
    private EnvioPagoDTO envioPago;
}
