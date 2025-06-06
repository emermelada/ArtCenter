package com.emermelada.artcenter.ui.screens.createcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * ViewModel que maneja la lógica para crear, cargar y actualizar categorías.
 *
 * Utiliza [CategoriesRepository] para realizar las operaciones de red,
 * y expone flujos de [UiState] para reflejar el estado de la interfaz de usuario.
 *
 * @property categoriesRepository Repositorio que realiza las operaciones remotas sobre categorías.
 */
@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    /**
     * Estado general de la UI para operaciones de crear o actualizar categoría.
     *
     * - Idle: estado inicial, sin operación en curso.
     * - Loading: operación en curso.
     * - Success: operación completada con éxito, almacena el mensaje resultante.
     * - Error: operación fallida, almacena el mensaje de error.
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Estado de la UI para la operación de carga de una categoría específica.
     *
     * - Idle: estado inicial, sin operación en curso.
     * - Loading: carga en curso.
     * - Success: carga completada con éxito, almacena el objeto [Category].
     * - Error: carga fallida, almacena el mensaje de error.
     */
    private val _categoryState = MutableStateFlow<UiState>(UiState.Idle)
    val categoryState: StateFlow<UiState> = _categoryState.asStateFlow()

    /**
     * Manejador de excepciones para las corrutinas que realizan llamadas a la red.
     *
     * Captura diversos tipos de errores y actualiza [_uiState] con un mensaje descriptivo.
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de red: ${throwable.message}"
            is HttpException -> "Error del servidor: ${throwable.message}"
            else -> "Error inesperado: ${throwable.localizedMessage}"
        }
        _uiState.value = UiState.Error(errorMessage)
    }

    /**
     * Inicia el proceso de creación de una nueva categoría.
     *
     * Actualiza [_uiState] a Loading, luego llama a [CategoriesRepository.createCategory].
     * Si la respuesta tiene código 200 o 201, emite Success con el mensaje resultante.
     * En caso contrario, emite Error con el mensaje de error.
     *
     * @param nombre Nombre de la nueva categoría.
     * @param descripcion Descripción de la nueva categoría.
     */
    fun createCategory(nombre: String, descripcion: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = UiState.Loading
            val result = categoriesRepository.createCategory(nombre, descripcion)
            if (result.code in listOf(200, 201)) {
                _uiState.value = UiState.Success(result.msg ?: "Categoría creada")
            } else {
                _uiState.value = UiState.Error(result.msg ?: "Error al crear la categoría")
            }
        }
    }

    /**
     * Carga una categoría existente por su identificador.
     *
     * Actualiza [_categoryState] a Loading, luego llama a [CategoriesRepository.getCategoryById].
     * Si obtiene datos, emite Success con el objeto [Category].
     * En caso contrario, emite Error con el mensaje de error.
     *
     * @param id Identificador de la categoría a cargar.
     */
    fun loadCategory(id: Int) {
        viewModelScope.launch(exceptionHandler) {
            _categoryState.value = UiState.Loading
            val result = categoriesRepository.getCategoryById(id)
            if (result.data != null) {
                _categoryState.value = UiState.Success(result.data)
            } else {
                _categoryState.value = UiState.Error(result.msg ?: "Error al cargar categorías")
            }
        }
    }

    /**
     * Actualiza una categoría existente con los nuevos valores proporcionados.
     *
     * Actualiza [_uiState] a Loading, luego crea un objeto [Category] con los nuevos datos
     * y llama a [CategoriesRepository.updateCategoryById]. Si la respuesta tiene código 200 o 201,
     * emite Success con el mensaje resultante. En caso contrario, emite Error con el mensaje de error.
     *
     * @param id Identificador de la categoría a actualizar.
     * @param nombre Nuevo nombre para la categoría.
     * @param descripcion Nueva descripción para la categoría.
     */
    fun updateCategory(id: Int, nombre: String, descripcion: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = UiState.Loading
            val category = Category(id, nombre, descripcion)
            val result = categoriesRepository.updateCategoryById(id, category)
            if (result.code in listOf(200, 201)) {
                _uiState.value = UiState.Success(result.msg ?: "Categoría actualizada")
            } else {
                _uiState.value = UiState.Error(result.msg ?: "Error al actualizar la categoría")
            }
        }
    }

    /**
     * Permite establecer manualmente un mensaje de error en el estado de la UI.
     *
     * @param message Mensaje de error a mostrar en la interfaz.
     */
    fun setError(message: String) {
        _uiState.value = UiState.Error(message)
    }
}
