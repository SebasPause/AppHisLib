package com.example.AppHisLib.presentacion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.CrearEstructura;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContentMainActivity extends BaseActivity {

    private String usuario;
    DatabaseReference myRef;
    BottomNavigationView btnNavegacion;
    ActionBar actionBar;
    CrearEstructura ce;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Principal");

        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        ce = new CrearEstructura(ContentMainActivity.this,usuario,myRef);
        ce.crearEstructuraDatos();



       /*
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(usuario)) {
                    // run some code
                    Toast.makeText(ContentMainActivity.this, "Conectado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    myRef.child(usuario).setValue(usuario);
                    myRef.child(usuario).child("Perfil").setValue("String");
                    myRef.child(usuario).child("Perfil").child("Autor").setValue("");
                    myRef.child(usuario).child("Perfil").child("Descripcion").setValue("");
                    myRef.child(usuario).child("Perfil").child("Foto").setValue("");
                    myRef.child(usuario).child("Perfil").child("Edad").setValue("");
                    myRef.child(usuario).child("Libros").setValue("String");
                    myRef.child(usuario).child("Libros").child("Libro");
                    myRef.child(usuario).child("Libros").child("Libro").child("Id").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Autor").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Descripcion").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Genero").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Foto").setValue("");
                    myRef.child(usuario).child("Libros").child("Libro").child("Valoracion").setValue("");
                    myRef.child(usuario).child("Publicados").setValue("String");
                    Toast.makeText(ContentMainActivity.this, "¡Bienvenid@!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //nada
            }
        });
        */

        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);

        btnNavegacion.setOnNavigationItemSelectedListener(this);


    } //fin onCreate

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        btnNavegacion.postDelayed(() -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.irPerfil){
                intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irPrincipal){
                intent = new Intent(this,ContentMainActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irLibros){
                intent = new Intent(this, LibrosActivity.class);
                startActivity(intent);
            }
            finish();
        },300);
        return true;
    }


    @Override
    int getBottomNavigationMenuItemId() {
        return R.id.irPrincipal;
    }

    @Override
    int getLayoutId() {
        return R.layout.content_main;
    }
}