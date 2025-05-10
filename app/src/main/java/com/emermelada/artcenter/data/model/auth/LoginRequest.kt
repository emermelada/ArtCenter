package com.emermelada.artcenter.data.model.auth

data class LoginRequest(
    val email: String,
    val contrasena: String
)