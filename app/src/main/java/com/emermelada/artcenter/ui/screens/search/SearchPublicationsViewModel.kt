package com.emermelada.artcenter.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.data.repositories.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SearchPublicationsViewModel @Inject constructor(
    private val repository: PublicationRepository
) : ViewModel() {

    private val _publications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val publications: StateFlow<List<PublicationSimple>> = _publications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentPage = 0
    private var currentQuery = ""
    private var canLoadMore = true

    private var searchJob: Job? = null

    fun search(query: String) {
        if (query == currentQuery) return // mismo query, no hacer nada

        currentQuery = query
        currentPage = 0
        canLoadMore = true
        _publications.value = emptyList()

        // Cancelar búsqueda anterior si existe y esperar debounce para no saturar API
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce 300ms
            loadMore()
        }
    }

    fun loadMore() {
        if (_isLoading.value || !canLoadMore || currentQuery.isBlank()) return

        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.searchPublications(currentQuery, currentPage)
            if (result.data != null) {
                val newList = _publications.value.toMutableList()
                newList.addAll(result.data)
                _publications.value = newList
                currentPage++

                // Si devuelve menos que página completa, no hay más páginas
                if (result.data.size < 20) canLoadMore = false
            } else {
                canLoadMore = false
            }
            _isLoading.value = false
        }
    }

    fun toggleLike(publication: PublicationSimple) {
        viewModelScope.launch {
            repository.toggleLike(publication.id)
            // Opcional: actualizar UI localmente
        }
    }

    fun toggleSave(publication: PublicationSimple) {
        viewModelScope.launch {
            repository.toggleBookmark(publication.id)
            // Opcional: actualizar UI localmente
        }
    }
}
