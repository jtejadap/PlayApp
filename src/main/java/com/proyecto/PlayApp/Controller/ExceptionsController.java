package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.Principal;


@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionsController {
    public static final String DEFAULT_ERROR_VIEW = "errorviews/internal-error";
    private final UsuarioService servicio;

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(
            HttpServletRequest req,
            Exception e,
            Principal principal
    ) {
        ModelAndView view = new ModelAndView();


        view.addObject("exception", e);
        view.addObject("url", req.getRequestURL());
        view.addObject("link", servicio.getHomePath(principal));

        view.setViewName(DEFAULT_ERROR_VIEW);
        return view;
    }


    @ExceptionHandler(value = NoResourceFoundException.class)
    public ModelAndView noResourceFoundErrorHandler(
            HttpServletRequest req,
            Exception e,
            Principal principal
    ) {
        ModelAndView view = new ModelAndView();

        view.addObject("exception", e.getMessage());
        view.addObject("url", req.getRequestURL());
        view.addObject("link", servicio.getHomePath(principal));

        view.setViewName("errorviews/not-found");
        return view;
    }
}
