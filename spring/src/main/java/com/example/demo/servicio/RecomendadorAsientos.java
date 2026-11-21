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
        if (cantidadPersonas <= 0 || cantidadPersonas > 10) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe estar entre 1 y 10."
            );
        }

        if (asientos == null || asientos.isEmpty()) {
            return List.of();
        }

        List<Asiento> ordenados = asientos.stream()
                .filter(asiento -> asiento != null && asiento.isDisponible())
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
            puntaje += asiento.getNivelConveniencia() * 100;
            // La sala y la simulación usan 0 grados como vista frontal.
            puntaje -= Math.abs(asiento.getAnguloVision()) * 2;
            puntaje -= Math.abs(asiento.getDistanciaPantalla() - 6.5);
        }

        return puntaje;
    }
}
