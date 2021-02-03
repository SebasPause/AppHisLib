package com.example.AppHisLib;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ContentMainActivity extends AppCompatActivity {

    Button btnProbar;
    private FirebaseUser usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        btnProbar = (Button)findViewById(R.id.btnProbar);

        btnProbar.setOnClickListener(v -> {
            usuario = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");

            System.out.println(myRef);

            Task task;

            task = myRef.setValue(usuario);

            Task<Void> subir = task.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ContentMainActivity.this, "Ha funcionado", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            Toast.makeText(this, "usuario almacenado", Toast.LENGTH_SHORT).show();
        });


    }


}