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

// FeedViewModel.kt
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
                val result = publicationRepository.getAllPublications(page)
                result.data?.let { list ->
                    _publications.value = _publications.value + list
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSave(pub: PublicationSimple) {
        viewModelScope.launch {
            val result = publicationRepository.toggleBookmark(pub.id)
            if (result.code in 200..299) {
                _publications.value = _publications.value.map {
                    if (it.id == pub.id) it.copy(saved = !pub.saved) else it
                }
            }
        }
    }

    fun toggleLike(pub: PublicationSimple) {
        viewModelScope.launch {
            val result = publicationRepository.toggleLike(pub.id)
            if (result.code in 200..299) {
                _publications.value = _publications.value.map {
                    if (it.id == pub.id) it.copy(liked = !pub.liked) else it
                }
            }
        }
    }

    // Nuevo método para eliminar una publicación
    fun deletePublication(publicationId: Int) {
        viewModelScope.launch {
            val result = publicationRepository.deletePublication(publicationId)
            if (result.code in 200..299) {
                _publications.value = _publications.value.filter { it.id != publicationId }
            }
        }
    }
}

