package com.emermelada.artcenter.data.model.subcategories

data class SubcategoryRequest(
    val id_categoria: Int,
    val nombre: String,
    val historia: String?,
    val caracteristicas: String?,
    val requerimientos: String?,
    val tutoriales: String?
)