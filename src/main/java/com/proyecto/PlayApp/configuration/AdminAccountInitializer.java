package com.proyecto.PlayApp.configuration;

import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    @Value("${playapp.admin.email:adminplayapp01@gmail.com}")
    private String adminEmail;
    @Value("${playapp.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Usuario admin = usuarios.findUsuarioByCorreo(adminEmail);
        if (admin == null) {
            usuarios.save(Usuario.builder()
                    .nombreCompleto("Administrador PlayApp")
                    .correo(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .rol("ROLE_ADMIN")
                    .build());
            return;
        }

        admin.setNombreCompleto(admin.getNombreCompleto() == null || admin.getNombreCompleto().isBlank()
                ? "Administrador PlayApp"
                : admin.getNombreCompleto());
        admin.setRol("ROLE_ADMIN");
        if (admin.getPassword() == null || !passwordEncoder.matches(adminPassword, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
        }
        usuarios.save(admin);
    }
}
