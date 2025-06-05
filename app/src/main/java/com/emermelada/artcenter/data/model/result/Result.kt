package com.emermelada.artcenter.data.model.result

/**
 * Contenedor genérico para respuestas de API que incluyen datos y metadatos.
 *
 * @param T Tipo del contenido que se devuelve en el campo `data`.
 * @param data Objeto de tipo genérico T con la información solicitada. Puede ser nulo si ocurre un error.
 * @param msg Mensaje informativo o de error proveniente del servidor. Puede ser nulo si no aplica.
 * @param code Código numérico que indica el resultado de la operación (por ejemplo, código de estado HTTP o interno).
 */
data class Result<T>(
    val data: T?,
    val msg: String?,
    val code: Int
)
