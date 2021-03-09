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
import android.text.Editable;
import android.text.TextWatcher;
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

        /**
         * Datos relacionados al menu superior
         * Permite volver hacia atras al pulsar la flecha que contiene
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Escribir Libro");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        /**
         * Siempre que se entra a este Activity,
         * hay que establecer que la posicionActual es 1
         */
        posicionActual = 1;

        /**
         * Gracias a los extras, obtengo desde el adaptador de libros,
         * el numero de paginas y su contenido
         * para poder interactuar con el
         */
        extras = getIntent().getExtras();
        contenidoPagina = (HashMap<String,String>)extras.getSerializable("Paginas");
        IdLibro = extras.getString("IdLibro");
        NuevaPagina = extras.getBoolean("NuevaPagina");

        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);
        btnForward = findViewById(R.id.btnForward);
        btnBack = findViewById(R.id.btnBack);
        txtPagina = findViewById(R.id.txtPagina);

        edtEscribirLibro.setMovementMethod(null);
        edtEscribirLibro.setMaxLines(20);

        //Habilito la flecha de avanzar
        btnForward.setEnabled(true);

        //Aqui hago el setText
        edtEscribirLibro.setText(contenidoPagina.getOrDefault("1","No lo coge"));
        txtPagina.setText("Página 1");

        /**
         * Si es nueva pagina,obtendremos en la posicion
         * el numero de la ultima pagina creada
         */
        if(NuevaPagina){
            posicion = extras.getInt("Posicion");
            edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicion),"No lo coge"));
            txtPagina.setText("Página "+posicion);
        }

        /**
         * Boton de retroceder
         */
        btnBack.setOnClickListener(v -> {
            /**
             * Si estamos en una nueva pagina
             * la posicion actual sera el numero total de paginas
             * y al pulsarlo,a la posicionActual se le restara 1
             * ademas de asignar al boolean de NuevaPagina que al ir hacia atras
             * ya no será una nueva pagina
             */
            if(NuevaPagina){
                numeroDePaginas = contenidoPagina.size();
                posicionActual = numeroDePaginas;
                //Gracias a este metodo se actualiza la pagina en la base de datos externa
                guardarTexto();
                posicionActual -= 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                txtPagina.setText("Página "+posicionActual);
                NuevaPagina = false;
                cargarPaginas(IdLibro);
            }else{
                /**
                 * Si la posicion actual es la primera
                 * Se guardan los datos
                 * y se avisa de que estamos en la primera pagina y no se puede retroceder mas
                 */
                if(posicionActual==1){
                    guardarTexto();
                    edtEscribirLibro.setText(contenido);
                    Toast.makeText(this, "Estas en la primera pagina", Toast.LENGTH_SHORT).show();
                    txtPagina.setText("Página "+posicionActual);
                }
                else{
                    /**
                     * En caso de que no estemos en la primera pagina,
                     * la posicion actual volvera a reducirse.
                     * Se establece el contenido de la pagina
                     * y textView donde aparece el numero de pagina actual
                     */
                    guardarTexto();
                    edtEscribirLibro.setText(contenido);
                    posicionActual -= 1;
                    edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                    txtPagina.setText("Página "+posicionActual);
                }
            }
        });

        /**
         * Boton para avanzar
         */
        btnForward.setOnClickListener(v -> {
            /**
             * Obtengo el numero de paginas total
             * y si se crea una nueva pagina obtengo su numero gracias
             * al numero de paginas totales + 1
             */
            numeroDePaginas = contenidoPagina.size();
            numeroNuevaPagina = numeroDePaginas+1;

            /**
             * Si la posicion actual es menor que el numero de paginas totales
             * y no es nueva nueva pagina,
             * se incrementa la posicion actual y se guardan los datos
             */
            if((posicionActual<numeroDePaginas)&(NuevaPagina==false)){
                guardarTexto();
                posicionActual += 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"No lo coge"));
                txtPagina.setText("Página "+posicionActual);
                btnForward.setEnabled(true);
                cargarPaginas(IdLibro);
            }else{
                /**
                 * si nos encontramos en la ultima pagina y seguimos avanzando,
                 * se creara una nueva pagina y se actualizaran estos datos en
                 * la base de datos externa
                 */
                if((posicionActual==numeroDePaginas)&NuevaPagina){
                    guardarTexto();
                    cargarPaginas(IdLibro);
                    Map<String, Object> paginas = new HashMap<>();
                    paginas.put(numeroNuevaPagina+"","Nueva Pagina");

                    usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db = FirebaseDatabase.getInstance();
                    myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");
                    myRef.child(IdLibro).child("Paginas").updateChildren(paginas);

                    Toast.makeText(EscribirLibroActivity.this, "Cargando Nueva Página", Toast.LENGTH_SHORT).show();
                    /**
                     * Se procedera a hacer un intent a la misma pagina para poder obtener
                     * los datos actuales relacionada a las paginas del libro
                     */
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
                    /**
                     * Para que no se se haga click mas de una vez, este boton se deshabilita
                     * hasta que se complete el intent y entonces sera habilitado de nuevo
                     */
                    btnForward.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2001);
                }else{
                    /**
                     * En el caso de que se creen mas de una pagina seguida,
                     * pasara por aqui para poder controlar de forma correcta el funcionamiento
                     * de la creacion de paginas.
                     * Por lo tanto se obtendran de nuevo todos los datos
                     */
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            cargarPaginas(IdLibro);
                        }
                    }, 2001);

                    numeroDePaginas = contenidoPagina.size();
                    posicionActual = numeroDePaginas;
                    guardarTexto();

                    Map<String, Object> paginas = new HashMap<>();
                    paginas.put(numeroNuevaPagina+"","Nueva Página");

                    usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db = FirebaseDatabase.getInstance();
                    myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");
                    myRef.child(IdLibro).child("Paginas").updateChildren(paginas);

                    Toast.makeText(EscribirLibroActivity.this, "Cargando Nueva Página", Toast.LENGTH_SHORT).show();
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

    /**
     * Metodo que permite guardar en la base de datos externa lo escrito y/o editado
     * al avanzar o retroceder entre las paginas de un lbirp
     */
    public void guardarTexto(){
        contenido = edtEscribirLibro.getText().toString();
        db = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Libros").child(IdLibro).child("Paginas");

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put(String.valueOf(posicionActual),contenido);
        myRef.updateChildren(hopperUpdates);
    }

    /**
     * Metodo que permite obtener el numero de paginas y el contenido del numero correspondiente
     * gracias a una consulta a la base de datos externa pasandole como parametro el id del libro
     * @param idLibro
     * @return
     */
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