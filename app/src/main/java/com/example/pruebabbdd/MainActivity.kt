package com.example.pruebabbdd

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import androidx.lifecycle.lifecycleScope
import com.example.pruebabbdd.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import org.apache.commons.net.ftp.FTP
import java.io.ByteArrayOutputStream
import java.io.IOException
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.InetAddress

import java.sql.SQLException


class MainActivity : AppCompatActivity() {

    var jdbcUrl = "jdbc:mysql://lhcp3367.webapps.net:3306/xj5trgrj_infoappacademia"
    var username = "xj5trgrj_admin"
    var password = "Velasco9!Velasco9!"
    var conexion: Connection? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            requesPermission()
        }
    }

    private fun requesPermission() {
        when{
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED ->{
                pickPhoto()
            }

            else-> requestPermisionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }
    private var requestPermisionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            pickPhoto()
        }else{
            Toast.makeText(this,"Necesitas habilitar los permisos de galería",Toast.LENGTH_SHORT).show()
        }

    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap?): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    private fun createAndUploadEmptyFile() {
        var ftpClient: FTPClient? = null

        try {
            ftpClient = FTPClient()
            ftpClient.connect(InetAddress.getByName("81.88.53.117"))

            if (ftpClient.isConnected) {
                ftpClient.login("admin_images@appacademia.es", "Velasco9!Velasco9!")
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                // Crear un archivo vacío
                val emptyFileContent = "".toByteArray()
                val inputStream: InputStream = ByteArrayInputStream(emptyFileContent)

                ftpClient.enterLocalPassiveMode()

                // Subir el archivo vacío al servidor
                val remoteFileName = "archivo_vacio.txt"
                if (ftpClient.storeFile(remoteFileName, inputStream)) {
                    showToast("¡Archivo vacío subido exitosamente!")
                } else {
                    val storeError = ftpClient.replyString
                    showToast("Error al subir el archivo vacío. Detalles: $storeError")
                    Log.i("a",storeError)
                }

                inputStream.close()
                ftpClient.logout()
            } else {
                showToast("No se pudo establecer la conexión FTP.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error durante la operación FTP: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error desconocido: ${e.message}")
        } finally {
            try {
                ftpClient?.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Error al desconectar: ${e.message}")
            }
        }
    }


    private fun uploadFileToFTP(bitmap: Bitmap) {
        var ftpClient: FTPClient? = null
        try {
            ftpClient = FTPClient()
            ftpClient.connect(InetAddress.getByName("81.88.53.117"))
            if (ftpClient.isConnected) {
                showToast("¡Conectado exitosamente!")
                Log.i("conexion ftp", "¡Conectado exitosamente!")
                ftpClient.login("admin_images@appacademia.es", "Velasco9!Velasco9!")
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                val byteArray = getByteArrayFromBitmap(bitmap)
                val inputStream = ByteArrayInputStream(byteArray)

                ftpClient.enterLocalPassiveMode()
                val remoteFileName = "imagen_subida.jpg"
                ftpClient.storeFile(remoteFileName, inputStream)

                inputStream.close()
                ftpClient.logout()
                showToast("¡Archivo subido exitosamente!")
                Log.i("conexion ftp", "¡Archivo subido exitosamente!")

            } else {
                showToast("No se pudo establecer la conexión FTP.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error durante la operación FTP: ${e.message}")
        } finally {
            try {
                ftpClient?.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Error al desconectar: ${e.message}")
            }
        }
    }


    private val startForActivityGalery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            try {
                val bitmap: Bitmap? = if (data != null) {
                    val source = ImageDecoder.createSource(contentResolver, data)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    null
                }

                binding.imageView.setImageBitmap(getResizedBitmap(bitmap, 1024))

                if (bitmap != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        uploadFileToFTP(bitmap)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getResizedBitmap(bitmap: Bitmap?, maxSize: Int): Bitmap? {
        var width = bitmap?.width
        var height = bitmap?.height
        if (width != null) {
            if (height != null) {
                if (width <= maxSize && height <= maxSize ){
                    return bitmap
                }
            }
        }
        var bitmapRatio = width!!.toFloat() / height!!.toFloat()
        if (bitmapRatio > 1){
            width = maxSize
            height = (width/bitmapRatio).toInt()
        }else{
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap!!,width,height,true)
    }

    private fun getStringImage(bitmap: Bitmap?): String{
        var baos: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG,100,baos)
        var imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes,Base64.DEFAULT)
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGalery.launch(intent)
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