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
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdaptadorLibrosPublicados extends RecyclerView.Adapter<AdaptadorLibrosPublicados.LibrosPublicadosViewHolder> {

    private Context contexto;
    private String usuario;
    DatabaseReference myRef;
    List<Libros> libros;
    Uri uri;


    public AdaptadorLibrosPublicados(Context contexto, List<Libros> libros){
        this.contexto = contexto;
        this.libros = libros;
    }




    @NonNull
    @Override
    public LibrosPublicadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.lista_libros,parent,false);
        AdaptadorLibrosPublicados.LibrosPublicadosViewHolder librospublicadosViewHolder = new AdaptadorLibrosPublicados.LibrosPublicadosViewHolder(v);
        return librospublicadosViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LibrosPublicadosViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return libros.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class LibrosPublicadosViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaLibros;
        ImageButton imagenOpciones;
        RatingBar ratingBar;
        ImageView imagenListaLibros;
        TextView txtAutor, txtDescripcion, txtGenero;


        public LibrosPublicadosViewHolder(@NonNull View itemView) {
            super(itemView);
            cvListaLibros = (CardView)itemView.findViewById(R.id.cvListaLibros);
            imagenOpciones = (ImageButton)itemView.findViewById(R.id.imagen_opciones);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar);
            imagenListaLibros = (ImageView)itemView.findViewById(R.id.imgListaLibros);
            txtAutor = (TextView)itemView.findViewById(R.id.txtAutor);
            txtDescripcion = (TextView)itemView.findViewById(R.id.txtDescripcion);
            txtGenero = (TextView)itemView.findViewById(R.id.txtGenero);


        }
    }
}
