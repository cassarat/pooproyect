package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import com.example.demo.modelo.VistaAsiento;
import com.example.demo.servicio.VistaAsientoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/vista-asiento")
public class VistaAsientoController {
    private final Sala sala;
    private final VistaAsientoService servicio;

    public VistaAsientoController(Sala sala, VistaAsientoService servicio) {
        this.sala = sala;
        this.servicio = servicio;
    }

    @GetMapping("/asientos")
    public List<Asiento> listarAsientos() {
        return sala.mostrarAsientos();
    }

    @GetMapping
    public VistaAsiento consultar(@RequestParam String fila, @RequestParam int numero) {
        if (fila == null || !fila.trim().matches("[a-zA-Z]+") || numero < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica una fila y un número válidos.");
        }
        Asiento asiento = sala.buscarAsiento(fila.trim(), numero);
        if (asiento == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El asiento no existe.");
        }
        if (!asiento.isDisponible()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El asiento está ocupado.");
        }
        try {
            return servicio.calcular(asiento);
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, error.getMessage());
        }
    }
}
