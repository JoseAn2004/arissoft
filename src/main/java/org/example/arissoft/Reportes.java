package org.example.arissoft;

public class Reportes {
    private int idReporte;
    private String fecha;
    private String concepto;
    private String tipo;
    private double monto;
    private int idIngreso;
    private int idEgreso;

    // Constructor
    public Reportes(int idReporte, String fecha, String concepto, String tipo, double monto) {
        this.idReporte = idReporte;
        this.fecha = fecha;
        this.concepto = concepto;
        this.tipo = tipo;
        this.monto = monto;
        this.idIngreso = idIngreso;
        this.idEgreso = idEgreso;
    }

    // Métodos "get" para obtener los valores de las propiedades
    public int getIdReporte() {
        return idReporte;
    }

    public String getFecha() {
        return fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public int getIdIngreso() {
        return idIngreso;
    }

    public int getIdEgreso() {
        return idEgreso;
    }

    // Métodos "set" para establecer los valores de las propiedades
    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public void setIdEgreso(int idEgreso) {
        this.idEgreso = idEgreso;
    }
}
