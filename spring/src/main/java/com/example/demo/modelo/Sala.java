package com.example.demo.modelo;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    private double tamanoPantalla;
    private String filas;
    private List<Asiento> asientos;

    public Sala() {
        this.tamanoPantalla = 15.5;
        this.filas = "A-E";
        this.asientos = new ArrayList<>();
        
        // Asientos iniciales sin nombres de parámetros
        asientos.add(new Asiento("A", 1, true, 5.2, 45.0, 5, "url_360_A1"));
        asientos.add(new Asiento("A", 2, true, 5.2, 50.0, 5, "url_360_A2"));
        asientos.add(new Asiento("B", 1, false, 4.0, 30.0, 3, "url_360_B1"));
    }

    public List<Asiento> mostrarAsientos() {
        return this.asientos;
    }

    public Asiento buscarAsiento(String fila, int numero) {
        for (Asiento a : asientos) {
            if (a.getFila().equalsIgnoreCase(fila) && a.getNumero() == numero) {
                return a;
            }
        }
        return null;
    }
}