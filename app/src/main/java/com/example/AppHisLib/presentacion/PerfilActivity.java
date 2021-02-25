package com.example.AppHisLib.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.DatosPerfil;
import com.example.AppHisLib.casosdeuso.Libros;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.List;

public class PerfilActivity extends BaseActivity implements Serializable {

    ImageView imgEditarPerfil;
    TextView txtAutor,txtDescripcion;
    BottomNavigationView btnNavegacion;
    private String usuario;
    DatabaseReference myRef;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    FirebaseDatabase db;
    Uri uri;
    ActionBar actionBar;
    List<Libros> listaLibrosPublicados;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            //nada
        }else{
            listaLibrosPublicados = (List<Libros>) extras.getSerializable("ListaLibrosPublicados");
        }

        txtAutor = (TextView)findViewById(R.id.txtAutor);
        txtDescripcion = (TextView)findViewById(R.id.txtDescripcion);
        imgEditarPerfil = (ImageView)findViewById(R.id.imgEditarPerfil);

        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);

        btnNavegacion.setOnNavigationItemSelectedListener(this);

        db = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Perfil");
        //Llamo al metodo de descargar y establecer su imagen correspondiente de perfil del storage de firebase
        downloadSetImage();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatosPerfil datosPerfil = snapshot.getValue(DatosPerfil.class);
                txtAutor.setText(datosPerfil.Autor);
                txtDescripcion.setText(datosPerfil.Descripcion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateNavigationBarState(){
        int actionId = getBottomNavigationMenuItemId();
        selectedBottomNavigationBarItem(actionId);
    }

    public void downloadSetImage(){
        mStorage = FirebaseStorage.getInstance();
        storageRef = mStorage.getReference().child("Imagenes").child(usuario).child("Perfil").child("Foto.jpeg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(PerfilActivity.this)
                .load(uri)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                .into(imgEditarPerfil));
    }

    void selectedBottomNavigationBarItem(int itemId){
        MenuItem item = btnNavegacion.getMenu().findItem(itemId);
        item.setChecked(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perfil,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.btnEditarPerfil){
            lanzarEditarPerfil(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void lanzarEditarPerfil(View view) {
        Intent intent = new Intent(this, EditarPerfilActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        btnNavegacion.postDelayed(() -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.irPerfil){
                intent = new Intent(this,PerfilActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irPrincipal){
                intent = new Intent(this, ContentMainActivity.class);
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
        return R.id.irPerfil;
    }

    @Override
    int getLayoutId() {
        return R.layout.perfil;
    }

}