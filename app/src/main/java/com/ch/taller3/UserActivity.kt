package com.ch.taller3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.ActivityUsuarioBinding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserActivity : AppCompatActivity(){
    private lateinit var binding: ActivityUsuarioBinding

    //Instancia FirebaseDatabase
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.saveButton.setOnClickListener {
            //Guardar datos en firebase
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
        }
    }
    
    /*
    * PENDIENTES
    * Traer datos de firebase
    * Boton para actualizar datos: Actualizar datos en firebase
    * 
    * */
}