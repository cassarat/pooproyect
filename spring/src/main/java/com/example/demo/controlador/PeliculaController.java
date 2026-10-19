package com.example.demo.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PeliculaController {

    @GetMapping("/peliculas")
    public ResponseEntity<List<Map<String, Object>>> obtenerCartelera() {
        List<Map<String, Object>> peliculas = new ArrayList<>();

        // Película 1
        Map<String, Object> p1 = new HashMap<>();
        p1.put("id", "parasite");
        p1.put("titulo", "Parasite");
        p1.put("genero", "Suspenso/Comedia");
        p1.put("clasificacion", "PG-13");
        p1.put("duracion", "2h 15m");
        p1.put("sinopsis", "la profunda desigualdad social y la lucha de clases a través de la historia de dos familias coreanas de mundos opuestos");
        p1.put("precio", 7.50);
        p1.put("poster", "/images/poster1.jpg"); 
        p1.put("horarios", List.of("15:30", "18:15", "21:00"));
        peliculas.add(p1);

        // Película 2
        Map<String, Object> p2 = new HashMap<>();
        p2.put("id", "ElPianista");
        p2.put("titulo", "El pianista");
        p2.put("genero", "Bélico/Drama");
        p2.put("clasificacion", "R");
        p2.put("duracion", "1h 50m");
        p2.put("sinopsis", "las memorias del músico polaco Władysław Szpilman, se desarrolla durante la Segunda Guerra Mundial");
        p2.put("precio", 7.00);
        p2.put("poster", "/images/poster2.jpg"); 
        p2.put("horarios", List.of("16:00", "19:30", "22:15"));
        peliculas.add(p2);

        return ResponseEntity.ok(peliculas);
    }
}