package com.example.AppHisLib.presentacion;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.CrearEstructura;
import com.example.AppHisLib.casosdeuso.Libros;
import com.example.AppHisLib.datos.LibroBD;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable{

    private FirebaseAuth mAuth;
    EditText txtCorreo,txtContrasena;
    Button btnEntrar,btnRegistrar;
    CrearEstructura ce;
    private String usuario;
    DatabaseReference myRef,myRef2;
    ActionBar actionBar;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            //no hace nada
        }else{
            String codigo = extras.getString("Codigo");
            if(codigo.equals("cerrarSesion")){
                usuario = null;
                onRestart();
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /**
         * Inicializo una nueva base de datos
         * onUpgrade() para borrar todos los datos y volver a crear las tablas
         * obtenerdDatos() para cargar todos los datos de la base de datos externa
         * en le base de datos interna LibroBD
         */
        LibroBD bd = new LibroBD(this);
        bd.onUpgrade(bd.getWritableDatabase(),1,2);
        bd.obtenerDatos();

        /**
         * Datos relacionados al menu superior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Bienvenid@ a TULIB!");

        //Incializo objetos
        txtCorreo = (EditText)findViewById(R.id.txtCorreo);
        txtContrasena = (EditText)findViewById(R.id.txtPassword);
        btnEntrar = (Button)findViewById(R.id.btn_Entrar);
        btnRegistrar = (Button)findViewById(R.id.btn_Registrar);

        mAuth = FirebaseAuth.getInstance();

        /**
         * Boton que lleva al activity de registro del usuario
         */
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });  //finaliza btnRegistrar

        /**
         * Metodo que inicia sesion
         * Comprobará si el usuario ha introducido bien los campos
         * y en el caso contrario se le informara
         */
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtCorreo.getText().toString().isEmpty() || txtContrasena.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Introduce correo y/o contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(txtCorreo.getText().toString(), txtContrasena.getText().toString())
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithCustomToken:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(MainActivity.this, ContentMainActivity.class);
                                            intent.putExtra("email", task.getResult().getUser().getEmail());
                                            intent.putExtra("LibrosPublicados",true);
                                            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                                            Toast.makeText(MainActivity.this, "Cargando Libros", Toast.LENGTH_SHORT).show();
                                            startActivity(intent);
                                        }
                                    }, 2001);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithCustomToken:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Vuelve a comprobar tus datos de inicio",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(MainActivity.this, "Si no tienes una cuenta Registrate",Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            });
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        //Compruebo si existe el usuario
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user) {
    }
}