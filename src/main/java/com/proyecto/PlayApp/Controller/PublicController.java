package com.proyecto.PlayApp.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PublicController {
    @GetMapping("/")
    public String showHome(){
        return "index";
    }

    @GetMapping("/inicio")
    public String goInicio(){
        return "index";
    }

    @GetMapping("/registro-plato")
    public String showFormAggPlato(){return "Restaurante/registro-plato";}

    @GetMapping("/editar-platos")
    public String showEdit(){
        return "Restaurante/editar-platos";
    }

    @GetMapping("/registro-bebida")
    public String showFormAggBebida(){
        return "Restaurante/registro-bebida";
    }

    @GetMapping("/registro-servicio")
    public String showFormAggServicio(){
        return "Restaurante/registro-servicio";
    }

}
