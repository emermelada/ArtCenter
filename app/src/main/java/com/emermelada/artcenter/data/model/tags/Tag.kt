package com.emermelada.artcenter.data.model.tags

/**
 * Representa una etiqueta que se puede asociar a publicaciones u otros elementos.
 *
 * @param id Identificador único de la etiqueta.
 * @param nombre Nombre descriptivo de la etiqueta.
 */
data class Tag(
    val id: Int,
    val nombre: String
)
