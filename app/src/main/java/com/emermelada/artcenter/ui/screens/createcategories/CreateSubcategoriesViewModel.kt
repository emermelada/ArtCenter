package com.emermelada.artcenter.ui.screens.createcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.subcategories.SubcategoryRequest
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.data.repositories.SubcategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSubcategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val subcategoriesRepository: SubcategoriesRepository
) : ViewModel() {
    // Estado para la creación de subcategorías
    private val _subcategoryState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoryState: StateFlow<UiState> = _subcategoryState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val categoriesState: StateFlow<UiState> get() = _categoriesState

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

    fun createSubcategory(
        nombre: String,
        descripcion: String,
        caracteristicas: String,
        requerimientos: String,
        tutoriales: String,
        categoriaSeleccionada: String
    ) {
        viewModelScope.launch {
            _subcategoryState.value = UiState.Loading
            try {
                // Encontrar el ID de la categoría seleccionada
                val categoriaId = (categoriesState.value as? UiState.Success<List<CategorySimple>>)
                    ?.data?.find { it.nombre == categoriaSeleccionada }?.id
                if (categoriaId == null) {
                    _subcategoryState.value = UiState.Error("Categoría no encontrada.")
                    return@launch
                }

                val subcategoryRequest = SubcategoryRequest(
                    id_categoria = categoriaId,  // Usar el id de la categoría seleccionada
                    nombre = nombre,
                    historia = descripcion,
                    caracteristicas = caracteristicas,
                    requerimientos = requerimientos,
                    tutoriales = tutoriales
                )

                val result = subcategoriesRepository.createSubcategory(subcategoryRequest)
                if (result.code in listOf(200, 201)) {
                    _subcategoryState.value = UiState.Success("Subcategoría creada correctamente.")
                } else {
                    _subcategoryState.value = UiState.Error(result.msg ?: "Error al crear la subcategoría.")
                }
            } catch (e: Exception) {
                _subcategoryState.value = UiState.Error("Error al crear la subcategoría")
            }
        }
    }

    fun setError(message: String) {
        _subcategoryState.value = UiState.Error(message)
    }
}
