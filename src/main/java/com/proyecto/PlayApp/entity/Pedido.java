package com.proyecto.PlayApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer estado;
    private double total;
    private LocalDateTime timestamp;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<PedidoItem> productos;
    /*
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

     */
    @ManyToOne
    @JoinColumn(name = "pago_id")
    private Pago pago;
    @ManyToOne
    @JoinColumn(name = "envio_id")
    private Envio envio;

    public String obtenerNombreEstado() {
        String [] estados = {"Recibido","En preparación","Terminado"};
        if( estado>=0 && estado<3){
            return estados[estado];
        }
        return "Sin Estado";
    }

    public String obtenerClaseCSSEstado() {
        String [] clases = {"text-bg-primary","text-bg-warning","text-bg-success"};
        if( estado>=0 && estado<3){
            return clases[estado];
        }
        return "text-bg-secondary";
    }

}
