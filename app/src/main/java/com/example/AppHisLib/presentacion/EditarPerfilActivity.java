package com.example.AppHisLib.presentacion;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.AppHisLib.R;
import com.example.AppHisLib.casosdeuso.DatosPerfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    ImageView imgEditarPerfil;
    ActionBar actionBar;
    EditText txtAutor,txtDescripcion,txtEdad;
    DatabaseReference myRef;
    Button btnGuardarDatosPerfil;
    private String usuario;
    Uri uri;

    //Para la foto de perfil
    private static final int REQUEST_PERMISION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private static final int REQUEST_PERMISION_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_IMAGE_GALERY = 4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        //Para la flecha de volver atras
        actionBar = getSupportActionBar();
        actionBar.setTitle("Editar Perfil");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        txtAutor = (EditText)findViewById(R.id.txtAutor);
        txtDescripcion = (EditText)findViewById(R.id.txtDescripcion);
        txtEdad = (EditText)findViewById(R.id.txtEdad);
        btnGuardarDatosPerfil = (Button)findViewById(R.id.btnGuardarDatosPerfil);
        imgEditarPerfil = (ImageView)findViewById(R.id.imgEditarPerfil);
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + this.getResources().getResourcePackageName(R.drawable.ic_person)
                + '/' + this.getResources().getResourceTypeName(R.drawable.ic_person)
                + '/' + this.getResources().getResourceEntryName(R.drawable.ic_person)
        );

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = db.getReference().child("Usuarios").child(usuario).child("Perfil");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatosPerfil datosPerfil = snapshot.getValue(DatosPerfil.class);
                txtAutor.setText(datosPerfil.Autor);
                txtDescripcion.setText(datosPerfil.Descripcion);
                txtEdad.setText(datosPerfil.Edad);
                uri = Uri.parse(datosPerfil.Foto);
                imgEditarPerfil.setImageURI(uri);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        btnGuardarDatosPerfil.setOnClickListener(v -> {
            usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
            String autor = txtAutor.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String edad = txtEdad.getText().toString();
            String foto = ""+uri;

            Map<String, Object> hopperUpdates = new HashMap<>();
            hopperUpdates.put("Foto",foto);
            hopperUpdates.put("Autor", autor);
            hopperUpdates.put("Descripcion",descripcion);
            hopperUpdates.put("Edad",edad);

            myRef.child(usuario).child("Perfil").updateChildren(hopperUpdates);
            Toast.makeText(this, "Datos modificados correctamente", Toast.LENGTH_SHORT).show();

        });


        imgEditarPerfil.setOnClickListener(onClickListener);


    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //FOTO PERFIL
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfilActivity.this);
            builder.setMessage("Elige una opcion")
                    .setPositiveButton("CAMARA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Compruebo si tiene permisos
                            if(ActivityCompat.checkSelfPermission(EditarPerfilActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                                irCamara();
                            }else{
                                //Si no tiene permisos uso el requestPermissions
                                ActivityCompat.requestPermissions(EditarPerfilActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_PERMISION_CAMERA);
                            }
                        }
                    })
                    .setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //compruebo si tiene permisos de acceder a los archivos
                            if(ActivityCompat.checkSelfPermission(EditarPerfilActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                                irGaleria();
                            }else{
                                //Pido los permisos
                                ActivityCompat.requestPermissions(EditarPerfilActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISION_EXTERNAL_STORAGE);
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }; //fin onClickListener

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
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
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
                imgEditarPerfil.setImageURI(imageUri);
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

}