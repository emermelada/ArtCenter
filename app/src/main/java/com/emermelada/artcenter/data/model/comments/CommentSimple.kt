package com.emermelada.artcenter.data.model.comments

/**
 * Contiene la información básica de un comentario realizado por un usuario.
 *
 * @param id Identificador único del comentario.
 * @param id_usuario Identificador del usuario que realizó el comentario.
 * @param username Nombre de usuario del autor del comentario.
 * @param contenido Texto que compone el comentario.
 * @param fecha_publicacion Fecha y hora en que se publicó el comentario (formato ISO 8601 o similar).
 */
data class CommentSimple(
    val id: Int,
    val id_usuario: Int,
    val username: String,
    val contenido: String,
    val fecha_publicacion: String
)
