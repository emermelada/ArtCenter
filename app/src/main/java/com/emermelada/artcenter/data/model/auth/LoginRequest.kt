package com.emermelada.artcenter.data.model.auth

/**
 * Representa la información necesaria para realizar una solicitud de inicio de sesión.
 *
 * @param email La dirección de correo electrónico del usuario.
 * @param contrasena La contraseña asociada al usuario.
 */
data class LoginRequest(
    val email: String,
    val contrasena: String
)
