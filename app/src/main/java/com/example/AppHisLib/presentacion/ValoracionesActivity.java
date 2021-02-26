package com.example.AppHisLib.presentacion;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.AdaptadorLibrosPublicados;
import com.example.AppHisLib.casosdeuso.AdaptadorValoraciones;
import com.example.AppHisLib.casosdeuso.Valoracion;
import com.example.AppHisLib.datos.LibroBD;

import java.util.List;

public class ValoracionesActivity extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView rvValoraciones;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;
    List<Valoracion> listaValoraciones;
    LibroBD bd;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Valoraciones");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras==null){
            //nada
        }else{
            String idLibro = extras.getString("IDlibro");
            LibroBD bd = new LibroBD(this);
            listaValoraciones = bd.devolverValoraciones(idLibro);
        }




        rvValoraciones = findViewById(R.id.rvValoraciones);
        adapter2 = new AdaptadorValoraciones(ValoracionesActivity.this,listaValoraciones);
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