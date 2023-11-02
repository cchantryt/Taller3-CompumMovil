package com.ch.taller3

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ch.taller3.databinding.ActivityMainBinding
import com.ch.taller3.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    //Ubicacion usuario
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el fragmento del mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //Localizar al usuario
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //TODO Separar mapa en varios fragmentos

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //Punto que ubica al usuario
        map.isMyLocationEnabled = true

        //Controles de zoom
        map.uiSettings.isZoomControlsEnabled = true

        //Localizar al usuario
        //Verificamos permisos
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val userLocation = LatLng(it.latitude, it.longitude)
                        map.addMarker(MarkerOptions().position(userLocation).title("Mi Ubicación"))
                        map.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
                        map.animateCamera(CameraUpdateFactory.zoomTo(15f))
                    }
                }
        }
    }
}