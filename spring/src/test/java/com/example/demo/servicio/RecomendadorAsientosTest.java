package com.example.demo.servicio;

import com.example.demo.modelo.Asiento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
