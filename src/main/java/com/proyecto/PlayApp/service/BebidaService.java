package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.TipoBebida;
import com.proyecto.PlayApp.repository.BebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BebidaService {

    @Autowired
    private BebidaRepository bebidaRepository;

    public List<Bebida> obtenerBebidasPorTipo(TipoBebida tipo) {
        return bebidaRepository.findByTipo(tipo);
    }

    // Agregar una nueva bebida
    public Bebida agregarBebida(Bebida bebida) {
        return bebidaRepository.save(bebida);
    }

    // Actualizar una bebida existente
    public Bebida actualizarBebida(Long id, Bebida detalles) {
        Bebida bebida = bebidaRepository.findById(id).orElseThrow(() -> new RuntimeException("Bebida no encontrada con ID: " + id));
        bebida.setNombre(detalles.getNombre());
        bebida.setPrecio(detalles.getPrecio());
        bebida.setStock(detalles.getStock());
        bebida.setDescripcion(detalles.getDescripcion());
        bebida.setTipo(detalles.getTipo());
        bebida.setImagen(detalles.getImagen());
        return bebidaRepository.save(bebida);
    }

    // Eliminar una bebida
    public void eliminarBebida(Long id) {
        if (!bebidaRepository.existsById(id)) {
            throw new RuntimeException("Bebida no encontrada con ID: " + id);
        }
        bebidaRepository.deleteById(id);
    }

    // Obtener todas las bebidas
    public List<Bebida> obtenerTodasLasBebidas() {
        return bebidaRepository.findAll();
    }

    // Obtener una bebida por su ID
    public Optional<Bebida> obtenerBebidaPorId(Long id) {
        return bebidaRepository.findById(id);
    }
}
