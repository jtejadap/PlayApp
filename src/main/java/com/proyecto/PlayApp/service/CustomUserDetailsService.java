package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws
            UsernameNotFoundException {
        System.out.println("Intentando autenticar usuario: " + mail);
        Usuario usuario = usuarioRepository.findByCorreo(mail)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado en la base de datos.");
                    return new UsernameNotFoundException("Usuario no encontrado");
                });
        System.out.println("Usuario encontrado: " + usuario.getNombre_completo());
        return new User(
                usuario.getNombre_completo(),
                usuario.getPassword(),
                usuario.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }
}
