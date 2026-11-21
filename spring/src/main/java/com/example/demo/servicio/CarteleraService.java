package com.example.demo.servicio;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/** Tarifas y horarios compartidos por cartelera, recomendaciones y compra simulada. */
@Service
public class CarteleraService {
    public record Pelicula(String id, String titulo, String genero, String clasificacion,
            String duracion, String sinopsis, BigDecimal precio, String poster, List<String> horarios) {}

    private final List<Pelicula> peliculas = List.of(
        new Pelicula("parasite", "Parasite", "Suspenso/Comedia", "PG-13", "2h 15m",
            "La profunda desigualdad social y la lucha de clases a través de dos familias coreanas de mundos opuestos.",
            new BigDecimal("7.50"), "/images/poster1.jpg", List.of("15:30", "18:15", "21:00")),
        new Pelicula("ElPianista", "El pianista", "Bélico/Drama", "R", "1h 50m",
            "Las memorias del músico polaco Władysław Szpilman durante la Segunda Guerra Mundial.",
            new BigDecimal("7.00"), "/images/poster2.jpg", List.of("16:00", "19:30", "22:15"))
    );

    public List<Pelicula> listar() { return peliculas; }

    public Pelicula buscar(String id) {
        return peliculas.stream().filter(pelicula -> pelicula.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La película no existe en la cartelera."));
    }
}
