package com.emermelada.artcenter.data.model.categories

/**
 * Representa una categoría dentro del sistema de ArtCenter.
 *
 * @param id Identificador único de la categoría.
 * @param nombre Nombre descriptivo de la categoría.
 * @param descripcion Texto que detalla el propósito o uso de la categoría.
 */
data class Category(
    val id: Int,
    val nombre: String,
    val descripcion: String
)
