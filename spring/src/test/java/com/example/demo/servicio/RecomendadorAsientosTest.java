package com.example.demo.servicio;

import com.example.demo.modelo.Asiento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecomendadorAsientosTest {
    private final RecomendadorAsientos recomendador = new RecomendadorAsientos();

    @Test
    void recomiendaElAsientoDisponibleConMejorCalificacion() {
        List<Asiento> resultado = recomendador.recomendar(List.of(
                asiento("A", 1, true, 3),
                asiento("B", 1, true, 5)
        ), 1);

        assertEquals("B", resultado.get(0).getFila());
    }

    @Test
    void recomiendaAsientosConsecutivosDeLaMismaFila() {
        List<Asiento> resultado = recomendador.recomendar(List.of(
                asiento("A", 1, true, 5),
                asiento("A", 2, true, 5),
                asiento("B", 1, true, 4)
        ), 2);

        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getNumero());
        assertEquals(2, resultado.get(1).getNumero());
    }

    @Test
    void nuncaIncluyeAsientosOcupados() {
        List<Asiento> resultado = recomendador.recomendar(List.of(
                asiento("A", 1, false, 5),
                asiento("A", 2, true, 4)
        ), 1);

        assertEquals(2, resultado.get(0).getNumero());
    }

    @Test
    void devuelveListaVaciaCuandoNoHayGrupoDisponible() {
        List<Asiento> resultado = recomendador.recomendar(List.of(
                asiento("A", 1, true, 5),
                asiento("A", 3, true, 5)
        ), 2);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void unOcupadoEnMedioImpideFormarUnGrupo() {
        assertTrue(recomendador.recomendar(List.of(
                asiento("A", 1, true, 5), asiento("A", 2, false, 5),
                asiento("A", 3, true, 5)), 2).isEmpty());
    }

    @Test
    void utilizaOtroGrupoCuandoElMejorEstaOcupado() {
        var resultado = recomendador.recomendar(List.of(
                asiento("A", 1, false, 5), asiento("A", 2, false, 5),
                asiento("B", 1, true, 4), asiento("B", 2, true, 4)), 2);
        assertEquals(List.of("B1", "B2"), resultado.stream().map(Asiento::getId).toList());
    }

    @Test
    void comparaAnguloFrontalConCeroGrados() {
        Asiento frontal = new Asiento("A", 1, true, 6.5, 0, 5, "");
        Asiento lateral = new Asiento("B", 1, true, 6.5, 45, 5, "");
        assertEquals("A1", recomendador.recomendar(List.of(lateral, frontal), 1).get(0).getId());
    }

    @Test
    void salaVaciaLlenaYCantidadesInvalidas() {
        assertTrue(recomendador.recomendar(null, 1).isEmpty());
        assertTrue(recomendador.recomendar(List.of(), 1).isEmpty());
        assertTrue(recomendador.recomendar(List.of(asiento("A", 1, false, 5)), 1).isEmpty());
        for (int cantidad : new int[]{-1, 0, 11}) {
            assertThrows(IllegalArgumentException.class, () -> recomendador.recomendar(List.of(), cantidad));
        }
    }

    private Asiento asiento(
            String fila,
            int numero,
            boolean disponible,
            int calificacion
    ) {
        return new Asiento(
                fila,
                numero,
                disponible,
                6.5,
                45.0,
                calificacion,
                "vista"
        );
    }
}
