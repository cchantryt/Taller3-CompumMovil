package com.ch.taller3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ch.taller3.models.User


class MapActivity: AppCompatActivity() {
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {
            val userId = user.uid
            databaseReference = FirebaseDatabase.getInstance().reference.child("usuarios").child(userId)
        }

        // Obtenemos la latitud y longitud del usuario autenticado
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usuarioAutenticado = dataSnapshot.getValue(User::class.java)
                if (usuarioAutenticado != null) {
                    // Inicializamos el mapa
                    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync { googleMap ->
                        map = googleMap

                        // Habilitar el botón de ubicación del usuario
                        map.isMyLocationEnabled = true

                        // Habilitar los controles de zoom
                        map.uiSettings.isZoomControlsEnabled = true

                        // Marcador verde para la posición del usuario autenticado
                        val ubicacionUsuarioAutenticado = LatLng(usuarioAutenticado.latitud, usuarioAutenticado.longitud)
                        val marcadorUsuarioAutenticado = MarkerOptions()
                            .position(ubicacionUsuarioAutenticado)
                            .title("Mi Ubicación")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                        map.addMarker(marcadorUsuarioAutenticado)
                        map.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuarioAutenticado))

                        // Cuando un usuario hace clic en otro usuario, deberías recibir los datos de ese usuario y
                        // agregar un marcador azul para la posición del usuario disponible

                        // Obtén los datos del usuario seleccionado de la intención
                        val nombreUsuario = intent.getStringExtra("nombreUsuario")
                        val latitudUsuario = intent.getDoubleExtra("latitudUsuario", 0.0)
                        val longitudUsuario = intent.getDoubleExtra("longitudUsuario", 0.0)

                        // Añade un marcador en el mapa para el usuario seleccionado
                        val ubicacionUsuario = LatLng(latitudUsuario, longitudUsuario)
                        val marcadorUsuario = MarkerOptions()
                            .position(ubicacionUsuario)
                            .title("Usuario: $nombreUsuario")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                        map.addMarker(marcadorUsuario)
                        map.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MapActivity, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
