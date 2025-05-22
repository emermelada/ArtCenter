package com.emermelada.artcenter.data.model.publications

data class Publication(
    val id: Int,
    val urlContenido: String,
    val descripcion: String?,
    val fecha_publicacion: String,
    val likes: Int,
    val etiqueta: String?
)

