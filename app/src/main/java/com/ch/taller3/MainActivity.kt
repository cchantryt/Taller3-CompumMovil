package com.ch.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Firebase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos Firebase Reference
        mAuth = FirebaseAuth.getInstance()
        // Obtenemos la referencia del usuario actual
        val user = mAuth.currentUser
        val email = user?.email


        if (user != null) {
            userId = user.uid // Obtén el UID del usuario
            databaseReference = FirebaseDatabase.getInstance().reference.child("usuarios").child(userId!!)
        }

        binding.logOutButton.setOnClickListener {
            // Cerramos sesión
            mAuth.signOut()

            // Pasamos a la actividad de inicio de sesión
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.userButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }

        // Switch
        // Obtener el estado actual del usuario desde Firebase Realtime Database y actualizar el Switch
        databaseReference.child("activo").get().addOnSuccessListener { dataSnapshot ->
            val isActive = dataSnapshot.value as? Boolean
            if (isActive != null) {
                binding.status.isChecked = isActive
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener el estado del usuario", Toast.LENGTH_SHORT).show()
        }

        binding.status.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Activo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Inactivo", Toast.LENGTH_SHORT).show()
            }

            // Guardar el estado del Switch en Firebase Realtime Database
            databaseReference.child("estado").setValue(isChecked)
        }
    }
}
