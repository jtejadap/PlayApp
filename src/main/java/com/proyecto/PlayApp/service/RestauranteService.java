package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Restaurante;
import com.proyecto.PlayApp.repository.BebidaRepository;
import com.proyecto.PlayApp.repository.PlatoRepository;
import com.proyecto.PlayApp.repository.RestauranteRepository;
import com.proyecto.PlayApp.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    // Método para obtener un restaurante por su correo (usuario)

    public Optional<Restaurante> obtenerRestaurantePorCorreo(String correoRestaurante){
        return restauranteRepository.findByCorreoRestaurante(correoRestaurante);
    }

    public void saveRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }

    // Método para registrar un nuevo restaurante
    public Restaurante registrarRestaurante(Restaurante restaurante) {
        Optional<Restaurante> existente = restauranteRepository.findByCorreoRestaurante(restaurante.getCorreoRestaurante());
        System.out.println("Buscando correo: " + restaurante.getCorreoRestaurante());  // Línea de depuración
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        return restauranteRepository.save(restaurante);
    }


    // Método para iniciar sesión (login)
    public Optional<Restaurante> login(String correoRestaurante, String password) {
        Optional<Restaurante> restaurante = restauranteRepository.findByCorreoRestaurante(correoRestaurante);

        // Verificar si la contraseña es correcta
        return restaurante.filter(r -> r.getPassword().equals(password));
    }

}
