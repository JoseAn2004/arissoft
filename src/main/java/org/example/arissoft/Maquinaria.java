package org.example.arissoft;

public class Maquinaria {
    public Maquinaria(int id, String nombre, String descripcion, double costoPorHora) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoPorHora = costoPorHora;
    }
    private int id;
    private String nombre;
    private String descripcion;
    private double costoPorHora;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCostoPorHora() {
        return costoPorHora;
    }

    public void setCostoPorHora(double costoPorHora) {
        this.costoPorHora = costoPorHora;
    }

}
