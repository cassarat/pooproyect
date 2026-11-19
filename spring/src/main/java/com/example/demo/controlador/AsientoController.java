package com.example.demo.controlador;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.repositorio.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
@CrossOrigin(origins = "*") // Permite peticiones desde la interfaz HTML
public class AsientoController {

    @Autowired
    private AsientoRepository asientoRepository;

    // Retorna todos los asientos insertados en la base de datos
    @GetMapping
    public List<Asiento> obtenerTodos() {
        return asientoRepository.findAll();
    }

    // Retorna un asiento específico por su ID (ej. "A1", "C5", "F10")
    @GetMapping("/{id}")
    public Asiento obtenerPorId(@PathVariable String id) {
        return asientoRepository.findById(id).orElse(null);
    }
}
