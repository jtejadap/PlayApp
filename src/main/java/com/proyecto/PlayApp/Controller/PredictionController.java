package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.PrediccionDTO;
import com.proyecto.PlayApp.service.WekaPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager/prediccion")  
public class PredictionController {

    private final WekaPredictionService wekaPredictionService;

    @PostMapping("/predecir")
    @ResponseBody
    public ResponseEntity<?> predecir(@RequestBody PrediccionDTO dto) {

        double resultado = wekaPredictionService.predecirVenta(
                dto.getDiaSemana(),
                dto.getProducto(),
                dto.getCategoria(),
                dto.getPrecio(),
                dto.getClima(),
                dto.getTemporada()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("prediccion", resultado);

        return ResponseEntity.ok(response);
    }
}
