package com.example.demo.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Funcion {
    @Id
    private String id; // Ej: "FUNC-101"
    private String tituloPelicula;
    private String horario;
    private String salaId;

    public Funcion() {}

    public Funcion(String id, String tituloPelicula, String horario, String salaId) {
        this.id = id;
        this.tituloPelicula = tituloPelicula;
        this.horario = horario;
        this.salaId = salaId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTituloPelicula() { return tituloPelicula; }
    public void setTituloPelicula(String tituloPelicula) { this.tituloPelicula = tituloPelicula; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getSalaId() { return salaId; }
    public void setSalaId(String salaId) { this.salaId = salaId; }
}
