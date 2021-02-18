package com.example.AppHisLib.casosdeuso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.example.AppHisLib.presentacion.AnadirLibro;
import com.example.AppHisLib.presentacion.EscribirLibroActivity;
import com.example.AppHisLib.presentacion.LibrosActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdaptadorListaLibros extends RecyclerView.Adapter<AdaptadorListaLibros.LibrosViewHolder> {

    private Context contexto;
    private String usuario;
    DatabaseReference myRef;
    List<Libros> libros;
    Uri uri;


    public AdaptadorListaLibros(Context contexto, List<Libros> libros){
        this.contexto = contexto;
        this.libros = libros;
    }


    @NonNull
    @Override
    public LibrosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.lista_libros,parent,false);
        LibrosViewHolder librosViewHolder = new LibrosViewHolder(v);
        return librosViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LibrosViewHolder holder, int position) {
        String autor = libros.get(position).Autor;
        String descripcion = libros.get(position).Descripcion;
        String genero = libros.get(position).Genero;
        String foto = libros.get(position).Foto;
        String valoracion = libros.get(position).Valoracion;
        String id = libros.get(position).Id;

        uri = Uri.parse(foto);

        holder.txtAutor.setText(autor);
        holder.txtDescripcion.setText(descripcion);
        holder.txtGenero.setText(genero);
        holder.imagenListaLibros.setImageURI(uri);
        holder.ratingBar.setRating(Float.parseFloat(valoracion));

        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference().child("Imagenes").child(usuario).child("Libros").child(id).child("Libro.jpeg");
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



        //Para la imagen de opciones
        holder.imagenOpciones.setOnClickListener(v -> {
            mostrarOpciones(""+position,id,autor,descripcion,genero,foto,valoracion);
        });

    }

    public void mostrarOpciones(String position, String id, String autor, String descripcion, String genero, String foto, String valoracion){
        //Array para que aparezca en el dialogo
        String[] opciones = {"Ver Libro","Escribir Libro","Editar Informacion Libro","Publicar Libro","Eliminar Libro"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Primera opcion es 0
                if(which == 0){
                    //Para ir a editar una persona
                    /*Intent intent = new Intent(contexto, EditarLibroActivity.class);
                    intent.putExtra("id",idFinal);
                    intent.putExtra("nombre",nombre);
                    intent.putExtra("apellidos",apellidos);
                    intent.putExtra("edad",edad);
                    intent.putExtra("telefono",telefono);
                    intent.putExtra("correo",correo);
                    intent.putExtra("foto",foto);
                    intent.putExtra("REQUISITO_EDITAR",true);
                    contexto.startActivity(intent);
                    */
                }
                else if(which == 1){
                    Intent intent = new Intent(contexto, EscribirLibroActivity.class);
                    intent.putExtra("IDLibro",id);
                    contexto.startActivity(intent);
                }
                else if(which == 2){
                    Intent intent = new Intent(contexto, AnadirLibro.class);
                    intent.putExtra("EditarLibro",true);
                    intent.putExtra("IDlibro",id);
                    contexto.startActivity(intent);
                }
                else if(which == 3){
                    //Para publicar un libro
                    AlertDialog.Builder builderEliminar = new AlertDialog.Builder(contexto);
                    builderEliminar.setTitle("Estas seguro de querer publicarlo?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.P)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    CrearEstructura ce = new CrearEstructura(contexto,usuario,myRef);
                                    ce.publicarLibro(id,usuario,uri);
                                    ((LibrosActivity)contexto).onResume();
                                }
                            })
                            .setNegativeButton("No",null);
                    builderEliminar.create().show();
                }
                else if(which == 4){
                    //Para eliminar una persona
                    AlertDialog.Builder builderEliminar = new AlertDialog.Builder(contexto);
                    builderEliminar.setTitle("Estas seguro de querer eliminarlo?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.P)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    CrearEstructura ce = new CrearEstructura(contexto,usuario,myRef);
                                    System.out.println("Id:"+id+" Usuario:"+usuario);
                                    ce.borrarLibro(id,usuario);
                                    ((LibrosActivity)contexto).onResume();
                                }
                            })
                            .setNegativeButton("No",null);
                    builderEliminar.create().show();
                }
            }
        });
        builder.create().show();

    }

    @Override
    public int getItemCount() {
        return libros.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class LibrosViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaLibros;
        ImageButton imagenOpciones;
        RatingBar ratingBar;
        ImageView imagenListaLibros;
        TextView txtAutor, txtDescripcion, txtGenero;


        public LibrosViewHolder(@NonNull View itemView) {
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
