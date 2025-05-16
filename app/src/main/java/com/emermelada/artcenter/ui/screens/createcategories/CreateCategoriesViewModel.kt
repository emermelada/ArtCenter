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

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle) // Cambiar Loading por Idle
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _categoryState = MutableStateFlow<UiState>(UiState.Idle)
    val categoryState: StateFlow<UiState> = _categoryState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de red: ${throwable.message}"
            is HttpException -> "Error del servidor: ${throwable.message}"
            else -> "Error inesperado: ${throwable.localizedMessage}"
        }
        _uiState.value = UiState.Error(errorMessage)
    }

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

    fun setError(message: String) {
        _uiState.value = UiState.Error(message)
    }

}


