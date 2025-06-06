package com.emermelada.artcenter.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsable de manejar la lógica para listar y eliminar categorías.
 *
 * Expone un flujo [categoriesState] que representa el estado de la interfaz de usuario durante
 * las operaciones de obtención y eliminación de categorías.
 *
 * @property categoriesRepository Repositorio que provee acceso a las operaciones remotas sobre categorías.
 */
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    /**
     * Estado de la UI para la lista de categorías.
     *
     * - [UiState.Loading]: cuando se está obteniendo o eliminando categorías.
     * - [UiState.Success]: cuando la obtención es exitosa, contiene la lista de categorías.
     * - [UiState.Error]: cuando ocurre un error, contiene el mensaje de error.
     */
    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val categoriesState: StateFlow<UiState> get() = _categoriesState

    /**
     * Inicia la obtención de todas las categorías desde el repositorio.
     *
     * Actualiza [categoriesState] a [UiState.Loading] antes de la llamada y luego:
     * - Si [CategoriesRepository.getAllCategories] retorna datos, emite [UiState.Success] con la lista.
     * - Si no, emite [UiState.Error] con el mensaje de error.
     */
    fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            val result = categoriesRepository.getAllCategories()
            if (result.data != null) {
                _categoriesState.value = UiState.Success(result.data)
            } else {
                _categoriesState.value = UiState.Error(result.msg ?: "Error al cargar categorías")
            }
        }
    }

    /**
     * Elimina una categoría por su identificador y luego recarga la lista.
     *
     * Actualiza [categoriesState] a [UiState.Loading] antes de la llamada y luego:
     * - Si [CategoriesRepository.deleteCategoryById] retorna código 200 o 201, invoca [fetchCategories] para refrescar la lista.
     * - Si no, emite [UiState.Error] con el mensaje de error.
     *
     * @param id Identificador de la categoría a eliminar.
     */
    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            val result = categoriesRepository.deleteCategoryById(id)
            if (result.code in listOf(200, 201)) {
                fetchCategories()
            } else {
                _categoriesState.value = UiState.Error(result.msg ?: "Error al eliminar categoría")
            }
        }
    }
}
