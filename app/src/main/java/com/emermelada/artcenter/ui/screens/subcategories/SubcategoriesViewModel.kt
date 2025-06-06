package com.emermelada.artcenter.ui.screens.subcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.data.repositories.SubcategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel encargado de manejar la lógica para la pantalla de subcategorías.
 *
 * - Carga la lista de subcategorías asociadas a una categoría específica.
 * - Carga la información de la categoría padre.
 * - Permite eliminar subcategorías y recargar la lista tras la eliminación.
 *
 * @property subcategoriesRepository Repositorio para obtener, eliminar y actualizar subcategorías.
 * @property categoriesRepository Repositorio para obtener información de la categoría padre.
 */
@HiltViewModel
class SubcategoriesViewModel @Inject constructor(
    private val subcategoriesRepository: SubcategoriesRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    /**
     * Estado que representa la lista de subcategorías para una categoría específica.
     *
     * - [UiState.Loading] mientras se realiza la petición.
     * - [UiState.Success] cuando la lista se carga correctamente (data: lista de subcategorías).
     * - [UiState.Error] si ocurre un error (mensaje en data.msg).
     */
    private val _subcategoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoriesState: StateFlow<UiState> get() = _subcategoriesState

    /**
     * Estado que representa la información de la categoría padre.
     *
     * - [UiState.Loading] mientras se realiza la petición.
     * - [UiState.Success] cuando la categoría se carga correctamente (data: objeto categoría).
     * - [UiState.Error] si ocurre un error (mensaje en data.msg).
     */
    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val categoriesState: StateFlow<UiState> get() = _categoriesState

    /**
     * Obtiene la lista de subcategorías correspondientes a la categoría cuyo ID es [idCategoria].
     *
     * - Actualiza [_subcategoriesState] a [UiState.Loading] antes de la llamada.
     * - Si la respuesta contiene datos, emite [UiState.Success] con la lista de subcategorías.
     * - Si ocurre un error o no hay datos, emite [UiState.Error] con el mensaje correspondiente.
     *
     * @param idCategoria Identificador de la categoría padre cuyas subcategorías se desean cargar.
     */
    fun fetchSubcategories(idCategoria: Int) {
        viewModelScope.launch {
            _subcategoriesState.value = UiState.Loading
            val result = subcategoriesRepository.getSubcategoriesByCategory(idCategoria)
            if (result.data != null) {
                _subcategoriesState.value = UiState.Success(result.data)
            } else {
                _subcategoriesState.value = UiState.Error(result.msg ?: "Error al cargar subcategorías")
            }
        }
    }

    /**
     * Obtiene la información de la categoría cuyo ID es [idCategoria].
     *
     * - Actualiza [_categoriesState] a [UiState.Loading] antes de la llamada.
     * - Si la respuesta contiene datos, emite [UiState.Success] con el objeto categoría.
     * - Si ocurre un error o no hay datos, emite [UiState.Error] con el mensaje correspondiente.
     *
     * @param idCategoria Identificador de la categoría a cargar.
     */
    fun fetchCategoryById(idCategoria: Int) {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            val result = categoriesRepository.getCategoryById(idCategoria)
            if (result.data != null) {
                _categoriesState.value = UiState.Success(result.data)
            } else {
                _categoriesState.value = UiState.Error(result.msg ?: "Error al cargar categorías")
            }
        }
    }

    /**
     * Elimina la subcategoría identificada por [idSubcategoria] dentro de la categoría [idCategoria].
     *
     * - Actualiza [_subcategoriesState] a [UiState.Loading] antes de la llamada.
     * - Si la eliminación es exitosa (código 200 o 201), recarga la lista de subcategorías mediante [fetchSubcategories].
     * - Si ocurre un error, emite [UiState.Error] con el mensaje correspondiente.
     *
     * @param idCategoria Identificador de la categoría padre de la subcategoría a eliminar.
     * @param idSubcategoria Identificador de la subcategoría que se desea eliminar.
     */
    fun deleteSubcategory(idCategoria: Int, idSubcategoria: Int) {
        viewModelScope.launch {
            _subcategoriesState.value = UiState.Loading
            val result = subcategoriesRepository.deleteSubcategory(idCategoria, idSubcategoria)
            if (result.code in listOf(200, 201)) {
                fetchSubcategories(idCategoria)
            } else {
                _subcategoriesState.value = UiState.Error(result.msg ?: "Error al eliminar subcategoría")
            }
        }
    }
}
