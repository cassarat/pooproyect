package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    private final Sala sala = new Sala();
    private Asiento asientoSeleccionado;

    @GetMapping("/mapa")
    public List<Asiento> cargarMapa() {
        return sala.mostrarAsientos();
    }

    @GetMapping("/seleccionar")
    public String seleccionarAsiento(@RequestParam String fila, @RequestParam int numero) {
        this.asientoSeleccionado = sala.buscarAsiento(fila, numero);
        if (this.asientoSeleccionado != null) {
            return "Asiento seleccionado: " + this.asientoSeleccionado.getFila() + this.asientoSeleccionado.getNumero();
        }
        return "Asiento no encontrado";
    }

    @GetMapping("/caracteristicas")
    public String consultarCaracteristicas() {
        if (this.asientoSeleccionado != null) {
            return this.asientoSeleccionado.obtenerCaracteristicas();
        }
        return "Ningún asiento seleccionado.";
    }
}