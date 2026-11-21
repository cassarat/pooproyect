package com.example.demo.controlador;

import com.example.demo.servicio.CarteleraService;
import com.example.demo.servicio.CarteleraService.Pelicula;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PeliculaController {
    private final CarteleraService cartelera;

    public PeliculaController(CarteleraService cartelera) { this.cartelera = cartelera; }

    @GetMapping("/peliculas")
    public List<Pelicula> obtenerCartelera() { return cartelera.listar(); }
}
