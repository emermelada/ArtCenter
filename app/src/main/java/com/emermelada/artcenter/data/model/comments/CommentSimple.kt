package com.emermelada.artcenter.data.model.comments

data class CommentSimple(
    val id: Int,
    val id_usuario: Int,
    val username: String,
    val contenido: String,
    val fecha_publicacion: String
)
