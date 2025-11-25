package com.proyecto.PlayApp.Controller.Reseña;

import com.proyecto.PlayApp.entity.Resena;
import com.proyecto.PlayApp.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.ProductoService;
import com.proyecto.PlayApp.service.UsuarioService;
import com.proyecto.PlayApp.util.PaginationMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/resena")
@RequiredArgsConstructor
public class ResenaController {
    private final CarritoService carrito;
    private final ResenaRepository resenaRepository;
    private final UsuarioService usuarios;

    @GetMapping
    public String mostrarFormulario(Model model, Principal usuario) {

        List<Resena> todas = resenaRepository.findAllByOrderByFechaDesc();

        // Tomar solo las 2 últimas
        List<Resena> ultimasDos = todas.stream()
                .limit(2)
                .toList();

        model.addAttribute("reviews", ultimasDos);
        model.addAttribute("nuevaResena", new Resena());
        model.addAttribute("carrito", numeroItemsCarrito(usuario));

        String nombreusuario = "";
        if(usuario != null){
            nombreusuario = usuarios.buscarUsuario(usuario.getName()).getNombreCompleto();
        }
        model.addAttribute("nombreUsuario", nombreusuario);

        return "contacto";
    }


    private int numeroItemsCarrito(Principal user){
        if(user == null){
            return 0;
        }
        return carrito.listarCarrito(user.getName()).size();
    }


    @PostMapping("/submit-review")
    public String guardarResena(@ModelAttribute("nuevaResena") Resena nuevaResena) {
        resenaRepository.save(nuevaResena);
        return "redirect:/resena";
    }
}