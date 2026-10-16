package com.example.demo.modelo;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    private double tamanoPantalla;
    private String filas;
    private List<Asiento> asientos;

    public Sala() {
        this.tamanoPantalla = 15.5;
        this.filas = "A-G";
        this.asientos = new ArrayList<>();

        // Inicialización automática de 70 asientos (Filas A-G, Asientos 1-10)
        char[] nombresFilas = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
        
        for (int i = 0; i < nombresFilas.length; i++) {
            String fila = String.valueOf(nombresFilas[i]);
            
            // La fila A está más cerca de la pantalla (ej: 3 metros), la G más lejos (ej: 15 metros)
            double distanciaBase = 3.0 + (i * 2.0); 

            for (int num = 1; num <= 10; num++) {
                // Cálculo simple de ángulo: Los del centro (5 y 6) tienen 0 grados (frente a la pantalla)
                // Los de los extremos tienen ángulos más pronunciados
                double angulo = Math.abs(5.5 - num) * 8.0; 
                
                // Cálculo de conveniencia: Entre más cerca al centro y a una distancia media, mejor (1 a 5 estrellas)
                int conveniencia = 5 - (int)(angulo / 15);
                if (conveniencia < 1) conveniencia = 1;

                // Crear el asiento y agregarlo a la lista
                String urlImagen = "url_360_" + fila + num;
                asientos.add(new Asiento(fila, num, true, distanciaBase, angulo, conveniencia, urlImagen));
            }
        }
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

    // Getters
    public double getTamanoPantalla() { return tamanoPantalla; }
    public String getFilas() { return filas; }
}