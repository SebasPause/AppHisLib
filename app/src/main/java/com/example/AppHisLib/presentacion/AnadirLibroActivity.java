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

    //Declaro variables
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

        /**
         * Obtengo los objetos que utilizare del layout
         */
        anadirLibro = findViewById(R.id.guardarLibro);
        txtAutor = findViewById(R.id.txtAutor);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtGenero = findViewById(R.id.txtGenero);
        imgAnadirLibro = findViewById(R.id.imgAnadirLibro);

        /**
         * Obtengo el usuario actual
         * Obtengo la instancia de la base de datos de firebase
         */
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();
        /**
         * Si al pasar de activity se recibe en extras
         * el key de "AnadirLibro"
         * significa que habra que realizar esa opcion.
         * Sino habra que realizar la modificacion
         * de los datos de un libro ya existente
         */
        if(extras.getBoolean("AnadirLibro")){
            /**
             * Obtengo la barra superior,
             * establezco el titulo
             * y habilito la flecha de volver hacia atras
             */
            actionBar = getSupportActionBar();
            actionBar.setTitle("Añadir Libro");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }else{
            /**
             * Obtengo la barra superior,
             * establezco el titulo
             * y habilito la flecha de volver hacia atras.
             */
            actionBar = getSupportActionBar();
            actionBar.setTitle("Editar Libro");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            /**
             * cargarDatos() => metodo que obtiene los datos relacionados con el libro
             * y procede a hacer un set en cada campo que corresponde
             * cargarImagen() => metodo que obtiene del Storage la foto almacenada al crear el libro
             * y procede a hacer un set en la imagen para que se pueda observar
             */
            cargarDatos(extras.getString("IDlibro"));
            cargarImagen(extras.getString("IDlibro"));
        }

        /**
         * En caso de que la uri sea null
         * se le asignara ic_book
         */
        if(uri==null){
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getResources().getResourcePackageName(R.drawable.ic_book)
                    + '/' + this.getResources().getResourceTypeName(R.drawable.ic_book)
                    + '/' + this.getResources().getResourceEntryName(R.drawable.ic_book)
            );
        }else{
            uri = Uri.parse(imgAnadirLibro.toString());
        }


        /**
         * Boton con el cual se interactua
         */
        anadirLibro.setOnClickListener(v -> {
            /**
             * Como anteriormente, si el key es para Añadir un nuevo libro
             */
            if(extras.getBoolean("AnadirLibro")){
                usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance();
                myRef = db.getReference().child("Usuarios").child(usuario).child("Libros");

                /**
                 * Obtengo los datos del layout para poder
                 * guardarlos en la base de datos externa
                 */
                String autor = txtAutor.getText().toString();
                String descripcion = txtDescripcion.getText().toString();
                String genero = txtGenero.getText().toString();
                String foto = ""+uri;

                /**
                 * Obtengo la instancia del Storage de firebase
                 * para poder obtener luego su referencia
                 * e interactuar con el
                 */
                mStorage = FirebaseStorage.getInstance();
                storageRef = mStorage.getReference();


                /**
                 * Genero un numero aleatorio que me permite crear un identificador exclusivo del libro
                 * a partir del usuario actual y el numero aleatorio
                 */
                int nrAleatorio =(int) (Math.random()*100000+1);
                id = usuario+nrAleatorio;
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        /**
                         * Compruebo sin en la base de datos externa ya existe un libro con ese identificador exclusivo
                         * En el caso de que exista, que es muy improbable pero siempre puede pasar,
                         * se generara nuevamente otro identificador
                         */
                        if (snapshot.hasChild(id)) {
                            // si ya existe el id del libro en la base de datos hay que crear otro id que no coincida con el que ya hay
                            int nrAleatorio = (int) (Math.random()*1000+1);
                            id = usuario+nrAleatorio;

                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                            /**
                             * Siempre que se crea un libro, se creara tambien
                             * una pagina en blanco para gestionar el error
                             * de que no existe ninguna pagina al querer leerlo o escribirlo
                             */
                            Map<String, Object> paginas = new HashMap<>();
                            paginas.put("1","");

                            /**
                             * Inserto todos los valores en un hashmap
                             */
                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("Foto",foto);
                            hopperUpdates.put("Autor", autor);
                            hopperUpdates.put("Descripcion",descripcion);
                            hopperUpdates.put("Genero",genero);
                            hopperUpdates.put("Valoracion","0");
                            hopperUpdates.put("Id",id);
                            hopperUpdates.put("Publicado",false);
                            hopperUpdates.put("FechaPublicado",currentDate);
                            hopperUpdates.put("Paginas","");
                            hopperUpdates.put("Valoraciones","");
                            hopperUpdates.put("Usuario",usuario);

                            /**
                             * Establezco esta estructura de datos en el identificador del libro.
                             * Establezco una pagina en blanco.
                             * Inserto la foto del libro en el Storage
                             */
                            myRef.child(id).setValue(hopperUpdates);
                            myRef.child(id).child("Paginas").setValue(paginas);
                            storageRef.child("Imagenes").child(usuario).child("Libros").child(id).child("Libro.jpeg").putFile(uri);
                            Toast.makeText(AnadirLibroActivity.this, "Libro creado", Toast.LENGTH_SHORT).show();
                            /**
                             * Metodo que hace que el cambio de ventana no sea instantaneo
                             */
                            new Handler().postDelayed(() -> {
                                Intent i=new Intent(AnadirLibroActivity.this,LibrosActivity.class);
                                startActivity(i);
                            }, 1001);
                        }else{
                            /**
                             * Mismo proceso que el anterior en caso de que el identificador del libro
                             * no este en la base de datos externa
                             */
                            Map<String, Object> paginas = new HashMap<>();
                            paginas.put("1","");

                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("Foto",foto);
                            hopperUpdates.put("Autor", autor);
                            hopperUpdates.put("Descripcion",descripcion);
                            hopperUpdates.put("Genero",genero);
                            hopperUpdates.put("Valoracion","0");
                            hopperUpdates.put("Id",id);
                            hopperUpdates.put("Publicado",false);
                            hopperUpdates.put("FechaPublicado",currentDate);
                            hopperUpdates.put("Paginas","");
                            hopperUpdates.put("Valoraciones","");
                            hopperUpdates.put("Usuario",usuario);

                            myRef.child(id).setValue(hopperUpdates);
                            myRef.child(id).child("Paginas").setValue(paginas);
                            storageRef.child("Imagenes").child(usuario).child("Libros").child(id).child("Libro.jpeg").putFile(uri);
                            Toast.makeText(AnadirLibroActivity.this, "Libro creado", Toast.LENGTH_SHORT).show();
                            /**
                             * Metodo que hace que el cambio de ventana no sea instantaneo
                             */
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i=new Intent(AnadirLibroActivity.this,LibrosActivity.class);
                                    startActivity(i);
                                }}, 1001);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //nada
                    }
                });
            } //fin extras null
            else{
                /**
                 * En caso de que la opción haya sido la de editar el libro,
                 * obtengo todas las referencias a la base de datos(Storage y DatabaseReference).
                 * Obtengo tambien los datos introducidos en los campos
                 */
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

                /**
                 * En el caso de que no exista el directorio al no haber elegido una foto para el libro,
                 * no se realizara ningun procedimiento.
                 * En el caso contrario, se actualizara la foto
                 */
                if(storageRef.child("Imagenes").child(usuario).child("Libros").child(idLibro).child("Libro.jpeg").hashCode()<=0){
                    //nada
                }else{
                    storageRef.child("Imagenes").child(usuario).child("Libros").child(idLibro).child("Libro.jpeg").putFile(uri);
                }

                /**
                 * Se hace un updateChildren para actualizar los datos
                 * del libro actual en la base de datos externa
                 */
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
            finish();
        }); //fin añadirLibro

        /**
         * Cuando se pulsa la imagen del libro
         * aparaceran estas opciones(Camara,Galeria)
         */
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


    /**
     * metodo que obtiene del Storage la foto almacenada al crear el libro
     * y procede a hacer un set en la imagen para que se pueda observar
     * @param idLibro
     */
    public void cargarImagen(String idLibro){
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference().child("Imagenes").child(usuario).child("Libros").child(idLibro).child("Libro.jpeg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri1) {
                Glide.with(AnadirLibroActivity.this)
                        .load(uri1)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                        .into(imgAnadirLibro);
                uri = uri1;
            }
        });
    }

    /**
     * metodo que obtiene los datos relacionados con el libro
     * y procede a hacer un set en cada campo que corresponde
     * @param idLibro
     */
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


    /**
     * Metodo que permite volver hacia atras cuando
     * la flecha del action bar haya sido pulsada
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}