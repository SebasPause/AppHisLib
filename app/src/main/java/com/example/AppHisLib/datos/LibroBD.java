package com.example.AppHisLib.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.AppHisLib.casosdeuso.Libros;
import com.example.AppHisLib.casosdeuso.Valoracion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.ls.LSOutput;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

public class LibroBD extends SQLiteOpenHelper {

    private DatabaseReference myRef;
    FirebaseDatabase db;
    //FirebaseFirestore ff;
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
                /**
                 * Cada vez que se cambia un valor que va referenciado a la base de datos de firebase
                 * el metodo onDataChange se ejecutara
                 * por eso es necesario llamar al metodo onUpgrade para borrar todos los datos anteriores
                 * y volver a insertar estos nuevos datos
                 * con el fin de que no se dupliquen los datos
                 */
                onUpgrade(getWritableDatabase(),1,2);
                for(DataSnapshot ds : snapshot.getChildren()){
                    //Insertar en la tabla de usuarios
                    valoresUsuarios.put(ConstantesBD.U_USUARIO,ds.getKey());
                    sqDB.insert(ConstantesBD.TABLE_NAME_USUARIO,null,valoresUsuarios);

                    //Insertar en la tabla de perfil
                    myRef = db.getReference("Usuarios").child(ds.getKey());
                    for(DataSnapshot ds2 : snapshot.getChildren()) {
                        //Guardo los datos de todos los perfiles por si mas adelante quiero hacer algo con ellos
                        valoresUsuarios.put(ConstantesBD.ID_PERFIL,"1");
                        valoresPerfil.put(ConstantesBD.ID_PERFIL,"1");
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Autor").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Descripcion").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Edad").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Foto").getValue(String.class));
                        sqDB.insert(ConstantesBD.TABLE_NAME_PERFIL,null,valoresPerfil);

                        for(DataSnapshot ds3 : ds2.child("Libros").getChildren()){
                            if(ds3.child("Publicado").getValue(Boolean.class)==true){
                                System.out.println("VALOR DEL BOOLEAN: "+ds3.child("Publicado").getValue(Boolean.class));
                                System.out.println("ID DEL LIBRO2: "+ds3.getKey());
                                valoresUsuarios.put(ConstantesBD.ID_LIBROS,ds3.getKey());
                                valoresLibros.put(ConstantesBD.ID_LIBROS,ds3.child("Id").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_AUTOR,ds3.child("Autor").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_DESCRIPCION,ds3.child("Descripcion").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_FECHA_PUBLICADO,ds3.child("FechaPublicado").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_PUBLICADO,ds3.child("Publicado").getValue(Boolean.class));
                                valoresLibros.put(ConstantesBD.L_FOTO,ds3.child("Foto").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_GENERO,ds3.child("Genero").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_VALORACION,ds3.child("Valoracion").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_USUARIO_LIBRO,ds3.child("Usuario").getValue(String.class));
                                valoresLibros.put(ConstantesBD.L_ID_VALORACIONES,"1");
                                valoresLibros.put(ConstantesBD.L_ID_PAGINAS,ds3.getKey());
                                sqDB.insert(ConstantesBD.TABLE_NAME_LIBROS,null,valoresLibros);

                                for(DataSnapshot ds4 : ds3.child("Paginas").getChildren()){
                                    valoresPaginas.put(ConstantesBD.L_ID_PAGINAS,ds3.getKey());
                                    valoresPaginas.put(ConstantesBD.PA_NUMERO_PAGINA,ds4.getKey());
                                    valoresPaginas.put(ConstantesBD.PA_CONTENIDO,ds4.getValue(String.class));
                                    sqDB.insert(ConstantesBD.TABLE_NAME_PAGINAS,null,valoresPaginas);
                                }

                                if(ds3.child("Valoraciones").hasChildren()){
                                    for(DataSnapshot ds5 : ds3.child("Valoraciones").getChildren()){
                                        valoresValoraciones.put(ConstantesBD.L_ID_VALORACIONES,ds3.getKey());
                                        valoresValoraciones.put(ConstantesBD.VA_USUARIO,ds5.getKey());
                                        valoresValoraciones.put(ConstantesBD.VA_COMENTARIO,ds5.child("Comentario").getValue(String.class));
                                        valoresValoraciones.put(ConstantesBD.VA_VALOR,ds5.child("Valor").getValue(String.class));
                                        valoresValoraciones.put(ConstantesBD.VA_LIBRO,ds3.child("Id").getValue(String.class));
                                        sqDB.insert(ConstantesBD.TABLE_NAME_VALORACIONES,null,valoresValoraciones);
                                    }
                                }

                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } //fin obtenerDatos

    //Metodo para obtener todos los libros publicados despues de que el metodo obtenerDatos() haya sido ejecutado
    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Libros> devolverLibros(){
        ArrayList<Libros> listaLibrosPublicados = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_LIBROS;

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                listaLibrosPublicados.add(new Libros(
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_AUTOR)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_DESCRIPCION)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_GENERO)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FOTO)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_VALORACION)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.ID_LIBROS)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FECHA_PUBLICADO)),
                    cursor.getString(cursor.getColumnIndex(ConstantesBD.L_USUARIO_LIBRO))
                ));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return listaLibrosPublicados;
    }

    public void borrarLibros(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ConstantesBD.TABLE_NAME_LIBROS);
        db.close();
    }

    //Metodo para obtener todas las valoraciones despues de que el metodo obtenerDatos() haya sido ejecutado
    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Valoracion> devolverValoraciones(String id){
        ArrayList<Valoracion> valoraciones = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+ " WHERE ID_VALORACIONES LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                valoraciones.add(new Valoracion(
                        cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_COMENTARIO)),
                        cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALOR))
                ));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return valoraciones;
    }

    //cargarPaginasLibro
    @RequiresApi(api = Build.VERSION_CODES.P)
    public HashMap<String,String> cargarPaginasLibro(String id){
        HashMap<String,String> paginasLibro = new HashMap<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM Paginas WHERE ID_PAGINAS LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String nr = cursor.getString(cursor.getColumnIndex(ConstantesBD.PA_NUMERO_PAGINA));
                String contenido= cursor.getString(cursor.getColumnIndex(ConstantesBD.PA_CONTENIDO));;
                paginasLibro.put(nr,contenido);
                System.out.println("Numero: "+nr+" Contenido: "+contenido);
            }while(cursor.moveToNext());
        }
        System.out.println("Contenido del hashmap: "+paginasLibro.toString());

        cursor.close();
        db.close();

        return paginasLibro;
    }

    //Metodo para cargar el comentario y la valoracion del libro el cual hemos comentado
    @RequiresApi(api = Build.VERSION_CODES.P)
    public HashMap<String,String> cargarComentario(String usuarioActual,String idLibro){
        HashMap<String,String> comentarios = new HashMap<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM Valoraciones WHERE USUARIO LIKE '"+usuarioActual+"' AND LIBRO LIKE '"+idLibro+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String comentario = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_COMENTARIO));
                String valor = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALOR));;
                comentarios.put("Comentario",comentario);
                comentarios.put("Valor",valor);
                System.out.println("Comentario: "+comentario+" Valor: "+valor);
            }while(cursor.moveToNext());
        }
        System.out.println("Contenido del hashmap: "+comentarios.toString());

        cursor.close();
        db.close();

        return comentarios;
    }

    //Metodo para obtener los comentarios y valoraciones del libro requerido
    @RequiresApi(api = Build.VERSION_CODES.P)
    public Float cargarRating(String id){
        Float rating = 0.0f;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+ " WHERE ID_VALORACIONES LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        float valores = 0.0f;
        int nrDeValores = 0;

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String valor = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALOR));;
                valores = valores + Float.parseFloat(valor);
                nrDeValores += 1;
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        rating = valores / nrDeValores;

        return rating;
    }


}
