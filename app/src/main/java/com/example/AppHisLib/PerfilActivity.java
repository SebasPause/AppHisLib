package com.example.AppHisLib;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends BaseActivity {

    BottomNavigationView btnNavegacion;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);

        btnNavegacion.setOnNavigationItemSelectedListener(this);
        btnNavegacion.bringToFront();

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
                intent = new Intent(this,ContentMainActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irLibros){
                intent = new Intent(this,LibrosActivity.class);
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