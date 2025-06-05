package com.emermelada.artcenter.data.model.comments

/**
 * Solicitud para crear un nuevo comentario en la plataforma.
 *
 * @param contenido Texto que contendrá el comentario que el usuario desea publicar.
 */
data class CommentCreateRequest(
    val contenido: String
)
