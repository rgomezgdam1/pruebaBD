package com.example.pruebabbdd

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import androidx.lifecycle.lifecycleScope

import java.sql.SQLException


class MainActivity : AppCompatActivity() {

    var jdbcUrl = "jdbc:mysql://sql8.freesqldatabase.com:3306/sql8687684"
    var jdbcUrl2 = "jdbc:mysql://lhcp3332.webapps.net:3306/rv5az9yb_patata"
    var username = "sql8687684"
    var password = "kfuFPeQzY9"
    var conexion: Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun bbdd(view: View) {
        showToast("Botón pulsado")
        Log.i("Boton", "Botón pulsado")

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleScope.launch(Dispatchers.IO) {
                conectar()
            }
        }
    }

    fun conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            conexion = DriverManager.getConnection(jdbcUrl, username, password)
            Log.i("conexion", "Se ha conectado a la bbdd")
        } catch (e: ClassNotFoundException) {
            Log.e("Error", "Error al cargar el driver de MySQL: ${e.message}")
            showToast("Error al cargar el driver de MySQL")
        } catch (e: SQLException) {
            Log.e("Error", "Error al conectar con la base de datos: ${e.message}")
            showToast("Error al conectar con la base de datos")
        } catch (e: Exception) {
            Log.e("Error", "Error desconocido: ${e.message}")
            showToast("Error desconocido")
        }finally {
            try {
                conexion?.close()
                Log.i("conexion", "Conexión cerrada")
            } catch (e: SQLException) {
                Log.e("Error", "Error al cerrar la conexión: ${e.message}")
            }
    }
    }
}