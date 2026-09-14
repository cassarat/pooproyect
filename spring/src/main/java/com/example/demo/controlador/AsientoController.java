package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.Sala;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    private Sala sala = new Sala();
    private Asiento asientoSeleccionado;

    @GetMapping("/mapa")
    public List<Asiento> cargarMapa() {
        return sala.mostrarAsientos();
    }
}