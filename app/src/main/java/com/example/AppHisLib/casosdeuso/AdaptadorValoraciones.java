package com.example.AppHisLib.casosdeuso;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppHisLib.R;

import java.util.List;

public class AdaptadorValoraciones extends RecyclerView.Adapter<AdaptadorValoraciones.ValoracionesViewHolder>{

    private Context contexto;
    List<Valoracion> valoraciones;

    public AdaptadorValoraciones(Context contexto, List<Valoracion> valoraciones) {
        this.contexto = contexto;
        this.valoraciones = valoraciones;
    }

    @NonNull
    @Override
    public ValoracionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.lista_valoraciones,parent,false);
        ValoracionesViewHolder librospublicadosViewHolder = new ValoracionesViewHolder(v);
        return librospublicadosViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ValoracionesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ValoracionesViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaLibrosPublicados;
        ImageButton imagenOpciones;
        RatingBar ratingBar;
        ImageView imagenListaLibros;
        TextView txtAutor, txtDescripcion, txtGenero,txtFechaPublicado;


        public ValoracionesViewHolder(@NonNull View itemView) {
            super(itemView);
            cvListaLibrosPublicados = (CardView)itemView.findViewById(R.id.cvListaLibrosPublicados);
            imagenOpciones = (ImageButton)itemView.findViewById(R.id.imagen_opciones);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar);
            imagenListaLibros = (ImageView)itemView.findViewById(R.id.imgListaLibros);
            txtAutor = (TextView)itemView.findViewById(R.id.txtAutor);
            txtDescripcion = (TextView)itemView.findViewById(R.id.txtDescripcion);
            txtGenero = (TextView)itemView.findViewById(R.id.txtGenero);
            txtFechaPublicado = (TextView)itemView.findViewById(R.id.txtFechaPublicado);
        }
    }


}
