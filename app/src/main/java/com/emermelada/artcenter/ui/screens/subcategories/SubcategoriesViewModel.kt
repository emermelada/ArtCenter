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

@HiltViewModel
class SubcategoriesViewModel @Inject constructor(
    private val subcategoriesRepository: SubcategoriesRepository,
    private val categoriesRepository: CategoriesRepository
): ViewModel() {
    private val _subcategoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoriesState: StateFlow<UiState> get() = _subcategoriesState

    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading)
    val categoriesState: StateFlow<UiState> get() = _categoriesState

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

    fun fetchCategoryById(idCategoria: Int){
        viewModelScope.launch{
            _categoriesState.value = UiState.Loading
            val result = categoriesRepository.getCategoryById(idCategoria)
            if (result.data != null) {
                _categoriesState.value = UiState.Success(result.data)
            } else {
                _categoriesState.value = UiState.Error(result.msg ?: "Error al cargar categorías")
            }
        }
    }
}