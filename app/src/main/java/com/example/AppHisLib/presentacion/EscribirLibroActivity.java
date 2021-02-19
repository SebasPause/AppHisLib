package com.example.AppHisLib.presentacion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
    FloatingActionButton btnForward,btnBack;
    HashMap<String,String> contenidoPagina;
    int posicion;
    int numeroDePaginas;
    int numeroNuevaPagina;
    String IdLibro;
    Bundle extras;
    Boolean NuevaPagina;
    int posicionActual;
    TextView txtPagina;
    ActionBar actionBar;
    String contenido;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_libro);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Escribir Libro");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        posicionActual = 1;

        extras = getIntent().getExtras();
        contenidoPagina = (HashMap<String,String>)extras.getSerializable("Paginas");
        IdLibro = extras.getString("IdLibro");
        NuevaPagina = extras.getBoolean("NuevaPagina");

        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);
        btnForward = findViewById(R.id.btnForward);
        btnBack = findViewById(R.id.btnBack);
        txtPagina = findViewById(R.id.txtPagina);

        btnForward.setEnabled(true);

        Collection<String> valores = contenidoPagina.values();
        System.out.println("Aqui tengo los valores: "+valores.toString());

        //Aqui hago el setText
        edtEscribirLibro.setText(contenidoPagina.getOrDefault("1","No lo coge"));
        txtPagina.setText("Pagina 1");

        if(NuevaPagina){
            posicion = extras.getInt("Posicion");
            edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicion),"No lo coge"));
            txtPagina.setText("Pagina "+posicion);
        }

        btnBack.setOnClickListener(v -> {
            if(NuevaPagina){
                numeroDePaginas = contenidoPagina.size();
                posicionActual = numeroDePaginas;
                guardarTexto();
                posicionActual -= 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                txtPagina.setText("Pagina "+posicionActual);
                NuevaPagina = false;
                cargarPaginas(IdLibro);
            }else{
                if(posicionActual==1){
                    guardarTexto();
                    edtEscribirLibro.setText(contenido);
                    Toast.makeText(this, "Estas en la primera pagina", Toast.LENGTH_SHORT).show();
                    txtPagina.setText("Pagina "+posicionActual);
                }
                else{
                    guardarTexto();
                    edtEscribirLibro.setText(contenido);
                    posicionActual -= 1;
                    edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                    txtPagina.setText("Pagina "+posicionActual);
                }
            }
        });

        btnForward.setOnClickListener(v -> {
            numeroDePaginas = contenidoPagina.size();
            numeroNuevaPagina = numeroDePaginas+1;

            if((posicionActual<numeroDePaginas)&(NuevaPagina==false)){
                guardarTexto();
                Toast.makeText(this, "He entrado aqui 0", Toast.LENGTH_SHORT).show();
                System.out.println("He entrado aqui 0");

                posicionActual += 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                txtPagina.setText("Pagina "+posicionActual);
                btnForward.setEnabled(true);
                cargarPaginas(IdLibro);
            }else{
                if((posicionActual==numeroDePaginas)&NuevaPagina){
                    guardarTexto();
                    cargarPaginas(IdLibro);
                    Map<String, Object> paginas = new HashMap<>();
                    paginas.put(numeroNuevaPagina+"","Nueva Pagina");

                    Toast.makeText(this, "He entrado aqui 1", Toast.LENGTH_SHORT).show();
                    System.out.println("He entrado aqui 1");

                    usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db = FirebaseDatabase.getInstance();
                    myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");
                    myRef.child(IdLibro).child("Paginas").updateChildren(paginas);

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
                    btnForward.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2001);
                }else{
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            cargarPaginas(IdLibro);
                        }
                    }, 2001);

                    numeroDePaginas = contenidoPagina.size();
                    posicionActual = numeroDePaginas;
                    Toast.makeText(this, "Posicion actual: "+posicionActual, Toast.LENGTH_SHORT).show();
                    System.out.println("Posicion actual: "+posicionActual);
                    guardarTexto();

                    Map<String, Object> paginas = new HashMap<>();
                    paginas.put(numeroNuevaPagina+"","Nueva Pagina");

                    usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db = FirebaseDatabase.getInstance();
                    myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");
                    myRef.child(IdLibro).child("Paginas").updateChildren(paginas);

                    Toast.makeText(this, "He entrado aqui 2", Toast.LENGTH_SHORT).show();
                    System.out.println("He entrado aqui 2");

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
                    btnForward.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2001);
                } //fin else
            } //fin else
        });


    } //fin on create

    public void guardarTexto(){
        contenido = edtEscribirLibro.getText().toString();
        db = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Libros").child(IdLibro).child("Paginas");

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put(String.valueOf(posicionActual),contenido);
        myRef.updateChildren(hopperUpdates);
    }


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

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}