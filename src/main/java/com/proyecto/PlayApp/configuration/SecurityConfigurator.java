package com.proyecto.PlayApp.configuration;

import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.service.CustomUserDetailsService;
import com.proyecto.PlayApp.util.CustomAccessDeniedHandler;
import com.proyecto.PlayApp.util.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurator {
    public String[] pathsToStaticResources = {
            "/resources/**",
            "/static/**",
            "/css/**",
            "/img/**",
            "/js/**",
            "/vendor/**",
            "/fonts/**",
            "/static/favicon.ico",
            "/favicon.ico"
    };

    public String[] pathsToUserEndpoints = {
            "/user/**",
            "/cart/**",
            "/shipment/**",
            "/payment/**"
    };

    public String[] pathsToAdminEndpoints = {
            "/manager/**",
    };


    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return new CustomUserDetailsService(usuarioRepository);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
        DaoAuthenticationProvider es el encargado de autenticar usuarios.
        Usa el UserDetailsService para cargar los usuarios desde la base de datos.
        Compara la contraseña ingresada con la encriptada usando BCrypt.
    */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    /*
        Crea el AuthenticationManager, que maneja la autenticación.
        Usa el proveedor de autenticación (DaoAuthenticationProvider) para verificar usuarios.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws
            Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider(userDetailsService,
                        passwordEncoder))
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(pathsToUserEndpoints).hasRole("USER")
                        .requestMatchers(pathsToAdminEndpoints).hasRole("ADMIN")
                        .requestMatchers(pathsToStaticResources).permitAll()
                        .requestMatchers("/shop/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll()
                ).exceptionHandling(ex->ex.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
        }
}
