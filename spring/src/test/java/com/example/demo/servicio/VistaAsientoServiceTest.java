package com.example.demo.servicio;

import com.example.demo.modelo.Asiento;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VistaAsientoServiceTest {
    private final VistaAsientoService servicio = new VistaAsientoService();

    private Asiento asiento(double distancia, double angulo) {
        return new Asiento("A", 1, true, distancia, angulo, 5, "");
    }

    @Test
    void vistaFrontalNoDesplazaNiInclinaLaPantalla() {
        var vista = servicio.calcular(asiento(5, 0));
        assertEquals(1, vista.escala());
        assertEquals(0, vista.desplazamientoPorcentaje(), 0.001);
        assertEquals(0, vista.rotacionGrados(), 0.001);
        assertEquals("Vista frontal", vista.descripcion());
    }

    @Test
    void pantallaSeReduceAlAlejarse() {
        assertEquals(0.5, servicio.calcular(asiento(10, 0)).escala());
        assertTrue(servicio.calcular(asiento(10, 0)).escala()
                < servicio.calcular(asiento(5, 0)).escala());
    }

    @Test
    void angulosOpuestosProducenVistasOpuestas() {
        var izquierda = servicio.calcular(asiento(6, -45));
        var derecha = servicio.calcular(asiento(6, 45));
        assertEquals(-izquierda.desplazamientoPorcentaje(), derecha.desplazamientoPorcentaje(), 0.001);
        assertEquals(-izquierda.rotacionGrados(), derecha.rotacionGrados(), 0.001);
        assertEquals(izquierda.escala(), derecha.escala());
    }

    @Test
    void rechazaDatosInvalidosYOcupados() {
        for (double distancia : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> servicio.calcular(asiento(distancia, 20)));
        }
        for (double angulo : new double[]{90, -90, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> servicio.calcular(asiento(5, angulo)));
        }
        Asiento ocupado = asiento(5, 30);
        ocupado.setDisponible(false);
        assertThrows(IllegalArgumentException.class, () -> servicio.calcular(ocupado));
    }
}
