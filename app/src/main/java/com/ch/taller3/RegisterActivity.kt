package com.ch.taller3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.ActivityRegistroBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
}