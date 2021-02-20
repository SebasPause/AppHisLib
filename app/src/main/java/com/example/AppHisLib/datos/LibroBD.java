package com.example.AppHisLib.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class LibroBD extends SQLiteOpenHelper {

    private DatabaseReference myRef;
    FirebaseDatabase db;
    ContentValues valoresUsuarios,valoresPerfil;

    public LibroBD(@Nullable Context context) {
        super(context, ConstantesBD.BD_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantesBD.CREATE_TABLE_PERFIL);
        db.execSQL(ConstantesBD.CREATE_TABLE_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Volver a crear la tabla
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_PERFIL);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_USUARIO);
        onCreate(db);
    }

    public void borrarUsuarios(){
        System.out.println("He borrado usuarios");
        SQLiteDatabase sqDB = this.getWritableDatabase();
        sqDB.execSQL("DELETE FROM "+ConstantesBD.TABLE_NAME_USUARIO);
    }

    public void insertarUsuarios(){
        System.out.println("He insertado usuarios");
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Usuarios");

        SQLiteDatabase sqDB = this.getWritableDatabase();

        valoresUsuarios = new ContentValues();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    valoresUsuarios.put(ConstantesBD.U_USUARIO,ds.getKey());
                    System.out.println("Usuario: "+ds.getKey());
                    sqDB.insert(ConstantesBD.TABLE_NAME_USUARIO,null,valoresUsuarios);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void obtenerDatos(){
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Usuarios");

        SQLiteDatabase sqDB = this.getWritableDatabase();

        valoresUsuarios = new ContentValues();
        valoresPerfil = new ContentValues();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    //Insertar en la tabla de usuarios
                    valoresUsuarios.put(ConstantesBD.U_USUARIO,ds.getKey());
                    sqDB.insert(ConstantesBD.TABLE_NAME_USUARIO,null,valoresUsuarios);

                    //Insertar en la tabla de perfil
                    valoresPerfil.put(ConstantesBD.ID_PERFIL,ds.getKey());
                    myRef = db.getReference("Usuarios").child(ds.getKey());
                    for(DataSnapshot ds2 : snapshot.getChildren()) {
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds.child("Perfil").child("Autor").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds.child("Perfil").child("Descripcion").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds.child("Perfil").child("Edad").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds.child("Perfil").child("Foto").getValue(String.class));
                        sqDB.insert(ConstantesBD.TABLE_NAME_PERFIL,null,valoresPerfil);
                    }
                }
                System.out.println("Valores Usuario: "+valoresUsuarios.toString());
                System.out.println("Valores Perfil : "+valoresPerfil.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void mostrarUsuarios(){
        SQLiteDatabase sqDB = this.getWritableDatabase();
        System.out.println("Muestro usuarios");

        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_USUARIO;

        Cursor cursor;
        cursor = sqDB.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);
        ArrayList<String> valoresUsuarios = new ArrayList<>();

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                valoresUsuarios.add(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_USUARIO)));
            }while(cursor.moveToNext());
        }
        cursor.close();

        System.out.println("Usuarios: "+valoresUsuarios.toString());

    }

}
