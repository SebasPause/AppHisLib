package com.example.AppHisLib.presentacion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.Libros;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnadirLibroActivity extends AppCompatActivity {

    private String usuario;
    private String id;
    DatabaseReference myRef;
    FirebaseDatabase db;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    ActionBar actionBar;
    FloatingActionButton anadirLibro;
    EditText txtAutor,txtDescripcion,txtGenero;
    ImageView imgAnadirLibro;
    Uri uri;
    List<Libros> listaLibrosPublicados;

    //Para la foto del libro
    private static final int REQUEST_PERMISION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private static final int REQUEST_PERMISION_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_IMAGE_GALERY = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_libro);

        anadirLibro = findViewById(R.id.guardarLibro);
        txtAutor = findViewById(R.id.txtAutor);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtGenero = findViewById(R.id.txtGenero);
        imgAnadirLibro = findViewById(R.id.imgAnadirLibro);

        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();
        if(extras.getBoolean("AnadirLibro")){
            //nada
            actionBar = getSupportActionBar();
            actionBar.setTitle("Añadir Libro");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            listaLibrosPublicados = (List<Libros>) extras.getSerializable("ListaLibrosPublicados");
        }else{
            actionBar = getSupportActionBar();
            actionBar.setTitle("Editar Libro");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            cargarDatos(extras.getString("IDlibro"));
            cargarImagen(extras.getString("IDlibro"));
            listaLibrosPublicados = (List<Libros>) extras.getSerializable("ListaLibrosPublicados");
        }

        if(uri==null){
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getResources().getResourcePackageName(R.drawable.ic_book)
                    + '/' + this.getResources().getResourceTypeName(R.drawable.ic_book)
                    + '/' + this.getResources().getResourceEntryName(R.drawable.ic_book)
            );
        }else{
            uri = Uri.parse(imgAnadirLibro.toString());
            System.out.println("Aqui la uri: "+uri);
        }


        //Para la flecha de volver atras


        anadirLibro.setOnClickListener(v -> {
            if(extras.getBoolean("AnadirLibro")){
                usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance();
                myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");

                String autor = txtAutor.getText().toString();
                String descripcion = txtDescripcion.getText().toString();
                String genero = txtGenero.getText().toString();
                String foto = ""+uri;

                mStorage = FirebaseStorage.getInstance();
                storageRef = mStorage.getReference();


                //Para generar una Id aleatoria y que no exista ya en el usuario
                int nrAleatorio =(int) (Math.random()*1000+1);
                id = usuario+nrAleatorio;
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(id)) {
                                // si ya existe el id del libro en la base de datos hay que crear otro id que no coincida con el que ya hay
                                int nrAleatorio =(int) (Math.random()*1000+1);
                                id = usuario+nrAleatorio;

                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                                Map<String, Object> paginas = new HashMap<>();
                                paginas.put("1","");

                                Map<String, Object> hopperUpdates = new HashMap<>();
                                hopperUpdates.put("Foto",foto);
                                hopperUpdates.put("Autor", autor);
                                hopperUpdates.put("Descripcion",descripcion);
                                hopperUpdates.put("Genero",genero);
                                hopperUpdates.put("Valoracion",0.0f);
                                hopperUpdates.put("Id",id);
                                hopperUpdates.put("Publicado",false);
                                hopperUpdates.put("FechaPublicado",currentDate);
                                hopperUpdates.put("Paginas","");
                                hopperUpdates.put("Valoraciones","");

                                myRef.child(id).setValue(hopperUpdates);
                                myRef.child(id).child("Paginas").setValue(paginas);
                                storageRef.child("Imagenes").child(usuario).child("Libros").child(id).child("Libro.jpeg").putFile(uri);
                                Toast.makeText(AnadirLibroActivity.this, "Libro creado", Toast.LENGTH_SHORT).show();
                            }else{
                                Map<String, Object> paginas = new HashMap<>();
                                paginas.put("1","");

                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                                Map<String, Object> hopperUpdates = new HashMap<>();
                                hopperUpdates.put("Foto",foto);
                                hopperUpdates.put("Autor", autor);
                                hopperUpdates.put("Descripcion",descripcion);
                                hopperUpdates.put("Genero",genero);
                                hopperUpdates.put("Valoracion",0.0f);
                                hopperUpdates.put("Id",id);
                                hopperUpdates.put("Publicado",false);
                                hopperUpdates.put("FechaPublicado",currentDate);
                                hopperUpdates.put("Paginas","");
                                hopperUpdates.put("Valoraciones","");

                                myRef.child(id).setValue(hopperUpdates);
                                myRef.child(id).child("Paginas").setValue(paginas);
                                storageRef.child("Imagenes").child(usuario).child("Libros").child(id).child("Libro.jpeg").putFile(uri);
                                Toast.makeText(AnadirLibroActivity.this, "Libro creado", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i=new Intent(AnadirLibroActivity.this,LibrosActivity.class);
                                        i.putExtra("ListaLibrosPublicados", (Serializable) listaLibrosPublicados);
                                        i.putExtra("Accion",true);
                                        startActivity(i);
                                    }
                                }, 1001);
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //nada
                    }
                });
            } //fin extras null
            else{
                //Aqui va el codigo para cambiar los datos del libro
                usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance();
                mStorage = FirebaseStorage.getInstance();
                storageRef = mStorage.getReference();
                myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");
                String idLibro = extras.getString("IDlibro");

                String autor = txtAutor.getText().toString();
                String descripcion = txtDescripcion.getText().toString();
                String genero = txtGenero.getText().toString();
                String foto = ""+uri;

                System.out.println("Uri de la foto: "+uri);
                System.out.println("Id del libro: "+idLibro);
                storageRef.child("Imagenes").child(usuario).child("Libros").child(idLibro).child("Libro.jpeg").putFile(uri);


                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("Foto",foto);
                            hopperUpdates.put("Autor", autor);
                            hopperUpdates.put("Descripcion",descripcion);
                            hopperUpdates.put("Genero",genero);

                            myRef.child(idLibro).updateChildren(hopperUpdates);
                            Toast.makeText(AnadirLibroActivity.this, "Libro modificado", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i=new Intent(AnadirLibroActivity.this,LibrosActivity.class);
                                    i.putExtra("ListaLibrosPublicados", (Serializable) listaLibrosPublicados);
                                    i.putExtra("Accion",true);
                                    startActivity(i);
                                }
                            }, 1001);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //nada
                    }
                });
            }

        }); //fin añadirLibro


        imgAnadirLibro.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AnadirLibroActivity.this);
            builder.setMessage("Elige una opcion")
                    .setPositiveButton("CAMARA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Compruebo si tiene permisos
                            if(ActivityCompat.checkSelfPermission(AnadirLibroActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                                irCamara();
                            }else{
                                //Si no tiene permisos uso el requestPermissions
                                ActivityCompat.requestPermissions(AnadirLibroActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_PERMISION_CAMERA);
                            }
                        }
                    })
                    .setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //compruebo si tiene permisos de acceder a los archivos
                            if(ActivityCompat.checkSelfPermission(AnadirLibroActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                                irGaleria();
                            }else{
                                //Pido los permisos
                                ActivityCompat.requestPermissions(AnadirLibroActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISION_EXTERNAL_STORAGE);
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }); //fin foto setonclicklistener
    }  //fin onCreate


    public void cargarImagen(String idLibro){
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference().child("Imagenes").child(usuario).child("Libros").child(idLibro).child("Libro.jpeg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(AnadirLibroActivity.this)
                        .load(uri)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                        .into(imgAnadirLibro);
            }
        });
    }

    public void cargarDatos(String idLibro){
        myRef = db.getReference().child("Usuarios").child(usuario).child("Libros").child(idLibro);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Libros datosLibro = snapshot.getValue(Libros.class);
                txtAutor.setText(datosLibro.Autor);
                txtDescripcion.setText(datosLibro.Descripcion);
                txtGenero.setText(datosLibro.Genero);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISION_CAMERA){
            //Si el usuario permite los permisos
            if(permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                irCamara();
            }else{
                Toast.makeText(this, "Acepta los permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == REQUEST_PERMISION_EXTERNAL_STORAGE){
            if(permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                irGaleria();
            }else{
                Toast.makeText(this, "Acepta los permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }
    } //fin onrequest

    //Compruebo si se lanza el intent de la camara y compruebo si se ha tomado foto o no
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAMERA){
            if(resultCode== Activity.RESULT_OK){
                Bitmap bitmap;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imgAnadirLibro.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == REQUEST_IMAGE_GALERY){
            if(resultCode==Activity.RESULT_OK){

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);


            }
            else{
                Toast.makeText(this, "No se ha seleccionado ninguna foto", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult resultado = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = resultado.getUri();
                uri = imageUri;
                imgAnadirLibro.setImageURI(imageUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = resultado.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }

    } //fin onActivityResult


    public void irCamara(){
        ContentValues valores = new ContentValues();
        valores.put(MediaStore.Images.Media.TITLE,"Titulo de la imagen");
        valores.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion de la imagen");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,valores);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,REQUEST_IMAGE_CAMERA);

    } // fin irCamara()

    public void irGaleria(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent,REQUEST_IMAGE_GALERY);
    }



    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}