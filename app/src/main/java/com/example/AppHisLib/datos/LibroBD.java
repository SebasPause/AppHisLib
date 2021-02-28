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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.ls.LSOutput;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LibroBD extends SQLiteOpenHelper {

    private DatabaseReference myRef;
    FirebaseDatabase db;
    ContentValues valoresUsuarios,valoresPerfil,valoresLibros,valoresPaginas,valoresValoraciones;
    boolean existeUsuario = false;

    /**
     * Constructor al cual se le pasa el contexto del cual es llamado
     * @param context
     */
    public LibroBD(@Nullable Context context) {
        super(context, ConstantesBD.BD_NAME, null, 1);
    }

    /**
     * Metodo que crea las tablas de la base de datos interna SQLiteDatabase
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantesBD.CREATE_TABLE_VALORACIONES);
        db.execSQL(ConstantesBD.CREATE_TABLE_PAGINAS);
        db.execSQL(ConstantesBD.CREATE_TABLE_LIBROS);
        db.execSQL(ConstantesBD.CREATE_TABLE_PERFIL);
        db.execSQL(ConstantesBD.CREATE_TABLE_USUARIO);
    }

    /**
     * Metodo que sirve para borrar las tablas existentes o no
     * para actualizar los datos de la base de datos externa(firebase)
     * @param db
     * @param oldVersion
     * @param newVersion
     */
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

    /**
     * Metodo para cargar toda la base de datos externa(firebase)
     * en la base de datos interna(SQLiteDatabase)
     * con el fin de poder hacer consultas
     * y manejar los datos de una forma mas comoda y sencilla
     */
    public void obtenerDatos(){
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Usuarios");

        SQLiteDatabase sqDB = this.getWritableDatabase();

        /**
         * Inicializo ContentValues que seran usados
         * con el fin de poder utilizar el metodo insert
         * de la base de datos interna para insertar los datos
         * correspondientes en su correspondiente tabla
         */
        valoresUsuarios = new ContentValues();
        valoresPerfil = new ContentValues();
        valoresLibros = new ContentValues();
        valoresPaginas = new ContentValues();
        valoresValoraciones = new ContentValues();

        /**
         * Hago una escucha a la base de datos externa
         * desde la raiz, ya que mref es la referencia de la raiz "Usuarios"
         * donde se almacena toda la información
         */
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
                    onUpgrade(getWritableDatabase(),1,2);

                    //Insertar en la tabla de perfil
                    myRef = db.getReference("Usuarios").child(ds.getKey());
                    /**
                     * Para poder obtener los hijos de la raiz
                     * hay que iterarar utilizando el metodo .getChildren()
                     * que se gestiona mediante un DataSnapshot
                     */
                    for(DataSnapshot ds2 : snapshot.getChildren()) {
                        /**
                         * Guardo los datos de todos los perfiles por si mas adelante quiero hacer algo con ellos
                         * Todos los campos de la base de datos externa se obtienen
                         * con el metodo .getValue() y el parametro que se le asigna
                         * es el tipo de dato que se ha usado para almacenar en ese campo
                         */
                        valoresUsuarios.put(ConstantesBD.ID_PERFIL,"1");
                        valoresPerfil.put(ConstantesBD.ID_PERFIL,"1");
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Autor").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Descripcion").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Edad").getValue(String.class));
                        valoresPerfil.put(ConstantesBD.P_AUTOR,ds2.child("Perfil").child("Foto").getValue(String.class));
                        sqDB.insert(ConstantesBD.TABLE_NAME_PERFIL,null,valoresPerfil);

                        /**
                         * Itero sobre los hijos "Libros"
                         */
                        for(DataSnapshot ds3 : ds2.child("Libros").getChildren()){
                            /**
                             * En el caso de que el libro tenga el campo de Publicado = true
                             * estos datos se insertaran en la tabla libros
                             * para saber que libros se han publicado.
                             * Los libros no publicados se obtienen directamente desde
                             * la base de datos externa
                             * y no hace falta insertarlos en la tabla
                             */
                            if(ds3.child("Publicado").getValue(Boolean.class)==true){
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

                                /**
                                 * Obtengo los datos del nodo "Paginas"
                                 * para insertarlos en su pagina correspondiente
                                 * al visualizar el libro publicado
                                 */
                                for(DataSnapshot ds4 : ds3.child("Paginas").getChildren()){
                                    valoresPaginas.put(ConstantesBD.L_ID_PAGINAS,ds3.getKey());
                                    valoresPaginas.put(ConstantesBD.PA_NUMERO_PAGINA,ds4.getKey());
                                    valoresPaginas.put(ConstantesBD.PA_CONTENIDO,ds4.getValue(String.class));
                                    sqDB.insert(ConstantesBD.TABLE_NAME_PAGINAS,null,valoresPaginas);
                                } //fin ds4

                                /**
                                 * Si el libro tiene comentarios, estos se insertaran en la tabla
                                 * para poder obtener la valoracion total del libro dependiendo
                                 * de todas las personas que han comentado
                                 * y con el fin de poder obtener la valoracion total del perfil
                                 * utilizando un metodo para obtener la media de valoracion de todos los
                                 * libros publicados y comentados
                                 */
                                if(ds3.child("Valoraciones").hasChildren()){
                                    for(DataSnapshot ds5 : ds3.child("Valoraciones").getChildren()){
                                        valoresValoraciones.put(ConstantesBD.L_ID_VALORACIONES,ds3.getKey());
                                        valoresValoraciones.put(ConstantesBD.VA_USUARIO,ds5.getKey());
                                        valoresValoraciones.put(ConstantesBD.VA_COMENTARIO,ds5.child("Comentario").getValue(String.class));
                                        valoresValoraciones.put(ConstantesBD.VA_VALOR,ds5.child("Valor").getValue(String.class));
                                        valoresValoraciones.put(ConstantesBD.VA_LIBRO,ds3.child("Id").getValue(String.class));
                                        valoresValoraciones.put(ConstantesBD.VA_USUARIOLIBRO,ds3.child("Usuario").getValue(String.class));
                                        sqDB.insert(ConstantesBD.TABLE_NAME_VALORACIONES,null,valoresValoraciones);
                                    } //fin ds5
                                } //fin if ds3.child("Valoraciones").hasChildren()
                            } //fin if ds3.child("Publicado").getValue(Boolean.class)==true
                        } //fin ds3
                    } //fin ds2
                } //fin ds
            } //fin onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            } //fin onCancelled
        });
    } //fin metodo obtenerDatos()

    /**
     * Metodo para obtener todos los libros publicados despues de que el metodo obtenerDatos() haya sido ejecutado
     * Consulta sobre la tabla libros
     * guarda cada libro en un nuevo objeto
     * y este objeto a la vez se añade a una lista
     * para poder iterarar sobre ella
     * y mostrar cada objeto en un cardview
     */
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

    /**
     * Metodo para borrar los libros de la tabla
     * ya que al pasar entre intents se producen duplicaciones
     * y por lo tanto es necesario borrar datos
     * que luego son obtenidos de nuevo cuando son requeridos
     */
    public void borrarLibros(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ConstantesBD.TABLE_NAME_LIBROS);
        db.close();
    }

    /**
     * Metodo para obtener todas las valoraciones despues de que el metodo obtenerDatos() haya sido ejecutado
     * A este metodo se le pasa un parametro que permite hacer una consulta sobre la tabla valoraciones
     * con el fin de crear un nuevo objeto que
     * a su vez se añadira a una lista para poder iterar sobre ella
     * mostrando cada objeto en un cardview
     * @param  id
     */
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

    /**
     * Metodo el cual recibe el id del libro
     * que permite hacer una consulta sobre la tabla Paginas
     * donde se obtienen las paginas y el contenido de cada pagina
     * para poder mostrarla cuando se invoque la opcion
     * de ver libro o editar libro
     * En este caso se devuelve un HashMap con parametros String ya que los dos valores
     * que son necesarios de la tabla, se han introducido con ese formato
     * @param id
     * @return
     */
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
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return paginasLibro;
    }

    //Metodo para cargar el comentario y la valoracion del libro el cual hemos comentado

    /**
     * Metodo que establece el comentario que hemos echo sobre un libro
     * para darnos cuenta que ya hemos echo ese paso
     * con el fin de informar al usuario que puede eliminar el comentario
     * y volver a insertar uno nuevo si asi lo desea
     * Si no ha echo ningun comentario, los dos campos establecidos estaran vacios
     * @param usuarioActual
     * @param idLibro
     * @return
     */
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
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return comentarios;
    }

    //Metodo para obtener los comentarios y valoraciones del libro requerido

    /**
     * Metodo al cual se le pasa el id del libro
     * con el fin de obtener la valoracion media
     * gracias a las valoraciones de los demas usuarios
     * (se puede hacer una valoracion del propio libro.PD:Para aumentar un poco el rating :D)
     * @param id
     * @return
     */
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

    //Metodo para obtener los comentarios y valoraciones del libro requerido

    /**
     * Metodo al cual se le pasa el parametro del usuario actual
     * para poder establecer el ratingBar del Perfil del usuario
     * haciendo una consulta a la tabla de valoraciones
     * donde la columna de "USUARIOLIBRO" de la tabla
     * devuelva todos los comentarios que se han echo sobre
     * todos los libros publicados por dicho usuario
     * @param usuario
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public Float cargarRatingPerfil(String usuario){
        Float rating = 0.0f;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" WHERE USUARIOLIBRO LIKE '"+usuario+"'";

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



    //Devolver usuarios de las valoraciones

    /**
     * Metodo que devuelve una lista de usuarios
     * donde la columna de id_valoraciones de la tabla valoraciones
     * sea el libro pasada por parametro.
     * Gracias a este metodo se puede saber si el usuario actual ya ha comentado
     * o no en el actual libro
     * @param id
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public List<String> devolverUsuarios(String id){
        List<String> usuarios =  new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" WHERE ID_VALORACIONES LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String usuario = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_USUARIO));
                usuarios.add(usuario);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usuarios;
    }

}
