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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    //Ubicacion
    val SOLICITUD_PERMISO_UBICACION = 100
    private var permisosConcedidos = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //Firebase
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        //Inicializamos Firebase Reference
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios")
        //Inicializamos Firebase Auth
        auth = Firebase.auth

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
    private fun registrarUsuario() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Registro exitoso
                    val user = auth.currentUser
                    if (user != null) {
                        val userId = user.uid // Obtenemos el UID del usuario

                        // Obtener la ubicación del usuario
                        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location: Location? ->
                            if (location != null) {
                                val latitud = location.latitude
                                val longitud = location.longitude

                                // Creamos el objeto usuario
                                val name = binding.name.text.toString()
                                val lastName = binding.lastName.text.toString()
                                val identificationNumber = binding.identificationNumber.text.toString()
                                val usuario = Usuario(name, lastName, identificationNumber, latitud, longitud)

                                // Guardamos los datos en Firebase Realtime Database asociados al correo y contraseña
                                // Reemplazamos los puntos con guiones bajos en el correo para usarlo como clave primaria
                                dbRef.child(email.replace(".", "_")).setValue(usuario)
                                    .addOnCompleteListener {
                                        Toast.makeText(this, "Registrado", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Error en el registro, muestra un mensaje de error
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
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