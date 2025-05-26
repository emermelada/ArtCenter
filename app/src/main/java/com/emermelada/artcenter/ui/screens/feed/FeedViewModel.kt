package com.emermelada.artcenter.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.data.repositories.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    private val _publications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val publications: StateFlow<List<PublicationSimple>> = _publications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPublications(page: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener el resultado de la API
                val result = publicationRepository.getAllPublications(page)

                // Verificar si el resultado es exitoso
                if (result.data != null) {
                    // Si es exitoso, agregar las publicaciones
                    _publications.value += result.data
                } else {
                    // Si no, manejar el error (puedes mostrar un mensaje de error o lo que necesites)
                    // Aquí puedes manejar el error si es necesario
                }
            } catch (e: Exception) {
                // Manejo de excepciones si ocurre algún error en la solicitud
                // Aquí podrías agregar lógica para manejar excepciones de red, etc.
            } finally {
                _isLoading.value = false
            }
        }
    }
}
