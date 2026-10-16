package com.example.demo.modelo;

public class Asiento {
    private String fila;
    private int numero;
    private boolean disponible;
    private double distanciaPantalla;
    private double anguloVision;
    private int calificacion;
    private String vista360;

    public Asiento(String fila, int numero, boolean disponible, double distanciaPantalla, double anguloVision, int calificacion, String vista360) {
        this.fila = fila;
        this.numero = numero;
        this.disponible = disponible;
        this.distanciaPantalla = distanciaPantalla;
        this.anguloVision = anguloVision;
        this.calificacion = calificacion;
        this.vista360 = vista360;
    }

    public boolean estaDisponible() {
        return this.disponible;
    }

    public String obtenerCaracteristicas() {
        return "Fila: " + fila + ", Número: " + numero + ", Distancia: " + distanciaPantalla + "m, Ángulo: " + anguloVision + "°";
    }

    public int obtenerCalificacion() {
        return this.calificacion;
    }

    public String getFila() { return fila; }
    public int getNumero() { return numero; }
    public boolean isDisponible() { return disponible; }
    public double getDistanciaPantalla() { return distanciaPantalla; }
    public double getAnguloVision() { return anguloVision; }
    public String getVista360() { return vista360; }
}