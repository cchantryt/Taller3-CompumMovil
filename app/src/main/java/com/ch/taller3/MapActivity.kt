package com.ch.taller3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ch.taller3.databinding.FragmentMapBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ch.taller3.models.User
import com.google.android.gms.location.LocationServices

class MapActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var marcadorUsuarioAutenticado: Marker? = null
    private var marcadorUsuarioSeleccionado: Marker? = null

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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            // Verifica que se tengan los permisos de ubicación
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) { return@getMapAsync }

            // Boton de localizacion
            map.isMyLocationEnabled = true
            // Zoom
            map.uiSettings.isZoomControlsEnabled = true

            //Actualiza la ubicación del usuario autenticado cada 10 segundos
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000

            // Actualiza la ubicación del usuario autenticado
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    if (location != null) {
                        // Almacena la nueva ubicación en Firebase
                        databaseReference.child("latitud").setValue(location.latitude)
                        databaseReference.child("longitud").setValue(location.longitude)

                        // Actualiza el marcador verde en el mapa
                        val newLocation = LatLng(location.latitude, location.longitude)
                        if (marcadorUsuarioAutenticado != null) {
                            marcadorUsuarioAutenticado!!.remove() // Elimina el marcador anterior
                        }
                        marcadorUsuarioAutenticado = map.addMarker(
                            MarkerOptions()
                                .position(newLocation)
                                .title("Mi Ubicación")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )

                        map.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
                    }
                }
            }, null)

            // Obtén la ubicación inicial del usuario autenticado
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usuarioAutenticado = dataSnapshot.getValue(User::class.java)
                    if (usuarioAutenticado != null) {
                        val ubicacionUsuarioAutenticado = LatLng(usuarioAutenticado.latitud, usuarioAutenticado.longitud)
                        if (marcadorUsuarioAutenticado != null) {
                            // Elimina el marcador anterior
                            marcadorUsuarioAutenticado!!.remove()
                        }
                        marcadorUsuarioAutenticado = map.addMarker(
                            MarkerOptions()
                                .position(ubicacionUsuarioAutenticado)
                                .title("Mi Ubicación")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )

                        map.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuarioAutenticado))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MapActivity, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                }
            })

            // Datos del usuario seleccionado
            val nombreUsuario = intent.getStringExtra("nombreUsuario")
            val latitudUsuario = intent.getDoubleExtra("latitudUsuario", 0.0)
            val longitudUsuario = intent.getDoubleExtra("longitudUsuario", 0.0)

            // Marcador del usuario seleccionado
            val ubicacionUsuario = LatLng(latitudUsuario, longitudUsuario)
            if (marcadorUsuarioSeleccionado != null) {
                marcadorUsuarioSeleccionado!!.remove() // Elimina el marcador anterior
            }
            marcadorUsuarioSeleccionado = map.addMarker(
                MarkerOptions()
                    .position(ubicacionUsuario)
                    .title("Usuario: $nombreUsuario")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

            map.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
        }
    }
}
