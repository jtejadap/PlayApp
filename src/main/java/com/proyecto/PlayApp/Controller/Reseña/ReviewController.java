package com.proyecto.PlayApp.Controller.Reseña;

import com.proyecto.PlayApp.entity.Review;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class ReviewController {


    


    @PostMapping("/submit-review")
    public String submitReview(@ModelAttribute Review review) {
        //reviewRepository.save(review);
        return "redirect:/contacto"; // Redirige a la página de contacto para ver las reseñas actualizadas
    }

    @GetMapping("/mejores-reseñas")
    public String showTopReviews(HttpSession session, Model model) {
        // Recupera las reseñas con mayor valoración, ordenadas en forma descendente
        /*
        model.addAttribute("topReviews", reviewRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Order.desc("valoracion")))).getContent());
         */
        return "contacto"; // Mantiene el mismo HTML
    }

}
