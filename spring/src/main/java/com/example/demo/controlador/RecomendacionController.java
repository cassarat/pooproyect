package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import com.example.demo.modelo.PreferenciasUsuario;
import com.example.demo.servicio.RecomendadorAsientos;
import com.example.demo.servicio.CarteleraService;
import java.math.BigDecimal;
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
    private final CarteleraService cartelera;

    public RecomendacionController(RecomendadorAsientos recomendador, Sala sala, CarteleraService cartelera) {
        this.recomendador = recomendador;
        this.sala = sala;
        this.cartelera = cartelera;
    }

    @GetMapping("/mejores")
    public ResponseEntity<?> recomendar(@RequestParam int cantidad,
            @RequestParam(defaultValue = "parasite") String peliculaId,
            @RequestParam(required = false) BigDecimal presupuesto) {
        try {
            var preferencias = new PreferenciasUsuario(cantidad, peliculaId, presupuesto);
            BigDecimal precioUnitario = cartelera.buscar(preferencias.peliculaId()).precio();
            BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            if (presupuesto != null && total.compareTo(presupuesto) > 0) {
                return ResponseEntity.ok(Map.of("mensaje", "El presupuesto no alcanza para el grupo.",
                        "asientos", List.of(), "precioUnitario", precioUnitario, "total", total));
            }
            List<Asiento> recomendados = recomendador.recomendar(
                    sala.mostrarAsientos(),
                    cantidad
            );

            if (recomendados.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "mensaje",
                        "No hay suficientes asientos consecutivos disponibles.",
                        "asientos",
                        recomendados, "precioUnitario", precioUnitario, "total", total
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje",
                    "Estos son los mejores asientos disponibles.",
                    "asientos",
                    recomendados, "precioUnitario", precioUnitario, "total", total
            ));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", error.getMessage())
            );
        }
    }
}
