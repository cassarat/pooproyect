package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    private Sala sala;

    public AsientoController() {
        this.sala = new Sala();
    }

    // --- NUEVO ENDPOINT: Devuelve TODOS los asientos para dibujar el mapa ---
    @GetMapping
    public ResponseEntity<List<Asiento>> obtenerTodosLosAsientos() {
        // Usa el método mostrarAsientos() de tu clase Sala
        return ResponseEntity.ok(sala.mostrarAsientos());
    }

    // --- ENDPOINT ANTERIOR: Devuelve detalles de un asiento (Requisito Nathalia) ---
    @GetMapping("/{id}/caracteristicas")
    public ResponseEntity<Asiento> obtenerCaracteristicas(@PathVariable String id) {
        String fila = id.substring(0, 1).toUpperCase(); 
        int numero = Integer.parseInt(id.substring(1));

        Asiento asiento = sala.buscarAsiento(fila, numero);
        
        if (asiento != null) {
            return ResponseEntity.ok(asiento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}