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

    // Lista de usuarios activos (nombres de usuarios)
    private var usuariosActivos = mutableListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    // Mapa de usuarios con detalles (nombre -> User)
    private val usuariosDetallados = mutableMapOf<String, User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos Firebase
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {
            userId = user.uid
            databaseReference = FirebaseDatabase.getInstance().reference.child("usuarios")
        }

        //Inicializamos el arrayAdapter
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, usuariosActivos)
        binding.listaUsuariosActivos.adapter = arrayAdapter

        agregarUsuariosActivos()

        //Click en elemento de la lista
        binding.listaUsuariosActivos.setOnItemClickListener { parent, view, position, id ->
            val nombreUsuario = usuariosActivos[position]
            val usuarioSeleccionado = usuariosDetallados[nombreUsuario]

            if (usuarioSeleccionado != null) {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("nombreUsuario", usuarioSeleccionado.nombre)
                intent.putExtra("latitudUsuario", usuarioSeleccionado.latitud)
                intent.putExtra("longitudUsuario", usuarioSeleccionado.longitud)
                startActivity(intent)
            }
        }

        //Estado del usuario, modificamo switch
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

            //Guardamos el estado del usuario en Firebase
            databaseReference.child(userId ?: "").child("estado").setValue(isChecked)
        }

        binding.logOutButton.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.userButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
    }

    //Obtener los usuarios activos
    private fun agregarUsuariosActivos() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usuariosActivos.clear()
                usuariosDetallados.clear()

                for (userSnapshot in dataSnapshot.children) {
                    val usuario = userSnapshot.getValue(User::class.java)
                    if (usuario != null && usuario.estado) {
                        usuariosActivos.add(usuario.nombre)
                        usuariosDetallados[usuario.nombre] = usuario
                    }
                }
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener los usuarios activos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
