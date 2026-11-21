package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import com.example.demo.servicio.CarteleraService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {
    private final Sala sala;
    private final CarteleraService cartelera;

    public BoletoController(Sala sala, CarteleraService cartelera) {
        this.sala = sala;
        this.cartelera = cartelera;
    }

    public record Compra(String peliculaId, String horario, List<String> asientos,
            String nombreCliente, String correo) {}

    @PostMapping("/compra")
    public ResponseEntity<?> simularCompra(@RequestBody Compra datos) {
        try {
            if (datos == null) throw new IllegalArgumentException("Completa los datos de la compra.");
            var pelicula = cartelera.buscar(datos.peliculaId());
            if (!pelicula.horarios().contains(datos.horario())) {
                throw new IllegalArgumentException("El horario no corresponde a la película.");
            }
            List<Asiento> confirmados = sala.confirmarSeleccion(datos.asientos());
            String cliente = datos.nombreCliente() == null || datos.nombreCliente().isBlank()
                    ? "Cliente Cine Nova" : datos.nombreCliente().trim();
            return ResponseEntity.ok(Map.of("exito", true, "reserva", Map.of(
                    "codigoReserva", "CN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    "pelicula", pelicula.titulo(), "horario", datos.horario(),
                    "asientos", confirmados.stream().map(Asiento::getId).toList(), "cliente", cliente,
                    "totalPagar", pelicula.precio().multiply(BigDecimal.valueOf(confirmados.size())))));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(Map.of("exito", false, "error", error.getMessage()));
        } catch (IllegalStateException error) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("exito", false, "error", error.getMessage()));
        }
    }
}
