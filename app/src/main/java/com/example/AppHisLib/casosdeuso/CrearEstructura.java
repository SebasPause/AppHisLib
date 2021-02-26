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

    public CrearEstructura(Context contexto, String usuario, DatabaseReference myRef) {
        this.contexto = contexto;
        this.usuario = usuario;
        this.myRef = myRef;
    }

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


    //Metodo para borrar un libro
    public void borrarLibro(String id, String usuarioEliminar) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference().child("Imagenes").child(usuarioEliminar).child("Libros").child(id).child("Libro.jpeg");
        System.out.println("Referencia de la imagen: " + storageRef);
        storageRef.delete();
        myRef = db.getReference().child("Usuarios").child(usuarioEliminar).child("Libros").child(id);
        myRef.removeValue();
        Toast.makeText(contexto, "Libro borrado", Toast.LENGTH_SHORT).show();

    }

    public void publicarLibro(String id, String usuarioLibro,boolean publicarValor) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        myRef = db.getReference().child("Usuarios").child(usuarioLibro).child("Libros");
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("Publicado", publicarValor);
        hopperUpdates.put("FechaPublicado", currentDate);
        myRef.child(id).updateChildren(hopperUpdates);

    } //fin publicarLibro


    public List<Libros> cargarLibrosPublicados() {
        listaLibrosPublicados = new ArrayList<>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        myRef2 = FirebaseDatabase.getInstance().getReference("Usuarios");
        myRef = db.getReference().child("Usuarios");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    myRef2 = myRef2.child(ds.getKey()).child("Libros");
                    myRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds2 : snapshot.getChildren()) {
                                boolean publicado = ds2.child("Publicado").getValue(Boolean.class);
                                if (publicado) {
                                    //se insertara en la lista de libros
                                    String autor = ds2.child("Autor").getValue(String.class);
                                    String descripcion = ds2.child("Descripcion").getValue(String.class);
                                    String foto = ds2.child("Foto").getValue(String.class);
                                    String genero = ds2.child("Genero").getValue(String.class);
                                    String Id = ds2.child("Id").getValue(String.class);
                                    String valoracion = ds2.child("Valoracion").getValue(String.class);
                                    String FechaPublicado = ds2.child("FechaPublicado").getValue(String.class);
                                    String usuarioLibro = ds2.child("Usuario").getValue(String.class);
                                    Libros libro = new Libros(autor, descripcion, genero, foto, valoracion, Id, FechaPublicado,usuarioLibro);

                                    listaLibrosPublicados.add(libro);
                                } else {
                                    //no esta publicado
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return listaLibrosPublicados;
    }


}
