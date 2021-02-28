package com.example.AppHisLib.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.AppHisLib.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    EditText etRepConReg,etConReg,etUsReg;
    Button btn_Finalizar,btnVolverRegistro;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        /**
         * Datos relacionados al menu superior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Bienvenid@ a TULIB!");

        etRepConReg = (EditText)findViewById(R.id.etRepConReg);
        etConReg = (EditText)findViewById(R.id.etConReg);
        etUsReg = (EditText)findViewById(R.id.etUsReg);
        btn_Finalizar = (Button)findViewById(R.id.btn_Finalizar);
        btnVolverRegistro = (Button)findViewById(R.id.btnVolverRegistro);

        /**
         * Al seleccionar el boton de finalizar,se haran las comprobaciones correspondientes de firebase
         * y si no ha habido ningun problema, el registro se habra realizado correctamente
         */
        btn_Finalizar.setOnClickListener(v -> {
            if (etUsReg.getText().toString().isEmpty() || etConReg.getText().toString().isEmpty()) {
                Toast.makeText(RegistroActivity.this, "Introduce correo y/o contraseña", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(etUsReg.getText().toString(), etConReg.getText().toString())
                        .addOnCompleteListener(RegistroActivity.this, task -> {
                            Log.i("Resultado task:", task.isSuccessful() + "");
                            Log.w("Mal", "createUserWithEmail:failure", task.getException());

                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                intent.putExtra("email", task.getResult().getUser().getEmail());
                                startActivity(intent);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegistroActivity.this);
                                builder.setTitle("Error");
                                builder.setMessage("Se ha producido un error: " + task.getException());
                                builder.setPositiveButton("Aceptar", null);
                                builder.create().show();
                            }
                        });
            }
        }); //finaliza btn_Finalizar

        /**
         * Si se selecciona el boton de volver, se retorna al activity de login
         */
        btnVolverRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            startActivity(intent);
        });






    }
}