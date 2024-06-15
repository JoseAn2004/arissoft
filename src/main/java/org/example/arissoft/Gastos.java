package org.example.arissoft;

public class Gastos {
    public Gastos(int id, String nombr, String apellid, String descrip,Double tot) {
        this.id = id;
        this.nombr = nombr;
        this.apellid = apellid;
        this.descrip= descrip;
        this.tot = tot;
    }
    private int id;
    private String nombr;
    private String apellid;
    private String descrip;
    private Double tot;

    public int getId() {
        return id;
    }
    public void setId(int id) {

        this.id = id;
    }

    public String getNombr() {
        return nombr;
    }
    public void setNombr(String nombre) {
        this.nombr = nombr;
    }

    public String getApellid() {
        return apellid;
    }
    public void setApellid(String apellido) {
        this.apellid = apellido;
    }

    public String getDescrip() {

        return descrip;
    }
    public void setDescrip(String descrip){
        this.descrip= descrip;}

//LOS NOMBRES ES IGULA A LAS VARIABLES
    public Double getTot() {return tot;}
    public void setTot(double total) {
        this.tot = total;

}}

