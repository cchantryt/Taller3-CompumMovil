package com.ch.taller3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.ActivityRegistroBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ch.taller3.modelos.Usuario
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    //Ubicacion
    val SOLICITUD_PERMISO_UBICACION = 100
    private var permisosConcedidos = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //Firebase
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        //Inicializamos Firebase Reference
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios")

        //Inicializamos FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener(){
            if (validarCampos()) {
                solicitarPermisoUbicacion()
            }else{
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (binding.name.text.toString().isEmpty()) {
            Toast.makeText(this, "Error en nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.lastName.text.toString().isEmpty()) {
            Toast.makeText(this, "Error en apellido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.email.text.toString().isEmpty()) {
            Toast.makeText(this, "Error en email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.password.text.toString().isEmpty() || binding.password.text.toString().length < 6) {
            Toast.makeText(this, "Error en contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.identificationNumber.text.toString().isEmpty()) {
            Toast.makeText(this, "Error en identificacion", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Funciones de Firebase
    private fun registrarUsuario(){
        val userId = dbRef.push().key
        val name = binding.name.text.toString()
        val lastName = binding.lastName.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val identificationNumber = binding.identificationNumber.text.toString()
        var latitud = 0.0
        var longitud = 0.0

        // Obtener la ubicación del usuario
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location: Location? ->
            if (location != null) {
                latitud = location.latitude
                longitud = location.longitude
            } else {
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }

        //Creamos el objeto usuario
        val usuario = Usuario(name, lastName, email, password, identificationNumber, latitud, longitud)

        //Guardamos el usuario en la base de datos
        dbRef.child(userId!!).setValue(usuario)
            .addOnCompleteListener{
                Toast.makeText(this, "Registrado", Toast.LENGTH_SHORT).show()
            }
    }

    //Funciones de ubicacion
    private fun solicitarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                SOLICITUD_PERMISO_UBICACION
            )
        } else {
            //Permisos ya concedidos
            permisosConcedidos = true
            registrarUsuario()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SOLICITUD_PERMISO_UBICACION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                permisosConcedidos = true
                registrarUsuario()
            } else {
                //Permiso denegado
                Toast.makeText(this, "Se requieren permisos de ubicación para continuar", Toast.LENGTH_LONG).show()
            }
        }
    }

}