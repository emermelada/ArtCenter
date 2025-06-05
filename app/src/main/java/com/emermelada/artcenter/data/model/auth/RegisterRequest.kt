package com.emermelada.artcenter.data.model.auth

/**
 * Representa la información necesaria para registrar un nuevo usuario en el sistema.
 *
 * @param email La dirección de correo electrónico del nuevo usuario.
 * @param contrasena La contraseña que el usuario elegirá para su cuenta.
 * @param username El nombre de usuario que identificará al usuario en la aplicación.
 */
data class RegisterRequest(
    val email: String,
    val contrasena: String,
    val username: String
)
