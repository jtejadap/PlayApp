package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Pago;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.EnvioRepository;
import com.proyecto.PlayApp.repository.PagoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository pagos;
    private final UsuarioRepository usuarios;

    public Pago crearPago(Pago pago) {

        Pago nuevoPago = Pago.builder()
                .valor(pago.getValor())
                .metodo(pago.getMetodo())
                .estado(0)
                .usuario(pago.getUsuario())
                .fecha(LocalDateTime.now())
                .build();

        return pagos.save(nuevoPago);
    }

    public Pago actualizarEstadoPago(Long id, Integer estado) {
        Pago pago = pagos.findById(id).orElse(new Pago());
        pago.setEstado(estado);
        return pagos.save(pago);
    }
}
