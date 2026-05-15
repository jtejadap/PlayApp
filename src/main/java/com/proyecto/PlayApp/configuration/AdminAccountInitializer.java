package com.proyecto.PlayApp.configuration;

import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {
    private static final String ADMIN_PASSWORD = "admin123";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Usuario admin = usuarios.findUsuarioByCorreo(ReporteService.ADMIN_GLOBAL_CORREO);
        if (admin == null) {
            usuarios.save(Usuario.builder()
                    .nombreCompleto("Administrador PlayApp")
                    .correo(ReporteService.ADMIN_GLOBAL_CORREO)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .rol("ROLE_ADMIN")
                    .build());
            return;
        }

        admin.setNombreCompleto(admin.getNombreCompleto() == null || admin.getNombreCompleto().isBlank()
                ? "Administrador PlayApp"
                : admin.getNombreCompleto());
        admin.setRol("ROLE_ADMIN");
        if (admin.getPassword() == null || !passwordEncoder.matches(ADMIN_PASSWORD, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        }
        usuarios.save(admin);
    }
}
