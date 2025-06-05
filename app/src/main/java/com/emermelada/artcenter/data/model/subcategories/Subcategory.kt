package com.emermelada.artcenter.data.model.subcategories

/**
 * Representa una subcategoría dentro de una categoría especificada.
 *
 * @param id_categoria Identificador de la categoría a la que pertenece esta subcategoría.
 * @param id_subcategoria Identificador único de la subcategoría.
 * @param nombre Nombre descriptivo de la subcategoría.
 * @param historia Texto que describe la historia o contexto de la subcategoría. Puede ser nulo si no aplica.
 * @param caracteristicas Texto con las características principales de la subcategoría. Puede ser nulo si no aplica.
 * @param requerimientos Texto que detalla los requerimientos necesarios para la subcategoría. Puede ser nulo si no aplica.
 * @param tutoriales Texto con enlaces o descripciones de tutoriales relacionados. Puede ser nulo si no aplica.
 */
data class Subcategory(
    val id_categoria: Int,
    val id_subcategoria: Int,
    val nombre: String,
    val historia: String?,
    val caracteristicas: String?,
    val requerimientos: String?,
    val tutoriales: String?
)
