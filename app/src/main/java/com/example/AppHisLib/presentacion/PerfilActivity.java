package com.example.AppHisLib.presentacion;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.DatosPerfil;
import com.example.AppHisLib.casosdeuso.Libros;
import com.example.AppHisLib.datos.LibroBD;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.sql.SQLOutput;
import java.util.List;

public class PerfilActivity extends BaseActivity implements Serializable {

    ImageView imgEditarPerfil;
    TextView txtAutor,txtDescripcion;
    BottomNavigationView btnNavegacion;
    RatingBar ratingBar;
    private String usuario;
    DatabaseReference myRef;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    FirebaseDatabase db;
    Uri uri;
    ActionBar actionBar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        /**
         * Datos relacionados al menu superior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");

        txtAutor = (TextView)findViewById(R.id.txtAutor);
        txtDescripcion = (TextView)findViewById(R.id.txtDescripcion);
        imgEditarPerfil = (ImageView)findViewById(R.id.imgEditarPerfil);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        /**
         * Establezco el rating del perfil a 0
         * e indico que solo es un indicador y que no se puede modificar manualmente
         */
        ratingBar.setRating(0.0f);
        ratingBar.setIsIndicator(true);

        /**
         * Obtengo el usuario actual y llamo al metodo cargarRatingPerfil()
         * para obtener la valoracion total de todos los publicados del usuario actual
         * con el fin de poder establecer el valor del ratingBar del usuario
         */
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LibroBD bd = new LibroBD(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ratingBar.setRating(bd.cargarRatingPerfil(usuario));
        }

        /**
         * En caso de que el usuario no haya editado su perfil
         * esta uri tendrá la imagen por defecto ic_person
         */
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + this.getResources().getResourcePackageName(R.drawable.ic_person)
                + '/' + this.getResources().getResourceTypeName(R.drawable.ic_person)
                + '/' + this.getResources().getResourceEntryName(R.drawable.ic_person)
        );
        imgEditarPerfil.setImageURI(uri);

        /**
         * Indico al menu inferior que estoy en este Activity
         */
        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);
        btnNavegacion.setOnNavigationItemSelectedListener(this);

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Perfil");
        //Llamo al metodo de descargar y para establecer su imagen correspondiente de perfil del storage de firebase
        downloadSetImage();

        /**
         * Obtengo los datos del objeto de DatosPerfil que contiene toda la informacion
         * relacionada con el perfil del usuario actual.
         * Establezco esos datos en los TextView correspondientes
         */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatosPerfil datosPerfil = snapshot.getValue(DatosPerfil.class);
                txtAutor.setText(datosPerfil.Autor);
                txtDescripcion.setText(datosPerfil.Descripcion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Metodo para actualizar el estado del menu inferior
     */
    private void updateNavigationBarState(){
        int actionId = getBottomNavigationMenuItemId();
        selectedBottomNavigationBarItem(actionId);
    }

    /**
     * Metodo utilizado para descargar la iamgen de perfil del Storage de firebase
     * En caso de que no exista, se establecerá la asignada al uri
     */
    public void downloadSetImage(){
            mStorage = FirebaseStorage.getInstance();
            storageRef = mStorage.getReference().child("Imagenes").child(usuario).child("Perfil").child("Foto.jpeg");

            if(storageRef.hashCode()<=0){
                //nada
            }else{
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(PerfilActivity.this)
                        .load(uri)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                        .into(imgEditarPerfil));
            }
    }

    /**
     * Metodo para decirle al menu inferior que estamos en este Activity
     * @param itemId
     */
    void selectedBottomNavigationBarItem(int itemId){
        MenuItem item = btnNavegacion.getMenu().findItem(itemId);
        item.setChecked(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perfil,menu);
        return true;
    }

    /**
     * Opciones del menu superior que llevan al ativity de editar el perfil
     * o a cerrar sesion(Esta opcion puede no funcionar si se viene desde algun otro activity
     * ya que supuestamente al cambiar de intents, su correspondiente activity deberia pasar por el onDestroy()
     * pero esto no sucede y al hacer click en esta opcion se vuelve a ese activity)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.btnEditarPerfil){
            lanzarEditarPerfil(null);
            return true;
        }
        if(id == R.id.btnCerrarSesion){
            Toast.makeText(this, "Vuelva pronto", Toast.LENGTH_SHORT).show();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            finish();
            Intent i =  new Intent(PerfilActivity.this,MainActivity.class);
            i.putExtra("Codigo","cerrarSesion");
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo llamado para acceder al activity de editar el perfil
     * @param view
     */
    private void lanzarEditarPerfil(View view) {
        Intent intent = new Intent(this, EditarPerfilActivity.class);
        startActivity(intent);
    }

    /**
     * Opciones del menu inferior
     * @param item
     * @return
     */
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
                intent = new Intent(this, ContentMainActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irLibros){
                intent = new Intent(this, LibrosActivity.class);
                startActivity(intent);
            }
            finish();
        },300);
        return true;
    }


    @Override
    int getBottomNavigationMenuItemId() {
        return R.id.irPerfil;
    }

    @Override
    int getLayoutId() {
        return R.layout.perfil;
    }

}