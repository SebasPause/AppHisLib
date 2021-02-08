package com.example.AppHisLib;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView btnNavegacion;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);

        btnNavegacion.setOnNavigationItemSelectedListener(this);
        btnNavegacion.bringToFront();


    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    private void updateNavigationBarState(){
        int actionId = getBottomNavigationMenuItemId();
        selectedBottomNavigationBarItem(actionId);
    }

    void selectedBottomNavigationBarItem(int itemId){
        MenuItem item = btnNavegacion.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getBottomNavigationMenuItemId();

    abstract int getLayoutId();

}