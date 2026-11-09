package com.example.demo.servicio;

import com.example.demo.modelo.Asiento;
import com.example.demo.modelo.VistaAsiento;
import org.springframework.stereotype.Service;

@Service
public class VistaAsientoService {
    public VistaAsiento calcular(Asiento asiento) {
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento no existe.");
        }
        if (!asiento.isDisponible()) {
            throw new IllegalArgumentException("El asiento está ocupado.");
        }
        double distancia = asiento.getDistanciaPantalla();
        double angulo = asiento.getAnguloVision();
        if (!Double.isFinite(distancia) || distancia <= 0
                || !Double.isFinite(angulo) || Math.abs(angulo) >= 90) {
            throw new IllegalArgumentException("La distancia o el ángulo del asiento no permiten simular la vista.");
        }

        // Convención de esta simulación: 0° es frontal; el signo indica el lado.
        // La sala actual solo proporciona ángulos positivos, sin coordenadas laterales.
        // Escala inversa a la distancia, limitada para mantener la pantalla visible.
        double escala = Math.max(0.25, Math.min(1.0, 5.0 / distancia));
        double desplazamiento = -16.0 * Math.sin(Math.toRadians(angulo));
        String descripcion = Math.abs(angulo) <= 15 ? "Vista frontal"
                : Math.abs(angulo) <= 40 ? "Vista ligeramente lateral" : "Vista lateral";
        return new VistaAsiento(asiento.getId(), asiento.getFila(), asiento.getNumero(),
                distancia, angulo, escala, desplazamiento, -angulo, descripcion);
    }
}
