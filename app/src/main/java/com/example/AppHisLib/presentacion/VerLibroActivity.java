package com.example.AppHisLib.presentacion;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.AppHisLib.R;
import com.example.AppHisLib.datos.LibroBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VerLibroActivity extends AppCompatActivity {

    FirebaseDatabase db;
    private String usuario;
    DatabaseReference myRef;
    TextView edtEscribirLibro,txtPagina;
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
    ActionBar actionBar;
    Collection<String> valores;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_libro);

        posicionActual = 1;

        actionBar = getSupportActionBar();
        actionBar.setTitle("Ver Libro");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        extras = getIntent().getExtras();
        if(extras.getBoolean("LibroPublicado")){
            IdLibro = extras.getString("IdLibro");
            LibroBD bd = new LibroBD(this);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                contenidoPagina = bd.cargarPaginasLibro(IdLibro);
                valores = contenidoPagina.values();
            }
        }else{
            contenidoPagina = (HashMap<String,String>)extras.getSerializable("Paginas");
            IdLibro = extras.getString("IdLibro");
            valores = contenidoPagina.values();
        }



        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);
        btnForward = findViewById(R.id.btnForward);
        btnBack = findViewById(R.id.btnBack);
        txtPagina = findViewById(R.id.txtPagina);


        System.out.println("Aqui tengo los valores: "+valores.toString());

        //Aqui hago el setText
        edtEscribirLibro.setText(contenidoPagina.getOrDefault("1","No lo coge"));
        txtPagina.setText("Pagina 1");

        btnBack.setOnClickListener(v -> {
            if(posicionActual==1){
                Toast.makeText(this, "Estas en la primera pagina", Toast.LENGTH_SHORT).show();
                txtPagina.setText("Pagina "+posicionActual);
            } else{
                posicionActual -= 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                txtPagina.setText("Pagina "+posicionActual);
            }
        });

        btnForward.setOnClickListener(v -> {
            numeroDePaginas = contenidoPagina.size();
            numeroNuevaPagina = numeroDePaginas+1;

            if(posicionActual<numeroDePaginas){
                posicionActual += 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                txtPagina.setText("Pagina "+posicionActual);
            }else{
                Toast.makeText(this, "Estas en la última página", Toast.LENGTH_SHORT).show();
            }
        });
    } //fin onCreate

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}