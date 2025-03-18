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

    public Usuario createUser(Usuario formulario){
        formulario.setPassword(passwordEncoder.encode(formulario.getPassword()));
        Usuario newUser = users.save(formulario);
        Roles rol = new Roles(newUser.getId(), "ROLE_USER");
        roles.save(rol);
        return newUser;
    }

    public Usuario createRestaurant(Usuario formulario){
        formulario.setPassword(passwordEncoder.encode(formulario.getPassword()));
        Usuario newUser = users.save(formulario);
        Roles rol = new Roles(newUser.getId(), "ROLE_ADMIN");
        roles.save(rol);
        return newUser;
    }


}
