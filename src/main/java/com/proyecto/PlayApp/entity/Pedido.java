package com.proyecto.PlayApp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    private String id;
    private Integer estado;
    private Double total;
    private LocalDateTime timestamp;
    private List<PedidoItem> carrito;
    private Usuario cliente;
    private Pago pago;
    private Envio envio;

    public String obtenerNombreEstado() {
        String[] estados = {"Recibido", "En preparación", "Terminado"};
        if (estado >= 0 && estado < 3) {
            return estados[estado];
        }
        return "Sin Estado";
    }

    public String obtenerClaseCSSEstado() {
        String[] clases = {"text-bg-primary", "text-bg-warning", "text-bg-success"};
        if (estado >= 0 && estado < 3) {
            return clases[estado];
        }
        return "text-bg-secondary";
    }
}
