package com.emermelada.artcenter.ui.screens.publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.publications.PublicationRequest
import com.emermelada.artcenter.data.model.tags.Tag
import com.emermelada.artcenter.data.repositories.PublicationRepository
import com.emermelada.artcenter.data.repositories.TagsRepository
import com.emermelada.artcenter.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PublicationViewModel(
    private val publicationRepository: PublicationRepository = PublicationRepository(),
    private val tagsRepository: TagsRepository = TagsRepository()
) : ViewModel() {

    private val _tagsState = MutableStateFlow<UiState>(UiState.Loading)
    val tagsState: StateFlow<UiState> = _tagsState.asStateFlow()

    private val _uploadState = MutableStateFlow<UiState>(UiState.Idle)
    val uploadState: StateFlow<UiState> = _uploadState.asStateFlow()

    var selectedTagId: Int? = null

    fun fetchTags() {
        viewModelScope.launch {
            _tagsState.value = UiState.Loading
            val result = tagsRepository.getAllTags()
            if (result.data != null) {
                _tagsState.value = UiState.Success(result.data)
            } else {
                _tagsState.value = UiState.Error(result.msg ?: "Error cargando etiquetas")
            }
        }
    }

    fun uploadPublication(imageFile: File, descripcion: String) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            try {
                val publicationRequest = PublicationRequest(
                    descripcion = descripcion.takeIf { it.isNotBlank() },
                    id_etiqueta = selectedTagId
                )

                val result = publicationRepository.createPublication(imageFile, publicationRequest)

                if (result.code in listOf(200, 201)) {
                    _uploadState.value = UiState.Success("Publicación creada correctamente")
                } else {
                    _uploadState.value = UiState.Error(result.msg ?: "Error al crear publicación")
                }
            } catch (e: Exception) {
                _uploadState.value = UiState.Error("Error al subir la publicación: ${e.localizedMessage}")
            }
        }
    }
}
