package com.example.AppHisLib.casosdeuso;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.AppHisLib.presentacion.ContentMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CrearEstructura {
    Context contexto;
    private String usuario;
    private DatabaseReference myRef;

    public CrearEstructura(Context contexto, String usuario, DatabaseReference myRef) {
        this.contexto = contexto;
        this.usuario = usuario;
        this.myRef = myRef;
    }

    public void crearEstructuraDatos(){
        myRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(usuario)) {
                    // si ya existe el usuario en la base de datos no hay que crear la estructura de datos
                }else{
                    //Clave principal usuario
                    myRef.child(usuario).setValue(usuario);
                    //Hijo de usuario
                    myRef.child(usuario).child("Perfil").setValue("String");
                    myRef.child(usuario).child("Perfil").child("Autor").setValue("");
                    myRef.child(usuario).child("Perfil").child("Descripcion").setValue("");
                    myRef.child(usuario).child("Perfil").child("Foto").setValue("");
                    myRef.child(usuario).child("Perfil").child("Edad").setValue("");
                    //Hijo de usuario
                    myRef.child(usuario).child("Libros").setValue("String");
                    myRef.child(usuario).child("Libros").child("Libro");
                    myRef.child(usuario).child("Libros").child("Libro").child("Id").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Autor").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Descripcion").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Genero").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Foto").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Valoracion").setValue("");
                    //Hijo de usuario
                    myRef.child(usuario).child("Publicados").setValue("String");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //nada
            }
        });
    } //fin metodo crearEstructura


    //Metodo para borrar un libro
    public void borrarLibro(String id,String usuarioEliminar){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        myRef = db.getReference().child("Usuarios").child(usuarioEliminar).child("Libros").child(id);
        myRef.removeValue();
        Toast.makeText(contexto, "Libro borrado", Toast.LENGTH_SHORT).show();

    }

}
