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
    private val _subcategoryState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoryState: StateFlow<UiState> = _subcategoryState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val categoriesState: StateFlow<UiState> get() = _categoriesState

    // Nuevo estado para cargar subcategoría para edición
    private val _subcategoryLoadedState = MutableStateFlow<UiState>(UiState.Idle)
    val subcategoryLoadedState: StateFlow<UiState> = _subcategoryLoadedState.asStateFlow()

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
                val categoriaId = (categoriesState.value as? UiState.Success<List<CategorySimple>>)
                    ?.data?.find { it.nombre == categoriaSeleccionada }?.id
                if (categoriaId == null) {
                    _subcategoryState.value = UiState.Error("Categoría no encontrada.")
                    return@launch
                }

                val subcategoryRequest = SubcategoryRequest(
                    id_categoria = categoriaId,
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

    // Nuevo método para cargar subcategoría por id para editar
    fun loadSubcategory(idCategoria: Int, idSubcategoria: Int) {
        viewModelScope.launch {
            _subcategoryLoadedState.value = UiState.Loading
            val result = subcategoriesRepository.getSubcategoryById(idCategoria, idSubcategoria)
            if (result.data != null) {
                _subcategoryLoadedState.value = UiState.Success(result.data)
            } else {
                _subcategoryLoadedState.value = UiState.Error(result.msg ?: "Error al cargar subcategoría")
            }
        }
    }

    // Nuevo método para actualizar subcategoría
    fun updateSubcategory(
        idCategoria: Int,
        idSubcategoria: Int,
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
                val categoriaId = (categoriesState.value as? UiState.Success<List<CategorySimple>>)
                    ?.data?.find { it.nombre == categoriaSeleccionada }?.id
                if (categoriaId == null) {
                    _subcategoryState.value = UiState.Error("Categoría no encontrada.")
                    return@launch
                }

                val subcategory = com.emermelada.artcenter.data.model.subcategories.Subcategory(
                    id_categoria = categoriaId,
                    id_subcategoria = idSubcategoria,
                    nombre = nombre,
                    historia = descripcion,
                    caracteristicas = caracteristicas,
                    requerimientos = requerimientos,
                    tutoriales = tutoriales
                )

                val result = subcategoriesRepository.updateSubcategory(idCategoria, idSubcategoria, subcategory)
                if (result.code in listOf(200, 201)) {
                    _subcategoryState.value = UiState.Success("Subcategoría actualizada correctamente.")
                } else {
                    _subcategoryState.value = UiState.Error(result.msg ?: "Error al actualizar la subcategoría.")
                }
            } catch (e: Exception) {
                _subcategoryState.value = UiState.Error("Error al actualizar la subcategoría")
            }
        }
    }

    fun setError(message: String) {
        _subcategoryState.value = UiState.Error(message)
    }
}
