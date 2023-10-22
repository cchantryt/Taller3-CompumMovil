package com.ch.taller3.modelos

data class Usuario (
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val contrasena: String = "",
    val numeroIdentificacion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    )