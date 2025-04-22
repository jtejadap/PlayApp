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

    public Pago crearPago(String userMail, Pago pago) {
        Usuario usuario = usuarios.findByCorreo(userMail).orElse(new Usuario());

        Pago nuevoPago = Pago.builder()
                .valor(pago.getValor())
                .metodo("PAYPAL")
                .estado(0)
                .usuario(usuario)
                .fecha(LocalDateTime.now())
                .build();

        return pagos.save(nuevoPago);
    }
}
