package com.example.AppHisLib.casosdeuso;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Objeto utilizado para guardar los datos relacionados al perfil del usuario actual
 */
public class DatosPerfil {
    public String Autor, Descripcion, Foto, Edad;

    public DatosPerfil() {
    }

    public DatosPerfil(String Autor, String Descripcion,String Foto, String Edad) {
        this.Autor = Autor;
        this.Descripcion = Descripcion;
        this.Foto = Foto;
        this.Edad = Edad;
    }

}



