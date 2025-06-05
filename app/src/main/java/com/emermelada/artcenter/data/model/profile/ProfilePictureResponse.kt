package com.emermelada.artcenter.data.model.profile

/**
 * Respuesta del servidor al actualizar o solicitar la foto de perfil de un usuario.
 *
 * @param msg Mensaje informativo sobre el resultado de la operación (por ejemplo, confirmación de éxito).
 * @param urlFotoPerfil URL donde se encuentra la nueva foto de perfil del usuario.
 */
data class ProfilePictureResponse(
    val msg: String,
    val urlFotoPerfil: String
)
