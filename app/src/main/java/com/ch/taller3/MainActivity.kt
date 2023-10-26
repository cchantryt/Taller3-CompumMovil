package com.ch.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.ActivityMainBinding
import com.ch.taller3.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Firebase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var userId: String? = null

    private var usuariosActivos = mutableListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos Firebase Reference
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {
            userId = user.uid
            databaseReference = FirebaseDatabase.getInstance().reference.child("usuarios")
        }

        // Inicializa el adaptador de la lista de usuarios activos
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, usuariosActivos)
        binding.listaUsuariosActivos.adapter = arrayAdapter

        // Agregar usuarios activos
        agregarUsuariosActivos()

        // Obtener y mostrar el estado actual del usuario desde Firebase Realtime Database y actualizar el Switch
        databaseReference.child(userId ?: "").child("estado").get().addOnSuccessListener { dataSnapshot ->
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
            databaseReference.child(userId ?: "").child("estado").setValue(isChecked)
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
    }

    // Función que agrega los nombres de usuarios activos a la lista
    private fun agregarUsuariosActivos() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usuariosActivos.clear() // Borra la lista antes de cargarla nuevamente

                for (userSnapshot in dataSnapshot.children) {
                    val usuario = userSnapshot.getValue(User::class.java)
                    if (usuario != null && usuario.estado) {
                        usuariosActivos.add(usuario.nombre)
                    }
                }

                // Notifica al adaptador que los datos han cambiado
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener los usuarios activos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*
    * TODO
    *  Hacer los elementos de la lista clickeables y que muestren la ubicación del usuario
    * */
}
