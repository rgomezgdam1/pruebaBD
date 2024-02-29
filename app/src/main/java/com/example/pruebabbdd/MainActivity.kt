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

    var jdbcUrl = "jdbc:mysql://lhcp3332.webapps.net:3306/rv5az9yb_patata?useSSL=false"
    var jdbcUrl2 = "jdbc:mysql://lhcp3332.webapps.net:3306/rv5az9yb_patata"
    var username = "rv5az9yb_bbddadmin"
    var password = "R1732004ldl"
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
                jdbcUrl2,
                username,
                password
            )
            Log.i("conexion","Se ha conectado a la bbdd")

        } catch (e: Exception) {
            println(e)
        }
    }
}