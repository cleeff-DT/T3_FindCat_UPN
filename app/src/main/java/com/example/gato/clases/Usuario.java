package com.example.gato.clases;

import java.io.Serializable;
import java.util.Date;

public class Usuario implements Serializable {
    private int id;
    private String nombre;
    private String apellidos;
    private String dni;
    private char celular;
    private int id_distrito;
    private String correo;
    private String clave;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String apellidos, String dni, char celular, int id_distrito, String correo, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.celular = celular;
        this.id_distrito = id_distrito;
        this.correo = correo;
        this.clave = clave;
    }

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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public char getCelular() {
        return celular;
    }

    public void setCelular(char celular) {
        this.celular = celular;
    }

    public int getId_distrito() {
        return id_distrito;
    }

    public void setId_distrito(int id_distrito) {
        this.id_distrito = id_distrito;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
