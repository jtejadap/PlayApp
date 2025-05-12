package com.proyecto.PlayApp.dto;

import com.proyecto.PlayApp.entity.PedidoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallesDTO {
    private List<PedidoItem> carrito;
    private List<String> restaurantes;
}
