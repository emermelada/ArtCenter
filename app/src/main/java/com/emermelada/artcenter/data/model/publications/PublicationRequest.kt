package com.emermelada.artcenter.data.model.publications

/**
 * Petición para crear o actualizar una publicación.
 *
 * @param descripcion Texto descriptivo que acompañará a la publicación. Puede ser nulo si se omite.
 * @param id_etiqueta Identificador de la etiqueta que se asignará. Puede ser nulo si no se desea asignar etiqueta.
 */
data class PublicationRequest(
    val descripcion: String?,
    val id_etiqueta: Int?
)
