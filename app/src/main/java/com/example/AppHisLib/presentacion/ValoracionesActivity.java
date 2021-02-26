package com.example.AppHisLib.presentacion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.AdaptadorLibrosPublicados;
import com.example.AppHisLib.casosdeuso.AdaptadorValoraciones;
import com.example.AppHisLib.casosdeuso.Valoracion;
import com.example.AppHisLib.datos.LibroBD;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValoracionesActivity extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView rvValoraciones;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;
    List<Valoracion> listaValoraciones;
    LibroBD bd;
    LinearLayout llContenido;
    Button btnEnviarComentario,btnEliminarComentario;
    private String usuario;
    String usuarioLibro;
    String idLibro;
    DatabaseReference myRef;
    EditText edtComentario;
    RatingBar ratingBar;
    HashMap<String,String> cargarComentario;
    boolean existeComentario = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones);

        llContenido = findViewById(R.id.llContenido);
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario);
        btnEliminarComentario = findViewById(R.id.btnEliminarComentario);
        edtComentario = findViewById(R.id.edtComentario);
        ratingBar = findViewById(R.id.ratingBar);

        llContenido.setVisibility(View.VISIBLE);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Valoraciones");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        edtComentario.setText("");
        ratingBar.setRating(0.0f);

        btnEliminarComentario.setOnClickListener(v -> {
            if(!existeComentario){
                Toast.makeText(this, "No has comentado en este libro", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Comentario borrado con exito", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ValoracionesActivity.this,ContentMainActivity.class);
                startActivity(intent);
            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras==null){
            //nada
        }else{
            idLibro = extras.getString("IDlibro");
            usuarioLibro = extras.getString("UsuarioLibro");
            bd = new LibroBD(this);
            listaValoraciones = bd.devolverValoraciones(idLibro);

            rvValoraciones = findViewById(R.id.rvValoraciones);
            adapter2 = new AdaptadorValoraciones(ValoracionesActivity.this,listaValoraciones);
            layoutManager2 = new LinearLayoutManager(ValoracionesActivity.this);
            rvValoraciones.setLayoutManager(layoutManager2);
            rvValoraciones.setHasFixedSize(true);
            rvValoraciones.setAdapter(adapter2);

            LibroBD bd = new LibroBD(this);
            if(bd.cargarComentario(usuario,idLibro).size()==0){
                Toast.makeText(this, "Esta vacio", Toast.LENGTH_SHORT).show();
            }else{
                cargarComentario = bd.cargarComentario(usuario,idLibro);
                edtComentario.setText(cargarComentario.get("Comentario"));
                ratingBar.setRating(Float.parseFloat(cargarComentario.get("Valor")));
                existeComentario = true;
            }


        }

        btnEnviarComentario.setOnClickListener(v -> {
            llContenido.setVisibility(View.GONE);

            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioLibro).child("Libros").child(idLibro).child("Valoraciones");

            if(myRef.child(usuario)==null){
                myRef.setValue(usuario);

                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("Comentario",edtComentario.getText().toString());
                hopperUpdates.put("Valor",String.valueOf(ratingBar.getRating()));

                myRef.child(usuario).setValue(hopperUpdates);
                Toast.makeText(this, "Comentario añadido", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Ya has echo un comentario en este libro", Toast.LENGTH_SHORT).show();
            }

        }); //fin btnEnviarComentario

    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}