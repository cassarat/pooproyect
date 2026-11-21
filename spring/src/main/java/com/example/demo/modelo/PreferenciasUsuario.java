package com.example.demo.modelo;

import java.math.BigDecimal;

/** El presupuesto es el total del grupo, en los mismos dólares que usa la cartelera. */
public record PreferenciasUsuario(int cantidadPersonas, String peliculaId, BigDecimal presupuesto) {
    public PreferenciasUsuario {
        if (cantidadPersonas < 1 || cantidadPersonas > 10) {
            throw new IllegalArgumentException("Indica entre 1 y 10 personas para sentarlas en la misma fila.");
        }
        if (presupuesto != null && presupuesto.signum() < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo.");
        }
        if (peliculaId == null || peliculaId.isBlank()) peliculaId = "parasite";
    }
}
