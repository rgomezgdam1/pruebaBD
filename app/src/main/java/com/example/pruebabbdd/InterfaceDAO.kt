package com.example.pruebabbdd

import android.widget.Toast
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.sql.*
import java.sql.DriverManager

abstract class InterfaceDAO {
    companion object {
        const val URL = "jdbc:mysql://lhcp3332.webapps.net:3306/appAcademia?useSSL=false"
        const val USUARIO = "paco69"
        const val CONTRASENA = "paquito69"
    }

    var conexion: Connection? = null
    fun crearBase() {
        var entrada: BufferedReader? = null
        var sentencia: Statement? = null
        val nombreArchivo =
            "sql/ScriptCreacionAppAcademia.sql"
        conectar()

        try {
            entrada = BufferedReader(FileReader(nombreArchivo))
            var linea = entrada.readLine()
            conexion!!.autoCommit = false
            while (linea != null) {
                linea = entrada.readLine()
                try {
                    sentencia = conexion!!.createStatement()
                    sentencia.execute(linea)
                } catch (e: SQLException) {
                }
            }
            conexion!!.commit() // para hacer transacción a la vez
        } catch (e: SQLException) {
            // para hacer transacción a la vez:
            try {
                conexion!!.rollback() // si al ejecutar da error, hacemos rollback
            } catch (e1: SQLException) {
            }
        } catch (e: IOException) {
        } finally {
            try {
                if (entrada != null) {
                    try {
                        entrada.close()
                    } catch (e: NullPointerException) {
                    } catch (e: IOException) {
                    }
                }
                sentencia?.close()
            } catch (e: SQLException) {
            }
            desconectar()
        }
    }

    fun conectar() {
        // Configuramos la conexión con la base de datos
        try {
            // Cargamos el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver")

            // Establecemos la conexión con la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA)

        } catch (e: SQLException) {
        } catch (e: ClassNotFoundException) {
        }
    }

    fun desconectar() {
        try {
            conexion!!.close()
        } catch (e: SQLException) {
        }
    }
}