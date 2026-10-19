package com.example.demo.servicio;

import com.example.demo.modelo.Asiento;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecomendadorAsientos {

    public List<Asiento> recomendar(
            List<Asiento> asientos,
            int cantidadPersonas
    ) {
        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero."
            );
        }

        if (asientos == null || asientos.isEmpty()) {
            return List.of();
        }

        List<Asiento> ordenados = asientos.stream()
                .filter(asiento -> asiento != null && asiento.estaDisponible())
                .sorted(
                        Comparator.comparing(Asiento::getFila)
                                .thenComparingInt(Asiento::getNumero)
                )
                .toList();

        List<Asiento> mejorGrupo = List.of();
        double mejorPuntaje = Double.NEGATIVE_INFINITY;

        for (int inicio = 0;
             inicio <= ordenados.size() - cantidadPersonas;
             inicio++) {
            List<Asiento> candidato = ordenados.subList(
                    inicio,
                    inicio + cantidadPersonas
            );

            if (!sonConsecutivos(candidato)) {
                continue;
            }

            double puntaje = calcularPuntaje(candidato);
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejorGrupo = new ArrayList<>(candidato);
            }
        }

        return mejorGrupo;
    }

    private boolean sonConsecutivos(List<Asiento> grupo) {
        for (int indice = 1; indice < grupo.size(); indice++) {
            Asiento anterior = grupo.get(indice - 1);
            Asiento actual = grupo.get(indice);

            boolean mismaFila = anterior.getFila()
                    .equalsIgnoreCase(actual.getFila());
            boolean numeroSiguiente = actual.getNumero()
                    == anterior.getNumero() + 1;

            if (!mismaFila || !numeroSiguiente) {
                return false;
            }
        }
        return true;
    }

    private double calcularPuntaje(List<Asiento> grupo) {
        double puntaje = 0;

        for (Asiento asiento : grupo) {
            puntaje += asiento.obtenerCalificacion() * 100;
            puntaje -= Math.abs(asiento.getAnguloVision() - 45.0) * 2;
            puntaje -= Math.abs(asiento.getDistanciaPantalla() - 6.5);
        }

        return puntaje;
    }
}
