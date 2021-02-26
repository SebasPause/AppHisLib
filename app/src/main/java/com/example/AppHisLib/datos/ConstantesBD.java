package com.example.AppHisLib.datos;

public class ConstantesBD {

    //Nombre de la Base de Datos
    public static final  String BD_NAME = "DatosAplicacion";

    //Version de la BD
    public static final int BD_VERSION = 1;

    //Nombre de las tablas
    public static final String TABLE_NAME_USUARIO = "Usuarios";
    public static final String TABLE_NAME_PERFIL = "Perfil";
    public static final String TABLE_NAME_LIBROS = "Libros";
    public static final String TABLE_NAME_PAGINAS = "Paginas";
    public static final String TABLE_NAME_VALORACIONES = "Valoraciones";

    //Nombre de campos de la tabla Usuarios
    public static final String U_USUARIO = "USUARIO";
    public static final String ID_PERFIL = "IDPERFIL";
    public static final String ID_LIBROS = "IDLIBROS";

    //Nombre de campos de la tabla Perfil
    public static final String P_AUTOR = "AUTOR";
    public static final String P_DESCRIPCION = "DESCRIPCION";
    public static final String P_EDAD = "EDAD";
    public static final String P_FOTO = "FOTO";

    //Nombre de campos de la tabla Libros
    public static final String L_IDENTIFICADOR_LIBRO = "IDENTIFICADOR_LIBRO";
    public static final String L_AUTOR = "AUTOR";
    public static final String L_DESCRIPCION = "DESCRIPCION";
    public static final String L_FOTO = "FOTO";
    public static final String L_GENERO = "GENERO";
    public static final String L_PUBLICADO = "PUBLICADO";
    public static final String L_FECHA_PUBLICADO = "FECHA_PUBLICADO";
    public static final String L_VALORACION = "VALORACION";
    public static final String L_ID_PAGINAS = "ID_PAGINAS";
    public static final String L_ID_VALORACIONES ="ID_VALORACIONES";
    public static final String L_USUARIO_LIBRO = "USUARIOLIBRO";

    //Nombre de campos de la tabla Paginas
    public static final String PA_NUMERO_PAGINA = "NUMERO_PAGINA";
    public static final String PA_CONTENIDO = "CONTENIDO";

    //Nombre de campos de la tabla Valoraciones
    public static final String VA_COMENTARIO = "COMENTARIO";
    public static final String VA_VALOR = "VALOR";
    public static final String VA_USUARIO = "USUARIO";
    public static final String VA_LIBRO = "LIBRO";


    //Codigo de creacion de la tabla de Usuario
    public static final String CREATE_TABLE_USUARIO = "CREATE TABLE "+ TABLE_NAME_USUARIO + "("
            + U_USUARIO + " TEXT, "
            + ID_PERFIL + " TEXT, "
            + ID_LIBROS + " TEXT, "
            + " FOREIGN KEY ("+ID_PERFIL+") REFERENCES "+TABLE_NAME_PERFIL+"("+ID_PERFIL+"),"
            + " FOREIGN KEY ("+ID_LIBROS+") REFERENCES "+TABLE_NAME_LIBROS+"("+ID_LIBROS+")"
            +")";

    //Codigo de creacion de la tabla de Perfil
    public static final String CREATE_TABLE_PERFIL = "CREATE TABLE "+ TABLE_NAME_PERFIL + "("
            + ID_PERFIL + " TEXT, "
            + P_AUTOR + " TEXT, "
            + P_DESCRIPCION + " TEXT, "
            + P_EDAD + " TEXT, "
            + P_FOTO + " TEXT "
            + ")";

    //Codigo de creacion de la tabla de Libros
    public static final String CREATE_TABLE_LIBROS = "CREATE TABLE "+ TABLE_NAME_LIBROS + "("
            + ID_LIBROS + " TEXT, "
            + L_IDENTIFICADOR_LIBRO + " TEXT, "
            + L_AUTOR + " TEXT, "
            + L_DESCRIPCION + " TEXT, "
            + L_FOTO + " TEXT, "
            + L_GENERO + " TEXT, "
            + L_PUBLICADO + " TEXT, "
            + L_FECHA_PUBLICADO + " TEXT, "
            + L_VALORACION + " TEXT, "
            + L_ID_PAGINAS + " TEXT, "
            + L_ID_VALORACIONES + " TEXT, "
            + L_USUARIO_LIBRO + " TEXT, "
            + " FOREIGN KEY ("+L_ID_PAGINAS+") REFERENCES "+TABLE_NAME_PAGINAS+"("+L_ID_PAGINAS+")"
            +")";

    //Codigo de creacion de la tabla de Paginas
    public static final String CREATE_TABLE_PAGINAS = "CREATE TABLE "+ TABLE_NAME_PAGINAS + "("
            + L_ID_PAGINAS + " TEXT, "
            + PA_NUMERO_PAGINA + " TEXT, "
            + PA_CONTENIDO + " TEXT "
            + ")";

    //Codigo de creacion de la tabla de Valoraciones
    public static final String CREATE_TABLE_VALORACIONES = "CREATE TABLE "+ TABLE_NAME_VALORACIONES + "("
            + L_ID_VALORACIONES + " TEXT PRIMARY KEY UNIQUE, "
            + VA_COMENTARIO + " TEXT, "
            + VA_VALOR + " TEXT, "
            + VA_USUARIO + " TEXT, "
            + VA_LIBRO + " TEXT "
            + ")";



}
