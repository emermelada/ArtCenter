package com.emermelada.artcenter.data.model.auth

data class RegisterRequest (
    val email: String,
    val contrasena: String,
    val username: String
)