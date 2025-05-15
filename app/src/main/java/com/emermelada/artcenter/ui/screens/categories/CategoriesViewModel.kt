package com.emermelada.artcenter.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.data.repositories.PreferencesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
): ViewModel() {
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

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            val result = categoriesRepository.deleteCategoryById(id)
            if (result.code in listOf(200, 201)) {
                // Tras borrar, recarga las categorías
                fetchCategories()
            } else {
                _categoriesState.value = UiState.Error(result.msg ?: "Error al eliminar categoría")
            }
        }
    }
}