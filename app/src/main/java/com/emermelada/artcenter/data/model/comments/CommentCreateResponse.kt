package com.emermelada.artcenter.data.model.comments

/**
 * Respuesta enviada por el servidor después de crear un comentario.
 *
 * @param msg Mensaje informativo sobre el resultado de la creación (por ejemplo, confirmación de éxito).
 * @param id_comentario Identificador único asignado al comentario recién creado.
 */
data class CommentCreateResponse(
    val msg: String,
    val id_comentario: Int
)
