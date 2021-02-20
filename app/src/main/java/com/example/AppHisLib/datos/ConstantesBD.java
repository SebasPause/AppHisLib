package com.example.AppHisLib.datos;

public class ConstantesBD {

    //Nombre de la Base de Datos
    public static final  String BD_NAME = "DatosAplicacion";

    //Version de la BD
    public static final int BD_VERSION = 1;

    //Nombre de las tablas
    public static final String TABLE_NAME_USUARIO = "Usuarios";
    public static final String TABLE_NAME_PERFIL = "Perfil";

    //Nombre de campos de la tabla Usuarios
    public static final String U_USUARIO = "USUARIO";
    public static final String ID_PERFIL = "IDPERFIL";

    //Nombre de campos de la tabla Perfil
    public static final String P_AUTOR = "AUTOR";
    public static final String P_DESCRIPCION = "DESCRIPCION";
    public static final String P_EDAD = "EDAD";
    public static final String P_FOTO = "FOTO";

    //Codigo de creacion de la tabla de Usuario
    public static final String CREATE_TABLE_USUARIO = "CREATE TABLE "+ TABLE_NAME_USUARIO + "("
            + U_USUARIO + " TEXT PRIMARY KEY UNIQUE, "
            + ID_PERFIL + " int, "
            + " FOREIGN KEY ("+ID_PERFIL+") REFERENCES "+TABLE_NAME_PERFIL+"("+ID_PERFIL+")"
            +")";

    //Codigo de creacion de la tabla de Perfil
    public static final String CREATE_TABLE_PERFIL = "CREATE TABLE "+ TABLE_NAME_PERFIL + "("
            + ID_PERFIL + " TEXT PRIMARY KEY UNIQUE, "
            + P_AUTOR + " TEXT, "
            + P_DESCRIPCION + " TEXT, "
            + P_EDAD + " TEXT, "
            + P_FOTO + " TEXT "
            + ")";

}
