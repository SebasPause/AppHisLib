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
    ContentValues valoresUsuarios,valoresPerfil,valoresLibros,valoresPaginas,valoresValoraciones;

    public LibroBD(@Nullable Context context) {
        super(context, ConstantesBD.BD_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantesBD.CREATE_TABLE_VALORACIONES);
        db.execSQL(ConstantesBD.CREATE_TABLE_PAGINAS);
        db.execSQL(ConstantesBD.CREATE_TABLE_LIBROS);
        db.execSQL(ConstantesBD.CREATE_TABLE_PERFIL);
        db.execSQL(ConstantesBD.CREATE_TABLE_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Volver a crear la tabla
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_VALORACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_PAGINAS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_LIBROS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_PERFIL);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_USUARIO);
        onCreate(db);
    }

    public void borrarUsuarios(){
        System.out.println("He borrado usuarios");
        SQLiteDatabase sqDB = this.getWritableDatabase();
        sqDB.execSQL("DELETE FROM "+ConstantesBD.TABLE_NAME_USUARIO);
    }

    public void obtenerDatos(){
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Usuarios");

        SQLiteDatabase sqDB = this.getWritableDatabase();

        valoresUsuarios = new ContentValues();
        valoresPerfil = new ContentValues();
        valoresLibros = new ContentValues();
        valoresPaginas = new ContentValues();
        valoresValoraciones = new ContentValues();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    //Insertar en la tabla de usuarios
                    valoresUsuarios.put(ConstantesBD.U_USUARIO,ds.getKey());
                    sqDB.insert(ConstantesBD.TABLE_NAME_USUARIO,null,valoresUsuarios);

                    //Insertar en la tabla de perfil
                    myRef = db.getReference("Usuarios").child(ds.getKey());
                    for(DataSnapshot ds2 : snapshot.getChildren()) {
                        valoresUsuarios.put(ConstantesBD.ID_PERFIL,"1");
                        valoresPerfil.put(ConstantesBD.ID_PERFIL,"1");
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Autor").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Descripcion").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Edad").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Foto").getValue(String.class));
                        sqDB.insert(ConstantesBD.TABLE_NAME_PERFIL,null,valoresPerfil);

                        for(DataSnapshot ds3 : ds2.child("Libros").getChildren()){
                            if(ds3.child("Publicado").getValue(Boolean.class)){
                                valoresLibros.put(ConstantesBD.ID_LIBROS,ds3.child("Id").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_AUTOR,ds3.child("Autor").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_DESCRIPCION,ds3.child("Descripcion").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_FECHA_PUBLICADO,ds3.child("FechaPublicado").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_FOTO,ds3.child("Foto").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_GENERO,ds3.child("Genero").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_VALORACION,ds3.child("Valoracion").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_ID_VALORACIONES,"1");
                                valoresLibros.put(ConstantesBD.L_ID_PAGINAS,"1");
                                sqDB.insert(ConstantesBD.TABLE_NAME_LIBROS,null,valoresLibros);

                                for(DataSnapshot ds4 : ds3.child("Paginas").getChildren()){
                                    valoresPaginas.put(ConstantesBD.L_ID_PAGINAS,"1");
                                    valoresPaginas.put(ConstantesBD.PA_NUMERO_PAGINA,ds4.getKey());
                                    valoresPaginas.put(ConstantesBD.PA_CONTENIDO,ds4.getValue(String.class));
                                }

                                if(ds3.child("Valoraciones").hasChildren()){
                                    for(DataSnapshot ds5 : ds3.child("Valoraciones").getChildren()){
                                        valoresValoraciones.put(ConstantesBD.L_ID_VALORACIONES,"1");
                                        valoresValoraciones.put(ConstantesBD.VA_COMENTARIO,ds5.child("Comentario").getValue(String.class));
                                        valoresValoraciones.put(ConstantesBD.VA_VALOR,ds5.child("Valor").getValue(Float.class));
                                    }
                                }

                            }
                        }

                    }

                }
                //System.out.println("Valores Usuario: "+valoresUsuarios.toString());
                //System.out.println("Valores Perfil : "+valoresPerfil.toString());
                //System.out.println("Valores Libros: "+valoresLibros.toString());
                //System.out.println("Valores Paginas: "+valoresPaginas.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
