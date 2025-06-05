package com.emermelada.artcenter.data.model.categories

/**
 * Representa la información mínima de una categoría.
 *
 * @param id Identificador único de la categoría.
 * @param nombre Nombre descriptivo de la categoría.
 */
data class CategorySimple(
    val id: Int,
    val nombre: String
)
