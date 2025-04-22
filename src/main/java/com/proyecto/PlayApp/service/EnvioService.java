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

    public Envio crearEnvio(String usermail,Envio envio) {
        Usuario usuario = usuarios.findByCorreo(usermail).orElse(new Usuario());

        Envio nuevoEnvio = Envio.builder()
                .latitud(envio.getLatitud())
                .longitud(envio.getLongitud())
                .dirreccion(envio.getDirreccion())
                .descripcion(envio.getDescripcion())
                .carpa(envio.getCarpa())
                .mesa(envio.getMesa())
                .fecha(LocalDateTime.now())
                .usuario(usuario)
                .build();

        return  envios.save(nuevoEnvio);
    }
}
