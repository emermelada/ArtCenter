package com.emermelada.artcenter.data.model.auth

/**
 * Contiene el mensaje devuelto por el servidor tras el proceso de registro de un usuario.
 *
 * @param msg Mensaje que indica el resultado del registro (por ejemplo, confirmación de éxito).
 */
data class RegisterResponse(
    val msg: String
)
