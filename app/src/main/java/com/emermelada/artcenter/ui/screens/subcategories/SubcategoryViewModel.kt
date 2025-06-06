package com.emermelada.artcenter.ui.screens.subcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.SubcategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel encargado de manejar la lógica para la pantalla de detalle de una subcategoría.
 *
 * @property subcategoriesRepository Repositorio para obtener datos de subcategorías.
 */
@HiltViewModel
class SubcategoryViewModel @Inject constructor(
    private val subcategoriesRepository: SubcategoriesRepository
) : ViewModel() {

    /**
     * Estado que representa la subcategoría cargada.
     *
     * - UiState.Loading: mientras se realiza la petición de carga.
     * - UiState.Success: cuando la subcategoría se carga correctamente (data: objeto subcategoría).
     * - UiState.Error: si ocurre un error (mensaje en data.msg).
     */
    private val _subcategoryState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoryState: StateFlow<UiState> get() = _subcategoryState

    /**
     * Carga la subcategoría identificada por [idCategoria] e [idSubcategoria].
     *
     * - Actualiza [_subcategoryState] a UiState.Loading antes de la llamada.
     * - Si la respuesta contiene datos, emite UiState.Success con el objeto subcategoría.
     * - En caso contrario, emite UiState.Error con el mensaje correspondiente.
     *
     * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a cargar.
     */
    fun fetchSubcategory(idCategoria: Int, idSubcategoria: Int) {
        viewModelScope.launch {
            _subcategoryState.value = UiState.Loading
            val result = subcategoriesRepository.getSubcategoryById(idCategoria, idSubcategoria)
            if (result.data != null) {
                _subcategoryState.value = UiState.Success(result.data)
            } else {
                _subcategoryState.value = UiState.Error(result.msg ?: "Error al cargar subcategoría")
            }
        }
    }
}
