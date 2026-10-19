package com.example.demo.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    @PostMapping("/compra")
    public ResponseEntity<Map<String, Object>> simularCompra(@RequestBody Map<String, Object> datosCompra) {
        // Simulamos que la compra siempre es exitosa
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);

        Map<String, Object> reserva = new HashMap<>();
        reserva.put("codigoReserva", "CN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        reserva.put("pelicula", "Película Seleccionada"); // Aquí iría la lógica real para buscar el nombre
        reserva.put("horario", datosCompra.get("horario"));
        reserva.put("asientos", datosCompra.get("asientos"));
        reserva.put("cliente", datosCompra.get("nombreCliente"));
        
        // Simulación de total: 7.50 * cantidad de asientos
        List<?> asientos = (List<?>) datosCompra.get("asientos");
        reserva.put("totalPagar", 7.50 * asientos.size()); 

        respuesta.put("reserva", reserva);

        return ResponseEntity.ok(respuesta);
    }
}