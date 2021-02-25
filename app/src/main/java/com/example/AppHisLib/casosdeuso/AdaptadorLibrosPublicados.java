package com.example.AppHisLib.casosdeuso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.AppHisLib.R;
import com.example.AppHisLib.datos.ConstantesBD;
import com.example.AppHisLib.presentacion.AnadirLibroActivity;
import com.example.AppHisLib.presentacion.EscribirLibroActivity;
import com.example.AppHisLib.presentacion.LibrosActivity;
import com.example.AppHisLib.presentacion.VerLibroActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdaptadorLibrosPublicados extends RecyclerView.Adapter<AdaptadorLibrosPublicados.LibrosPublicadosViewHolder> {

    private Context contexto;
    private String usuario;
    DatabaseReference myRef;
    List<Libros> librosPublicados;
    Uri uri;


    public AdaptadorLibrosPublicados(Context contexto, List<Libros> librosPublicados){
        this.contexto = contexto;
        this.librosPublicados = librosPublicados;
    }

    @NonNull
    @Override
    public LibrosPublicadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.lista_libros_publicados,parent,false);
        LibrosPublicadosViewHolder librospublicadosViewHolder = new LibrosPublicadosViewHolder(v);
        return librospublicadosViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LibrosPublicadosViewHolder holder, int position) {
        String autor = librosPublicados.get(position).Autor;
        String descripcion = librosPublicados.get(position).Descripcion;
        String genero = librosPublicados.get(position).Genero;
        String foto = librosPublicados.get(position).Foto;
        String valoracion = librosPublicados.get(position).Valoracion;
        String id = librosPublicados.get(position).Id;
        String fechaPublicado = librosPublicados.get(position).FechaPublicado;
        String usuarioLibro = librosPublicados.get(position).usuarioLibro;

        uri = Uri.parse(foto);

        holder.txtAutor.setText(autor);
        holder.txtDescripcion.setText(descripcion);
        holder.txtGenero.setText(genero);
        holder.imagenListaLibros.setImageURI(uri);
        holder.ratingBar.setRating(Float.parseFloat(valoracion));
        holder.txtFechaPublicado.setText(fechaPublicado);

        char charFoto = foto.charAt(0);
        String letra = String.valueOf(charFoto);

        if(letra.equals("a")) {
            holder.imagenListaLibros.setImageURI(uri);
        }else{
            //usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = mStorage.getReference().child("Imagenes").child(usuarioLibro).child("Libros").child(id).child("Libro.jpeg");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(contexto)
                            .load(uri)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                            .into(holder.imagenListaLibros);
                }
            });
        }

        //Si clicko en un libro
        holder.itemView.setOnClickListener(v -> {
            Intent i=new Intent(contexto, VerLibroActivity.class);
            i.putExtra("LibroPublicado",true);
            i.putExtra("IdLibro",id);
            contexto.startActivity(i);
        });

        //Para la imagen de opciones
        holder.imagenOpciones.setOnClickListener(v -> {
            mostrarOpciones(""+position,id,autor,descripcion,genero,foto,valoracion);
        });

        
    }

    public void mostrarOpciones(String position, String id, String autor, String descripcion, String genero, String foto, String valoracion) {
        //Array para que aparezca en el dialogo
        String[] opciones = {"Ver Libro","Valoraciones"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Primera opcion es 0
                if(which == 0){
                    Intent i=new Intent(contexto, VerLibroActivity.class);
                    i.putExtra("LibroPublicado",true);
                    i.putExtra("IdLibro",id);
                    contexto.startActivity(i);
                }
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return librosPublicados.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class LibrosPublicadosViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaLibrosPublicados;
        ImageButton imagenOpciones;
        RatingBar ratingBar;
        ImageView imagenListaLibros;
        TextView txtAutor, txtDescripcion, txtGenero,txtFechaPublicado;


        public LibrosPublicadosViewHolder(@NonNull View itemView) {
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
