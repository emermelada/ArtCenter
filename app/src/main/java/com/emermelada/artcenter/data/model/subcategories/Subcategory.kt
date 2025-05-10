package com.emermelada.artcenter.data.model.subcategories

data class Subcategory(
    val id_categoria: Int,
    val id_subcategoria: Int,
    val nombre: String,
    val historia: String?,
    val caracteristicas: String?,
    val requerimientos: String?,
    val tutoriales: String?
)