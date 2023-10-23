package com.ch.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ch.taller3.databinding.ActivityInicioSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioSesionBinding

    //Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        //Inicializamos Firebase Auth
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(){
            if (validarCampos()) {
                iniciarSesion()
            }else{
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        binding.registerButton.setOnClickListener(){
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validarCampos(): Boolean {
        if (binding.email.text.toString().isEmpty()) {
            Toast.makeText(this, "Error en email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.password.text.toString().isEmpty() || binding.password.text.toString().length < 6) {
            Toast.makeText(this, "Error en contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun iniciarSesion(){
        auth.signInWithEmailAndPassword(
            binding.email.text.toString(),
            binding.password.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Inicio de sesión fallido
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}  