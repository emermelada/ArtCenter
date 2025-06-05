package com.emermelada.artcenter.data.model.comments

/**
 * Respuesta enviada por el servidor después de eliminar un comentario.
 *
 * @param msg Mensaje informativo sobre el resultado de la eliminación (por ejemplo, confirmación de éxito).
 */
data class CommentDeleteResponse(
    val msg: String
)
