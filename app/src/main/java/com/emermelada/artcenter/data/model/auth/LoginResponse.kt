package com.emermelada.artcenter.data.model.auth

data class LoginResponse(
    val token: String,
    val rol: String,
    val id: Int
)