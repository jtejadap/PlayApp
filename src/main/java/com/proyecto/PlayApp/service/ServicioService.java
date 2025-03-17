package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Servicio;
import com.proyecto.PlayApp.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    // Agregar un nuevo servicio
    public Servicio agregarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    // Actualizar un servicio existente
    public Servicio actualizarServicio(Long id, Servicio detalles) {
        // Verifica si el servicio existe
        Servicio servicio = servicioRepository.findById(id).orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        // Actualiza los atributos del servicio
        servicio.setNombre(detalles.getNombre());
        servicio.setDescripcion(detalles.getDescripcion());
        servicio.setPrecio(detalles.getPrecio());
        servicio.setImagen(detalles.getImagen());
        // Guarda el servicio actualizado
        return servicioRepository.save(servicio);
    }

    // Eliminar un servicio
    public void eliminarServicio(Long id) {
        // Verifica si el servicio existe antes de eliminarlo
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    // Obtener todos los servicios
    public List<Servicio> obtenerTodosLosServicios() {
        return servicioRepository.findAll();
    }

    // Obtener un servicio por su ID
    public Optional<Servicio> obtenerServicioPorId(Long id) {
        return servicioRepository.findById(id);
    }
}
