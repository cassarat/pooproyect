package com.example.demo.modelo;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** La disponibilidad del mapa, del recomendador y de la compra proviene de SQLite. */
@Component
public class Sala {
    private final AsientoRepository repositorio;

    public Sala(AsientoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Asiento> mostrarAsientos() {
        return repositorio.findAll().stream()
                .sorted(Comparator.comparing(Asiento::getFila).thenComparingInt(Asiento::getNumero))
                .toList();
    }

    public Asiento buscarAsiento(String fila, int numero) {
        if (fila == null) return null;
        return repositorio.findById(fila.trim().toUpperCase(Locale.ROOT) + numero).orElse(null);
    }

    /** Todos los asientos se ocupan juntos; un conflicto revierte la operación completa. */
    @Transactional
    public List<Asiento> confirmarSeleccion(List<String> ids) {
        if (ids == null || ids.isEmpty() || ids.size() > 70
                || ids.stream().anyMatch(id -> id == null || !id.trim().matches("(?i)[A-G](?:10|[1-9])"))) {
            throw new IllegalArgumentException("Selecciona entre 1 y 70 asientos válidos de la sala A-G.");
        }
        List<String> normalizados = ids.stream()
                .map(id -> id.trim().toUpperCase(Locale.ROOT)).sorted().toList();
        if (normalizados.stream().distinct().count() != normalizados.size()) {
            throw new IllegalArgumentException("La selección contiene asientos repetidos.");
        }

        // La comprobación y la escritura son una única sentencia SQL por asiento.
        // No se hace una lectura previa que pueda quedar desactualizada antes de escribir.
        for (String id : normalizados) {
            if (repositorio.ocuparSiDisponible(id) != 1) {
                throw new IllegalStateException("El asiento " + id + " ya no está disponible. Actualiza el mapa.");
            }
        }
        return repositorio.findAllById(normalizados).stream()
                .sorted(Comparator.comparing(Asiento::getFila).thenComparingInt(Asiento::getNumero))
                .toList();
    }

    public double getTamanoPantalla() { return 15.5; }
    public String getFilas() { return "A-G"; }
}
