package org.example.arissoft;




public class Usuario {
    public Usuario(int id, String nombre, String apellido, int dni ,String direccionc, int telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.direccionc = direccionc;
        this.telefono= telefono;
    }
    private int id;
    private String nombre;
    private String apellido;
    private int dni;
    private String direccionc;
    private int  telefono;

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;

    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;

    }

    public String getDireccionc() {
        return direccionc;
    }

    public void setDireccionc(String direccionc) {
        this.direccionc = direccionc;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }


}
