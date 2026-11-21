package com.example.demo;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.AsientoRepository;
import com.example.demo.modelo.Sala;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** Datos aislados para las pruebas de la simulación; producción siempre consulta SQLite. */
public final class DatosSala {
    private DatosSala() {}

    public static Sala crear() {
        List<Asiento> asientos = new ArrayList<>();
        for (int fila = 0; fila < 7; fila++) {
            for (int numero = 1; numero <= 10; numero++) {
                double angulo = Math.abs(5.5 - numero) * 8;
                asientos.add(new Asiento(String.valueOf((char) ('A' + fila)), numero, true,
                        3 + fila * 2, angulo, Math.max(1, 5 - (int) (angulo / 15)), ""));
            }
        }
        AsientoRepository repositorio = mock(AsientoRepository.class);
        when(repositorio.findAll()).thenReturn(asientos);
        when(repositorio.findById(anyString())).thenAnswer(invocacion ->
                asientos.stream().filter(a -> a.getId().equals(invocacion.getArgument(0))).findFirst());
        return new Sala(repositorio);
    }
}
