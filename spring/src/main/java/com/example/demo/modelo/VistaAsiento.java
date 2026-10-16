package com.example.demo.modelo;

/** Datos de la simulación; no modifica la selección ni reserva el asiento. */
public record VistaAsiento(
        String id, String fila, int numero, double distanciaPantalla,
        double anguloVision, double escala, double desplazamientoPorcentaje,
        double rotacionGrados, String descripcion) {
}
