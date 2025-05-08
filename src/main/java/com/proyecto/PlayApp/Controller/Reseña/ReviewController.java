package com.proyecto.PlayApp.Controller.Reseña;

import com.proyecto.PlayApp.entity.Review;
import com.proyecto.PlayApp.repository.ReviewRepository;
import com.proyecto.PlayApp.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.security.Principal;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CarritoService carrito;


    @GetMapping("/contacto")
    public String showContacto(Principal session, Model model) {
        // Recupera las últimas 2 reseñas ordenadas por ID de forma descendente
        model.addAttribute("reviews", reviewRepository.findAll(
                PageRequest.of(0, 2, Sort.by(Sort.Order.desc("id")))).getContent());
        model.addAttribute("carrito", carrito.listarCarrito(session.getName()).size());
        return "contacto"; // Carga el archivo contacto.html
    }

    @PostMapping("/submit-review")
    public String submitReview(@ModelAttribute Review review) {
        reviewRepository.save(review);
        return "redirect:/contacto"; // Redirige a la página de contacto para ver las reseñas actualizadas
    }

    @GetMapping("/mejores-reseñas")
    public String showTopReviews(HttpSession session, Model model) {
        // Recupera las reseñas con mayor valoración, ordenadas en forma descendente
        model.addAttribute("topReviews", reviewRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Order.desc("valoracion")))).getContent());
        return "contacto"; // Mantiene el mismo HTML
    }

}
