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
import com.google.android.gms.maps.OnMapReadyCallback
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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    // Ubicacion
    private lateinit var map: GoogleMap
    // Firebase
    private lateinit var binding: FragmentMapBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    // Marcadores
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
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        map.isMyLocationEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    databaseReference.child("latitud").setValue(location.latitude)
                    databaseReference.child("longitud").setValue(location.longitude)

                    val newLocation = LatLng(location.latitude, location.longitude)
                    marcadorUsuarioAutenticado?.remove()
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

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usuarioAutenticado = dataSnapshot.getValue(User::class.java)
                if (usuarioAutenticado != null) {
                    val ubicacionUsuarioAutenticado = LatLng(usuarioAutenticado.latitud, usuarioAutenticado.longitud)
                    marcadorUsuarioAutenticado?.remove()
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

        val nombreUsuario = intent.getStringExtra("nombreUsuario")
        val latitudUsuario = intent.getDoubleExtra("latitudUsuario", 0.0)
        val longitudUsuario = intent.getDoubleExtra("longitudUsuario", 0.0)

        val ubicacionUsuario = LatLng(latitudUsuario, longitudUsuario)
        marcadorUsuarioSeleccionado?.remove()
        marcadorUsuarioSeleccionado = map.addMarker(
            MarkerOptions()
                .position(ubicacionUsuario)
                .title("Usuario: $nombreUsuario")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )

        map.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
    }

    /*
    * TODO
    *  La ubicacion del usuario disponible solo se actualiza cuando se vuelve a iniciar sesion
    */
}
