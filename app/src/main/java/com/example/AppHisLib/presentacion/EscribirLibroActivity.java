package com.example.AppHisLib.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.AdaptadorListaLibros;
import com.example.AppHisLib.casosdeuso.Libros;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EscribirLibroActivity extends AppCompatActivity {

    FirebaseDatabase db;
    private String usuario;
    DatabaseReference myRef;
    EditText edtEscribirLibro;
    String autor,nrPagina,texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_libro);

        Bundle extras = getIntent().getExtras();
        String idLibro = extras.getString("IDLibro");

        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);

        db = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Libros").child(idLibro).child("Paginas");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,String> listaPaginas = new HashMap<>();
                for(DataSnapshot ds : snapshot.getChildren()){
                    nrPagina = ds.getKey();
                    texto = ds.getValue(String.class);

                    listaPaginas.put(nrPagina,texto);

                }

                Toast.makeText(EscribirLibroActivity.this, "Pagina Libro: "+nrPagina+" Texto: "+texto, Toast.LENGTH_SHORT).show();
                //Aqui hago el setText
                edtEscribirLibro.setText(texto);            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}