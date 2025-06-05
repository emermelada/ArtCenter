package com.emermelada.artcenter.ui

/**
 * Representa el estado de la interfaz de usuario para operaciones asíncronas o datos cargados.
 *
 * Puede encontrarse en los siguientes estados:
 * - [Idle]: Estado inicial, sin operaciones en curso.
 * - [Loading]: Indica que se está cargando o esperando una respuesta.
 * - [Success]: Indica que la operación fue exitosa y contiene los datos resultantes.
 * - [Error]: Indica que la operación falló y contiene un mensaje de error.
 */
sealed class UiState {

    /**
     * Estado inicial o neutro, sin operaciones en curso.
     */
    data object Idle : UiState()

    /**
     * Estado que indica que una operación se está procesando o cargando.
     */
    data object Loading : UiState()

    /**
     * Estado que indica que la operación fue exitosa.
     *
     * @param T Tipo de los datos devueltos.
     * @param data Datos resultantes de la operación exitosa.
     */
    data class Success<T>(val data: T) : UiState()

    /**
     * Estado que indica que ocurrió un error durante la operación.
     *
     * @param message Mensaje descriptivo del error ocurrido.
     */
    data class Error(val message: String) : UiState()
}
