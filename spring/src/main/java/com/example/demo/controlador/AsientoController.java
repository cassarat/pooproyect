package com.example.demo.controlador;
import com.example.demo.modelo.Asiento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    // Simulamos la sala con un mapa (puedes conectarlo a tu clase Sala.java luego)
    private Map<String, Asiento> salaTemporal = new HashMap<>();

    public AsientoController() {
        // Datos de prueba paea probar
        salaTemporal.put("A1", new Asiento("A1", 3.5, 45.0, "Media"));
        salaTemporal.put("C5", new Asiento("C5", 8.0, 0.0, "Alta")); // Mejor asiento
        salaTemporal.put("F10", new Asiento("F10", 15.0, 30.0, "Baja"));
    }

    // ENDPOINT NUEVO 
    @GetMapping("/{id}/caracteristicas")
    public ResponseEntity<Asiento> obtenerCaracteristicas(@PathVariable String id) {
        Asiento asiento = salaTemporal.get(id.toUpperCase());
        
        if (asiento != null) {
            return ResponseEntity.ok(asiento); // Si lo encuentra, envía los datos 
        } else {
            return ResponseEntity.notFound().build(); // Si no, error 404
        }
    }
}