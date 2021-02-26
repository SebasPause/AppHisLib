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
import android.widget.Toast;

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
        String comentario = valoraciones.get(position).comentario;
        String valor = valoraciones.get(position).valor;

        Toast.makeText(contexto, "Comentario: "+comentario+" Valor: "+valor, Toast.LENGTH_SHORT).show();

        holder.txtComentario.setText(comentario);
        holder.ratingBar.setRating(Float.parseFloat(valor));

    }

    @Override
    public int getItemCount() {
        return valoraciones.size();
    }

    public class ValoracionesViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaValoraciones;
        RatingBar ratingBar;
        TextView txtComentario;


        public ValoracionesViewHolder(@NonNull View itemView) {
            super(itemView);
            cvListaValoraciones = (CardView)itemView.findViewById(R.id.cvListaValoraciones);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar);
            txtComentario = (TextView)itemView.findViewById(R.id.txtComentario);
        }
    }


}
