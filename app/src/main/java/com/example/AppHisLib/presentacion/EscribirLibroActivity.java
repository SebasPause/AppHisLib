package com.example.AppHisLib.presentacion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.AppHisLib.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscribirLibroActivity extends AppCompatActivity {

    FirebaseDatabase db;
    private String usuario;
    DatabaseReference myRef;
    EditText edtEscribirLibro;
    String autor,nrPagina,texto;
    FloatingActionButton btnForward;
    HashMap<String,String> contenidoPagina;
    int posicion;
    int numeroDePaginas;
    int numeroNuevaPagina;
    String IdLibro;
    Bundle extras;
    Boolean NuevaPagina;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_libro);

        extras = getIntent().getExtras();
        contenidoPagina = (HashMap<String,String>)extras.getSerializable("Paginas");
        IdLibro = extras.getString("IdLibro");
        NuevaPagina = extras.getBoolean("NuevaPagina");

        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);
        btnForward = findViewById(R.id.btnForward);

        Collection<String> valores = contenidoPagina.values();
        System.out.println("Aqui tengo los valores: "+valores.toString());

        //Aqui hago el setText
        edtEscribirLibro.setText(contenidoPagina.getOrDefault("1","No lo coge"));

        if(NuevaPagina){
            posicion = extras.getInt("Posicion");
            edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicion),"No lo coge"));
        }

        btnForward.setOnClickListener(v -> {
            numeroDePaginas = contenidoPagina.size();
            numeroNuevaPagina = numeroDePaginas+1;
            Map<String, Object> paginas = new HashMap<>();
            paginas.put(numeroNuevaPagina+"","Nueva Pagina");

            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db = FirebaseDatabase.getInstance();
            myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");
            myRef.child(IdLibro).child("Paginas").updateChildren(paginas);

            cargarPaginas(IdLibro);

            Toast.makeText(EscribirLibroActivity.this, "Cargando Nueva Pagina", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                    i.putExtra("Paginas",contenidoPagina);
                    i.putExtra("Posicion",numeroNuevaPagina);
                    i.putExtra("NuevaPagina",true);
                    i.putExtra("IdLibro",IdLibro);
                    startActivity(i);
                }
            }, 2000);


            Toast.makeText(this, "Posicion "+posicion, Toast.LENGTH_SHORT).show();
        });


    } //fin on create



    public HashMap<String,String> cargarPaginas(String idLibro){
        db = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Libros").child(idLibro).child("Paginas");
        contenidoPagina = new HashMap<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    nrPagina = ds.getKey();
                    texto = ds.getValue(String.class);
                    contenidoPagina.put(nrPagina,texto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return contenidoPagina;
    }

}