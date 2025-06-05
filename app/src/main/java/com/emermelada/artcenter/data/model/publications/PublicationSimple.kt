package com.emermelada.artcenter.data.model.publications

/**
 * Información mínima necesaria para listar publicaciones sin incluir detalles adicionales.
 *
 * @param id Identificador único de la publicación.
 * @param urlContenido URL del recurso multimedia asociado a la publicación.
 * @param id_etiqueta Identificador de la etiqueta vinculada a esta publicación.
 * @param nombre_etiqueta Nombre de la etiqueta asociada. Puede ser nulo si la etiqueta no tiene nombre o no está presente.
 * @param id_categoria Identificador de la categoría a la que pertenece la publicación.
 * @param id_subcategoria Identificador de la subcategoría a la que pertenece la publicación. Puede ser nulo si no aplica.
 * @param liked Indica si el usuario actual ha dado like a la publicación.
 * @param saved Indica si el usuario actual ha guardado la publicación.
 * @param id_usuario Identificador del usuario que creó la publicación.
 */
data class PublicationSimple(
    val id: Int,
    val urlContenido: String,
    val id_etiqueta: Int,
    val nombre_etiqueta: String?,
    val id_categoria: Int,
    val id_subcategoria: Int?,
    val liked: Boolean,
    val saved: Boolean,
    val id_usuario: Int
)
