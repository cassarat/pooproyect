package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    private Sala sala;

    public AsientoController() {
        // Inicializamos tu sala real para que cargue los asientos A1, A2 y B1
        this.sala = new Sala();
    }

    // ENDPOINT (Requisito Nathalia)
    @GetMapping("/{id}/caracteristicas")
    public ResponseEntity<Asiento> obtenerCaracteristicas(@PathVariable String id) {
        // El frontend manda "A1". Separamos la letra (fila) y el número
        String fila = id.substring(0, 1).toUpperCase(); 
        int numero = Integer.parseInt(id.substring(1));

        // Usamos tu método buscarAsiento que está en Sala.java
        Asiento asiento = sala.buscarAsiento(fila, numero);
        
        if (asiento != null) {
            return ResponseEntity.ok(asiento); // Envía los datos
        } else {
            return ResponseEntity.notFound().build(); // Error 404 si no existe
        }
    }
}
