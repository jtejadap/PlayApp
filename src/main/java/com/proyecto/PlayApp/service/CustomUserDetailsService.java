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
import java.util.List;
import java.util.Set;
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
        Usuario usuario = usuarioRepository.findUsuarioByCorreo(mail);
        if(usuario == null) {
            System.out.println("Usuario no encontrado: " + mail);
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        System.out.println("Usuario encontrado: " + usuario.getNombreCompleto());
        List<String> roles = new ArrayList<>();
        roles.add(usuario.getRol());

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }
}
