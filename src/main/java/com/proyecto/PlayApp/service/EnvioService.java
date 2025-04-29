package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.EnvioRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnvioService {
    private final EnvioRepository envios;
    private final UsuarioRepository usuarios;

    public Envio crearEnvio(Envio envio) {

        Envio nuevoEnvio = Envio.builder()
                .latitud(envio.getLatitud())
                .longitud(envio.getLongitud())
                .dirreccion(envio.getDirreccion())
                .descripcion(envio.getDescripcion())
                .carpa(envio.getCarpa())
                .mesa(envio.getMesa())
                .fecha(LocalDateTime.now())
                .usuario(envio.getUsuario())
                .build();

        return  envios.save(nuevoEnvio);
    }
}
