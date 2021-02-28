package com.example.AppHisLib.casosdeuso;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.AppHisLib.datos.LibroBD;
import com.example.AppHisLib.presentacion.ContentMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CrearEstructura {
    Context contexto;
    private String usuario;
    private DatabaseReference myRef, myRef2;
    List<Libros> listaLibrosPublicados;

    /**
     * Constructor de la clase
     * @param contexto
     * @param usuario
     * @param myRef
     */
    public CrearEstructura(Context contexto, String usuario, DatabaseReference myRef) {
        this.contexto = contexto;
        this.usuario = usuario;
        this.myRef = myRef;
    }

    /**
     * Metodo para crear la estructura del usuario, que colgara de la raiz "Usuarios"
     * Eesta estructura estara formado por usuario(perfil,libros)
     */
    public void crearEstructuraDatos() {
        myRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(usuario)) {
                    // si ya existe el usuario en la base de datos no hay que crear la estructura de datos
                } else {
                    //Clave principal usuario
                    myRef.child(usuario).setValue(usuario);
                    //Hijo de usuario
                    myRef.child(usuario).child("Perfil").setValue("String");
                    myRef.child(usuario).child("Perfil").child("Autor").setValue("Autor");
                    myRef.child(usuario).child("Perfil").child("Descripcion").setValue("Descripción Max 180 caracteres.");
                    myRef.child(usuario).child("Perfil").child("Foto").setValue("");
                    myRef.child(usuario).child("Perfil").child("Edad").setValue("");
                    //Hijo de usuario
                    myRef.child(usuario).child("Libros").setValue("String");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //nada
            }
        });
    } //fin metodo crearEstructura

    /**
     * Metodo para borrar un libro al cual se le pasa dos parametros
     * Permite encontrar el libro a borrar gracias a esos dos parametros
     * y lo borra de la base de datos externa con el metodo removeValue()
     * Tambien borra la imagen del libro guardada en el Storage de firebase
     * con el metodo delete()
     * @param id
     * @param usuarioEliminar
     */
    public void borrarLibro(String id, String usuarioEliminar) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference().child("Imagenes").child(usuarioEliminar).child("Libros").child(id).child("Libro.jpeg");

        if(storageRef.hashCode()<=0){
            //nada
        }else{
            storageRef.delete();
        }

        myRef = db.getReference().child("Usuarios").child(usuarioEliminar).child("Libros").child(id);
        myRef.removeValue();
        Toast.makeText(contexto, "Libro borrado", Toast.LENGTH_SHORT).show();

    }

    /**
     * Metodo para publicar un libro
     * Se le cambia el valor de publicarValor a true, entonces gracias a este cambio
     * en la lista de publicaciones obtendrá que el campo "Publicado" es true y lo insertará en el recycler view
     * Se establecerá tambien la fecha en la que se ha publicado
     * @param id
     * @param usuarioLibro
     * @param publicarValor
     */
    public void publicarLibro(String id, String usuarioLibro,boolean publicarValor) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        myRef = db.getReference().child("Usuarios").child(usuarioLibro).child("Libros");
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("Publicado", publicarValor);
        hopperUpdates.put("FechaPublicado", currentDate);
        myRef.child(id).updateChildren(hopperUpdates);

    } //fin publicarLibro

}
