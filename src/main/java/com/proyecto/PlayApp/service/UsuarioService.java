package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Roles;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.RolesRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository users;
    private final RolesRepository roles;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.users = usuarioRepository;
        this.roles = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crearUsuario(Usuario formulario){
        formulario.setPassword(passwordEncoder.encode(formulario.getPassword()));
        formulario.setRol("ROLE_USER");
        return users.save(formulario);
    }

    public Usuario createRestaurant(Usuario formulario){
        formulario.setPassword(passwordEncoder.encode(formulario.getPassword()));
        formulario.setRol("ROLE_ADMIN");
        return users.save(formulario);
    }

    public Usuario buscarUsuario(String mail){
        return users.findUsuarioByCorreo(mail);
    }


}
