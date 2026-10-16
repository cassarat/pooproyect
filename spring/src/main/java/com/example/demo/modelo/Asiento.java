package com.example.demo.modelo;

public class Asiento {
    private String id; // Ejemplo: "A1"
    private boolean ocupado;
    
    // ATRIBUTOS NUEVOS 
    private double distanciaPantalla; // En metros
    private double anguloVision;      // En grados
    private String nivelConveniencia; // "Alta", "Media", "Baja"

    // Constructor
    public Asiento(String id, double distanciaPantalla, double anguloVision, String nivelConveniencia) {
        this.id = id;
        this.ocupado = false;
        this.distanciaPantalla = distanciaPantalla;
        this.anguloVision = anguloVision;
        this.nivelConveniencia = nivelConveniencia;
    }

    // --- GETTERS Y SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }

    public double getDistanciaPantalla() { return distanciaPantalla; }
    public void setDistanciaPantalla(double distanciaPantalla) { this.distanciaPantalla = distanciaPantalla; }

    public double getAnguloVision() { return anguloVision; }
    public void setAnguloVision(double anguloVision) { this.anguloVision = anguloVision; }

    public String getNivelConveniencia() { return nivelConveniencia; }
    public void setNivelConveniencia(String nivelConveniencia) { this.nivelConveniencia = nivelConveniencia; }
}