package com.example.demo.modelo;

public class Asiento {
    // atribitos que espera sala
    private String fila;
    private int numero;
    private boolean disponible;
    private double distanciaPantalla;
    private double anguloVision;
    private int nivelConveniencia;
    private String urlImagen;

    // Constructor 
    public Asiento(String fila, int numero, boolean disponible, double distanciaPantalla, double anguloVision, int nivelConveniencia, String urlImagen) {
        this.fila = fila;
        this.numero = numero;
        this.disponible = disponible;
        this.distanciaPantalla = distanciaPantalla;
        this.anguloVision = anguloVision;
        this.nivelConveniencia = nivelConveniencia;
        this.urlImagen = urlImagen;
    }

    // Método para que el frontend lo reciba como "A1", "B2", etc.
    public String getId() {
        return this.fila + this.numero;
    }

    // --- GETTERS Y SETTERS NECESARIOS PARA SALA.JAVA Y EL FRONTEND ---

    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public double getDistanciaPantalla() { return distanciaPantalla; }
    public void setDistanciaPantalla(double distanciaPantalla) { this.distanciaPantalla = distanciaPantalla; }

    public double getAnguloVision() { return anguloVision; }
    public void setAnguloVision(double anguloVision) { this.anguloVision = anguloVision; }

    public int getNivelConveniencia() { return nivelConveniencia; }
    public void setNivelConveniencia(int nivelConveniencia) { this.nivelConveniencia = nivelConveniencia; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}