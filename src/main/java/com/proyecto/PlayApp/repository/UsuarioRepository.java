package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    @Query("{correo:'?0'}")
    Usuario findUsuarioByCorreo(String correo);
}
