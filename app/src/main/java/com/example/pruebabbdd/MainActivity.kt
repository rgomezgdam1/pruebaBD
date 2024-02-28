package com.example.pruebabbdd

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class MainActivity : AppCompatActivity() {

    private val jdbcUrl = "jdbc:mysql://appacc-adqydk60.db.tb-hosting.com"
    private val username = "appacc_adqydk60"
    private val password = "Velasco9!"
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

    fun bbdd(view: View){
        showToast("Botón pulsado")
        Log.i("Boton","Botón pulsado")
        conectar()
    }
    fun conectar() {
        // Configuramos la conexión con la base de datos
        try {
            // Cargamos el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver")

            // Establecemos la conexión con la base de datos
            conexion = DriverManager.getConnection(
                jdbcUrl,
                username,
                password
            )
            Log.i("conexion","Se ha conectado a la bbdd")

        } catch (e: Exception) {
            println(e)
        }
    }
}