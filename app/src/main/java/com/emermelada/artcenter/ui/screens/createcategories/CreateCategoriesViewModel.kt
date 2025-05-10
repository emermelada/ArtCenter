package com.emermelada.artcenter.ui.screens.createcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: CategoriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
            val result = repository.createCategory(nombre, descripcion)
            if (result.code in listOf(200, 201)) {
                _uiState.value = UiState.Success(result.msg ?: "Categoría creada")
            } else {
                _uiState.value = UiState.Error(result.msg ?: "Error al crear la categoría")
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = UiState.Error(message)
    }
}
