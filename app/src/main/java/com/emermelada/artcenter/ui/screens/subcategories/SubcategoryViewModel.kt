package com.emermelada.artcenter.ui.screens.subcategories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.SubcategoriesRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubcategoryViewModel @Inject constructor(
    private val subcategoriesRepository: SubcategoriesRepository
): ViewModel(){
    private val _subcategoryState = MutableStateFlow<UiState>(UiState.Loading)
    val subcategoryState: StateFlow<UiState> get() = _subcategoryState

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