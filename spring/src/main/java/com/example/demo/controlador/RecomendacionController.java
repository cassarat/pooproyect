package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import com.example.demo.servicio.RecomendadorAsientos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {
    private final RecomendadorAsientos recomendador;
    private final Sala sala;

    public RecomendacionController(RecomendadorAsientos recomendador) {
        this.recomendador = recomendador;
        this.sala = new Sala();
    }

    @GetMapping("/mejores")
    public ResponseEntity<?> recomendar(@RequestParam int cantidad) {
        try {
            List<Asiento> recomendados = recomendador.recomendar(
                    sala.mostrarAsientos(),
                    cantidad
            );

            if (recomendados.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "mensaje",
                        "No hay suficientes asientos consecutivos disponibles.",
                        "asientos",
                        recomendados
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje",
                    "Estos son los mejores asientos disponibles.",
                    "asientos",
                    recomendados
            ));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", error.getMessage())
            );
        }
    }
}
