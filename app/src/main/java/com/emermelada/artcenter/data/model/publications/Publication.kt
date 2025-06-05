package com.emermelada.artcenter.data.model.publications

/**
 * Representa una publicación realizada por un usuario en la plataforma.
 *
 * @param id Identificador único de la publicación.
 * @param urlContenido URL del recurso multimedia asociado (imagen, video, etc.).
 * @param descripcion Texto descriptivo opcional que acompaña a la publicación.
 * @param fecha_publicacion Fecha y hora en que se publicó (en formato ISO 8601 o similar).
 * @param likes Número total de likes que ha recibido la publicación.
 * @param etiqueta Nombre de la etiqueta asociada a la publicación. Puede ser nulo si no tiene etiqueta.
 * @param liked Indica si el usuario actual ha dado like a la publicación.
 * @param saved Indica si el usuario actual ha guardado la publicación.
 */
data class Publication(
    val id: Int,
    val urlContenido: String,
    val descripcion: String?,
    val fecha_publicacion: String,
    val likes: Int,
    val etiqueta: String?,
    val liked: Boolean,
    val saved: Boolean
)
