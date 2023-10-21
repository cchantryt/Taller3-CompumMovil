package com.ch.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ch.taller3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(){
            if (validarCampos()) {
                Toast.makeText(this, "Inicio sesion", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Campos vacios", Toast.LENGTH_SHORT).show()
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
}  