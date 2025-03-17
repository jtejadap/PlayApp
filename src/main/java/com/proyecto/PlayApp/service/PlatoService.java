package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Plato;
import com.proyecto.PlayApp.entity.TipoPlato;
import com.proyecto.PlayApp.repository.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    // Agregar un nuevo plato
    public Plato agregarPlato(Plato plato) {
        return platoRepository.save(plato);
    }

    public List<Plato> obtenerPlatosPorTipo(TipoPlato tipoPlato){
        return platoRepository.findByTipo(tipoPlato);
    }

    // Actualizar un plato existente
    public Plato actualizarPlato(Long id, Plato detalles) {
        Plato plato = platoRepository.findById(id).orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + id));
        plato.setNombre(detalles.getNombre());
        plato.setPrecio(detalles.getPrecio());
        plato.setCantidad(detalles.getCantidad());
        plato.setDescripcion(detalles.getDescripcion());
        plato.setTipo(detalles.getTipo());
        plato.setImagen(detalles.getImagen());
        return platoRepository.save(plato);
    }

    // Eliminar un plato
    public void eliminarPlato(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new RuntimeException("Plato no encontrado con ID: " + id);
        }
        platoRepository.deleteById(id);
    }

    // Obtener todos los platos
    public List<Plato> obtenerTodosLosPlatos() {
        return platoRepository.findAll();
    }

    // Obtener un plato por su ID
    public Optional<Plato> obtenerPlatoPorId(Long id) {
        return platoRepository.findById(id);
    }
}
