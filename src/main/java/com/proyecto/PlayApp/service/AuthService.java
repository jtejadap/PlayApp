package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Restaurante;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.RestauranteRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Autenticación de Restaurante
    public Optional<Restaurante> authenticateRestaurante(String correoRestaurante, String password) {
        Optional<Restaurante> restaurante = restauranteRepository.findByCorreoRestaurante(correoRestaurante);
        if (restaurante.isPresent() && restaurante.get().getPassword().equals(password)) {
            return restaurante;
        }
        return Optional.empty();
    }

    // Verificar si el correo de restaurante ya está en uso
    public boolean existePorCorreoRestaurante(String correoRestaurante) {
        return restauranteRepository.findByCorreoRestaurante(correoRestaurante).isPresent();
    }

    // Autenticación de Usuario (sin cambios)
    public Optional<Usuario> authenticateUsuario(String correo, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario;
        }
        return Optional.empty();
    }

    // Registro de Usuario (sin cambios)
    public void registrarUsuario(Usuario usuario) throws Exception {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new Exception("El correo ya está en uso.");
        }
        usuarioRepository.save(usuario);
    }
}
