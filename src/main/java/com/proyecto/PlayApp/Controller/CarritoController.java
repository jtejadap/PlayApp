package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.ItemDTO;
import com.proyecto.PlayApp.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CarritoController {
    private final CarritoService servicio;

    @GetMapping("/add/{item}")
    public String addItem(
            @PathVariable  Long item,
            @RequestParam(name = "page", defaultValue = "1") Integer cantidad,
            Principal usuario
    ) {
        servicio.agregar(
                ItemDTO.builder()
                        .itemId(item)
                        .cantidad(cantidad)
                        .correo(usuario.getName())
                        .build()
        );
        return "redirect:/shop";
    }
}
