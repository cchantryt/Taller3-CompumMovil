package com.ch.taller3.models

data class User (
    val nombre: String = "",
    val apellido: String = "",
    val numeroIdentificacion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val estado: Boolean = false
    )