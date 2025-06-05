package com.emermelada.artcenter.data.model.profile

/**
 * Representa la información básica de un usuario.
 *
 * @param username Nombre de usuario único en la plataforma.
 * @param urlFotoPerfil URL de la foto de perfil del usuario. Puede ser nulo si no tiene foto.
 */
data class User(
    val username: String,
    val urlFotoPerfil: String?
)
