package com.emermelada.artcenter.data.model.subcategories

/**
 * Petición para crear o actualizar una subcategoría dentro de una categoría.
 *
 * @param id_categoria Identificador de la categoría a la que se asignará la nueva subcategoría.
 * @param nombre Nombre descriptivo de la subcategoría.
 * @param historia Texto que describe la historia o contexto de la subcategoría. Puede ser nulo si no se provee.
 * @param caracteristicas Texto con las características principales de la subcategoría. Puede ser nulo si no se provee.
 * @param requerimientos Texto que detalla los requerimientos necesarios para la subcategoría. Puede ser nulo si no se provee.
 * @param tutoriales Texto con enlaces o descripciones de tutoriales relacionados. Puede ser nulo si no se provee.
 */
data class SubcategoryRequest(
    val id_categoria: Int,
    val nombre: String,
    val historia: String?,
    val caracteristicas: String?,
    val requerimientos: String?,
    val tutoriales: String?
)
