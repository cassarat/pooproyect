package com.example.demo.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Entity
public class Sala {

    @Id
    private String id;
    private String nombre;
    private int capacidad;

    // Con @Transient le decimos a JPA que esta lista se maneja en código y no cree una columna extra
    @Transient
    private List<Asiento> asientos;

    public Sala() {
        this.asientos = new ArrayList<>();
    }

    public Sala(String id, String nombre, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.asientos = new ArrayList<>();
    }

    // --- MÉTODOS QUE REQUIEREN TUS CONTROLADORES ---

    public List<Asiento> mostrarAsientos() {
        return asientos;
    }

    public Asiento buscarAsiento(String fila, int numero) {
        if (asientos == null) return null;
        for (Asiento a : asientos) {
            if (a.getFila().equalsIgnoreCase(fila) && a.getNumero() == numero) {
                return a;
            }
        }
        return null;
    }

    public void agregarAsiento(Asiento asiento) {
        if (this.asientos == null) {
            this.asientos = new ArrayList<>();
        }
        this.asientos.add(asiento);
    }

    // --- GETTERS Y SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public List<Asiento> getAsientos() { return asientos; }
    public void setAsientos(List<Asiento> asientos) { this.asientos = asientos; }
}
