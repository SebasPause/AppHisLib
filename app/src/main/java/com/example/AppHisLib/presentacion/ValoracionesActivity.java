package com.example.AppHisLib.presentacion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.AdaptadorLibrosPublicados;

public class ValoracionesActivity extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView rvValoraciones;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Valoraciones");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        rvValoraciones = findViewById(R.id.rvValoraciones);
        adapter2 = new AdaptadorLibrosPublicados(ValoracionesActivity.this,null);
        layoutManager2 = new LinearLayoutManager(ValoracionesActivity.this);
        rvValoraciones.setLayoutManager(layoutManager2);
        rvValoraciones.setHasFixedSize(true);
        rvValoraciones.setAdapter(adapter2);


    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}