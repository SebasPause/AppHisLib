package com.example.AppHisLib.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.AppHisLib.R;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AnadirLibro extends AppCompatActivity {

    private String usuario;
    private String id;
    DatabaseReference myRef;
    FirebaseDatabase db;
    ActionBar actionBar;
    FloatingActionButton anadirLibro;
    EditText txtAutor,txtDescripcion,txtGenero;
    ImageView foto;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_libro);

        anadirLibro = findViewById(R.id.guardarLibro);
        txtAutor = findViewById(R.id.txtAutor);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtGenero = findViewById(R.id.txtGenero);
        foto = findViewById(R.id.imgAnadirLibro);


        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + this.getResources().getResourcePackageName(R.drawable.ic_book)
                + '/' + this.getResources().getResourceTypeName(R.drawable.ic_book)
                + '/' + this.getResources().getResourceEntryName(R.drawable.ic_book)
        );

        //Para la flecha de volver atras
        actionBar = getSupportActionBar();
        actionBar.setTitle("Añadir Libro");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        anadirLibro.setOnClickListener(v -> {
            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db = FirebaseDatabase.getInstance();
            myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");

            String autor = txtAutor.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String genero = txtGenero.getText().toString();
            String foto = ""+uri;

            //Para generar una Id aleatoria y que no exista ya en el usuario
            int nrAleatorio =(int) (Math.random()*1000+1);
            id = usuario+nrAleatorio;
            System.out.println("Usuario "+usuario);
            System.out.println("nrAleatorio "+nrAleatorio);
            System.out.println(id);
            System.out.println("LLegue aqui");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(id)) {
                            // si ya existe el id del libro en la base de datos hay que crear otro id que no coincida con el que ya hay
                            int nrAleatorio =(int) (Math.random()*1000+1);
                            id = usuario+nrAleatorio;

                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("Foto",foto);
                            hopperUpdates.put("Autor", autor);
                            hopperUpdates.put("Descripcion",descripcion);
                            hopperUpdates.put("Genero",genero);
                            hopperUpdates.put("Valoracion","0");
                            hopperUpdates.put("Id",id);

                            myRef.child(id).setValue(hopperUpdates);
                            Toast.makeText(AnadirLibro.this, "Libro creado", Toast.LENGTH_SHORT).show();
                        }else{
                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("Foto",foto);
                            hopperUpdates.put("Autor", autor);
                            hopperUpdates.put("Descripcion",descripcion);
                            hopperUpdates.put("Genero",genero);
                            hopperUpdates.put("Valoracion","0");
                            hopperUpdates.put("Id",id);

                            myRef.child(id).setValue(hopperUpdates);
                            Toast.makeText(AnadirLibro.this, "Libro creado", Toast.LENGTH_SHORT).show();
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //nada
                }
            });

        }); //fin añadirLibro

    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}