package com.emermelada.artcenter.data.model.auth

/**
 * Contiene los datos devueltos por el servidor tras un intento de inicio de sesión exitoso.
 *
 * @param token Token de autenticación que se utilizará en peticiones futuras.
 * @param rol Rol asignado al usuario en el sistema.
 * @param id Identificador único del usuario.
 */
data class LoginResponse(
    val token: String,
    val rol: String,
    val id: Int
)
