package com.emermelada.artcenter.data.model.profile

/**
 * Petición para actualizar datos de perfil de un usuario.
 *
 * @param username Nuevo nombre de usuario. Puede ser nulo si no se desea modificar.
 * @param urlFotoPerfil Nueva URL de la foto de perfil. Puede ser nulo si no se desea modificar.
 */
data class UserUpdateRequest(
    val username: String?,
    val urlFotoPerfil: String?
)
