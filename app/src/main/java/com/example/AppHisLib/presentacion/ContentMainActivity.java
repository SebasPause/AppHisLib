package com.example.AppHisLib.presentacion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.AdaptadorLibrosPublicados;
import com.example.AppHisLib.casosdeuso.AdaptadorListaLibros;
import com.example.AppHisLib.casosdeuso.CrearEstructura;
import com.example.AppHisLib.casosdeuso.Libros;
import com.example.AppHisLib.datos.LibroBD;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentMainActivity extends BaseActivity implements Serializable{

    private String usuario;
    DatabaseReference myRef;
    BottomNavigationView btnNavegacion;
    ActionBar actionBar;
    CrearEstructura ce;
    RecyclerView librosPublicados;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Publicaciones");

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            /**
             * Obtengo todos los datos de la base de datos interna
             * mediante el metodo .devolerLibros()
             * Estos datos los guardo en un arraylist el cual se usara
             * para crear un nuevo adaptador
             */
            LibroBD bd = new LibroBD(this);
            List<Libros> listaLibrosPublicados = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                listaLibrosPublicados = bd.devolverLibros();
            }

            librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
            adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
            layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
            librosPublicados.setLayoutManager(layoutManager2);
            librosPublicados.setHasFixedSize(true);
            librosPublicados.setAdapter(adapter2);

        }else{
            /**
             * Aqui entra siempre al inciar sesion en la aplicacion
             * Una vez dentro de la aplicacion, ya no vuelve a pasar por aqui
             */
            if(extras.getBoolean("LibrosPublicados")){
                List<Libros> listaLibrosPublicados = new ArrayList<>();
                LibroBD bd = new LibroBD(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    listaLibrosPublicados = bd.devolverLibros();
                }

                /**
                 * Ya que es lo primero que se carga al iniciar sesion,es necesario crear una estructura de datos
                 * en la base de datos externa.
                 * El metodo crearEstructuraDatos() se encarga de realizar este paso
                 */
                usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                ce = new CrearEstructura(ContentMainActivity.this,usuario,myRef);
                ce.crearEstructuraDatos();

                librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
                adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
                layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
                librosPublicados.setLayoutManager(layoutManager2);
                librosPublicados.setHasFixedSize(true);
                librosPublicados.setAdapter(adapter2);
            }else{
                /**
                 * Utilizado en caso de que ocurra algo inesperado
                 * se borran los libros actuales que hay en la tabla de libros
                 * y se vuelven a cargar
                 */
                LibroBD bd = new LibroBD(this);
                List<Libros> listaLibrosPublicados = new ArrayList<>();
                bd.borrarLibros();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    listaLibrosPublicados = bd.devolverLibros();
                }

                librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
                adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
                layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
                librosPublicados.setLayoutManager(layoutManager2);
                librosPublicados.setHasFixedSize(true);
                librosPublicados.setAdapter(adapter2);
            }
        }

        /**
         * Hacemos que el menu inferior sepa que estamos en este Activity
         */
        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);
        btnNavegacion.setOnNavigationItemSelectedListener(this);


    } //fin onCreate

    /**
     * Opciones del menu inferior
     * @param item
     * @return
     */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actualizar,menu);
        return true;
    }

    /**
     * Permite averiguar que item se selecciono del menu superior
     * del activity para poder actualizar los libros publicados en este caso
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        /**
         * Lo que hace es llamar al metodo unUpgrade() para resetear la base de datos interna
         * y con el metodo obtenerDatos() inserto todos los datos en la base de datos interna
         */
        if(id == R.id.btnActualizarLibros){
            Toast.makeText(ContentMainActivity.this, "Actualizando la lista de libros publicados", Toast.LENGTH_SHORT).show();
            LibroBD bd = new LibroBD(this);
            bd.onUpgrade(bd.getWritableDatabase(),1,2);
            bd.obtenerDatos();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ContentMainActivity.this, ContentMainActivity.class);
                    startActivity(intent);
                }
            }, 2000);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}