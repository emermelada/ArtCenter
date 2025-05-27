package com.emermelada.artcenter.data.model.publications

data class PublicationSimple(
    val id: Int,
    val urlContenido: String,
    val id_etiqueta: Int,
    val nombre_etiqueta: String?,
    val id_categoria: Int,
    val id_subcategoria: Int?,
    val liked: Boolean,
    val saved: Boolean
)