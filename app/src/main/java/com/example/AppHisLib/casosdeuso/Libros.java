package com.example.AppHisLib.casosdeuso;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Libros implements Serializable {
    public String Autor,Descripcion,Genero,Foto,Valoracion,Id,FechaPublicado,usuarioLibro;

    public Libros(){}

    public Libros(String Autor, String Descripcion, String Genero, String Foto, String Valoracion, String Id) {
        this.Autor = Autor;
        this.Descripcion = Descripcion;
        this.Genero = Genero;
        this.Foto = Foto;
        this.Valoracion = Valoracion;
        this.Id = Id;
    }

    public Libros(String Autor, String Descripcion, String Genero, String Foto, String Valoracion, String Id,String FechaPublicado,String usuarioLibro) {
        this.Autor = Autor;
        this.Descripcion = Descripcion;
        this.Genero = Genero;
        this.Foto = Foto;
        this.Valoracion = Valoracion;
        this.Id = Id;
        this.FechaPublicado = FechaPublicado;
        this.usuarioLibro = usuarioLibro;
    }

    public List<Libros> libros;

}
