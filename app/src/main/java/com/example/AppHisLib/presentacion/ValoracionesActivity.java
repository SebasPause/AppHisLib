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
import java.util.ArrayList;
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
    boolean existeUsuario = false;

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

        /**
         * Hago que el liner layout sea visible
         */
        llContenido.setVisibility(View.VISIBLE);

        /**
         * Datos relacionados al menu superior
         * Habilito una flecha para poder volver al activity anterior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Valoraciones");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /**
         * Establezco por defecto el comentario y el valor del rating bar
         * del linear layout
         */
        edtComentario.setText("");
        ratingBar.setRating(0.0f);

        Bundle extras = getIntent().getExtras();
        if(extras==null){
            //nada
        }else{
            /**
             * Siempre se llega a esta actividad con un putExtra
             * entonces el paso anterior es por si sucede algun error inesperado.
             * Con el metodo devolverValoraciones() obtengo todos los comentarios y valoraciones
             * realizadas en el libro actual y los guardo en un arrayList para que el adaptador
             * se encargue de manejar y mostrar esa informacion
             */
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

            /**
             * Gracias al metodo cargarComentario(),
             * puedo averiguar si el usuario actual ha echo algun comentario en el libro actual.
             * Si no ha echo ningun comentario, podra realizar un comentario.
             * En el caso contrario, se introducirán en el campo del comentario del linear layout,
             * el comentario realizado y se establecerá su correspondiente valoracion en el ratingBar.
             * Gracias a esto podre eliminar la valoracion ya que el usuario actual no puede comentar
             * varias veces en el mismo libro
             */
            bd = new LibroBD(this);
            if(bd.cargarComentario(usuario,idLibro).size()<=0){
                //nada
            }else{
                cargarComentario = bd.cargarComentario(usuario,idLibro);
                edtComentario.setText(cargarComentario.get("Comentario"));
                ratingBar.setRating(Float.parseFloat(cargarComentario.get("Valor")));
                existeComentario = true;
            }


        }

        /**
         * Cuando se envie un comentario , el linear layout desaparecerá
         */
        btnEnviarComentario.setOnClickListener(v -> {
            llContenido.setVisibility(View.GONE);

            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioLibro).child("Libros").child(idLibro).child("Valoraciones");

            /**
             * Obtengo los usuarios que han comentado en este libro gracias
             * al metodo devolverUsuarios() asignandole el id del libro como parametro.
             */
            LibroBD bd = new LibroBD(this);
            List<String> usuarios = new ArrayList<>();
            usuarios = bd.devolverUsuarios(idLibro);

            for(int i=0;i<usuarios.size();i++){
                if(usuarios.get(i).equals(usuario)){
                    existeUsuario = true;
                }
            }

            /**
             * Si no existe ese usuario en la valoracion de ese libro,
             * podra hacer su comentario.
             * En caso contrario se le informará de que ya ha realizado su comentario.
             */
            if(!existeUsuario){
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("Comentario",edtComentario.getText().toString());
                hopperUpdates.put("Valor",String.valueOf(ratingBar.getRating()));

                myRef.child(usuario).updateChildren(hopperUpdates);
                Toast.makeText(this, "Comentario añadido", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ValoracionesActivity.this,ContentMainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Ya has echo un comentario en este libro", Toast.LENGTH_SHORT).show();
            }

        }); //fin btnEnviarComentario

        /**
         * Es el mismo procedimiento que el boton anterior, salvo que en este caso
         * es para eliminar el comentario.
         * Si existe el usuario en la valoracion del libro, se borrara el comentario.
         * En caso contrario se le informará de que no ha realizado ningun comentario.
         */
        btnEliminarComentario.setOnClickListener(v -> {
            LibroBD bd = new LibroBD(this);
            List<String> usuarios = new ArrayList<>();
            usuarios = bd.devolverUsuarios(idLibro);

            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioLibro).child("Libros").child(idLibro).child("Valoraciones");

            for(int i=0;i<usuarios.size();i++){
                if(usuarios.get(i).equals(usuario)){
                    existeUsuario = true;
                }
            }

            if(existeUsuario){
                myRef.child(usuario).removeValue();
                Toast.makeText(this, "Comentario borrado con exito", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ValoracionesActivity.this,ContentMainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "No has echo ningun comentario en este libro", Toast.LENGTH_SHORT).show();
            }


        }); //fin btnEliminarComentario


    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}