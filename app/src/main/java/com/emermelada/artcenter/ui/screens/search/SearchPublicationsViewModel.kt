package com.emermelada.artcenter.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.data.repositories.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica de búsqueda de publicaciones con paginación y debounce.
 *
 * - Al llamar a [search], descarta búsquedas previas, reinicia página y resultados, y lanza
 *   la búsqueda tras un retardo de 300 ms.
 * - [loadMore] carga la siguiente página si no está cargando y si hay más resultados.
 * - Ofrece funciones para alternar "like", "save" y eliminar publicaciones.
 *
 * @property repository Repositorio para realizar operaciones sobre publicaciones.
 */
@HiltViewModel
class SearchPublicationsViewModel @Inject constructor(
    private val repository: PublicationRepository
) : ViewModel() {

    /**
     * Lista de publicaciones resultantes de la búsqueda.
     */
    private val _publications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val publications: StateFlow<List<PublicationSimple>> = _publications

    /**
     * Indicador de carga para las operaciones de búsqueda y paginación.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Página actual para la paginación incremental.
     */
    private var currentPage = 0

    /**
     * Query actual que se está buscando.
     *
     * Se compara con nuevas consultas para evitar recargas redundantes.
     */
    private var currentQuery = ""

    /**
     * Indica si aún se pueden cargar más resultados (basado en el tamaño del último lote).
     */
    private var canLoadMore = true

    /**
     * Trabajo de búsqueda en curso, utilizado para debounce.
     */
    private var searchJob: Job? = null

    /**
     * Inicia una nueva búsqueda de publicaciones con el texto [query].
     *
     * - Si [query] coincide con [currentQuery], no hace nada.
     * - Reinicia [currentQuery], [currentPage], [canLoadMore] y la lista interna.
     * - Cancela cualquier búsqueda previa mediante [searchJob] y lanza una nueva corrutina
     *   que espera 300 ms antes de invocar [loadMore], implementando debounce.
     *
     * @param query Texto de búsqueda ingresado por el usuario.
     */
    fun search(query: String) {
        if (query == currentQuery) return

        currentQuery = query
        currentPage = 0
        canLoadMore = true
        _publications.value = emptyList()

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            loadMore()
        }
    }

    /**
     * Carga la siguiente página de resultados para [currentQuery].
     *
     * - No hace nada si ya está cargando, si no se pueden cargar más o si [currentQuery] está en blanco.
     * - Actualiza [_isLoading] a `true` antes de la llamada.
     * - Llama a [PublicationRepository.searchPublications] con [currentQuery] y [currentPage].
     * - Si la respuesta contiene datos, concatena el nuevo listado a [_publications] y aumenta [currentPage].
     *   Si el tamaño del lote es menor a 20, establece [canLoadMore] en `false`.
     * - Si no hay datos, establece [canLoadMore] en `false`.
     * - Al finalizar, actualiza [_isLoading] a `false`.
     */
    fun loadMore() {
        if (_isLoading.value || !canLoadMore || currentQuery.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchPublications(currentQuery, currentPage)
            if (result.data != null) {
                val newList = _publications.value.toMutableList()
                newList.addAll(result.data)
                _publications.value = newList
                currentPage++
                if (result.data.size < 20) {
                    canLoadMore = false
                }
            } else {
                canLoadMore = false
            }
            _isLoading.value = false
        }
    }

    /**
     * Alterna el estado de "like" para [publication].
     *
     * Realiza la llamada a [PublicationRepository.toggleLike] con el id de la publicación.
     * No modifica localmente la lista de resultados (puede implementarse opcionalmente).
     *
     * @param publication Objeto [PublicationSimple] que contiene el identificador de la publicación.
     */
    fun toggleLike(publication: PublicationSimple) {
        viewModelScope.launch {
            repository.toggleLike(publication.id)
        }
    }

    /**
     * Alterna el estado de "save" (guardado) para [publication].
     *
     * Realiza la llamada a [PublicationRepository.toggleBookmark] con el id de la publicación.
     * No modifica localmente la lista de resultados (puede implementarse opcionalmente).
     *
     * @param publication Objeto [PublicationSimple] que contiene el identificador de la publicación.
     */
    fun toggleSave(publication: PublicationSimple) {
        viewModelScope.launch {
            repository.toggleBookmark(publication.id)
        }
    }

    /**
     * Elimina la publicación con [publicationId] de los resultados actuales.
     *
     * - Actualiza [_isLoading] a `true` mientras se realiza la operación.
     * - Llama a [PublicationRepository.deletePublication] con el id de la publicación.
     * - Si el mensaje de resultado indica éxito, elimina el elemento de [_publications].
     * - Finalmente, establece [_isLoading] a `false`.
     *
     * @param publicationId Identificador de la publicación a eliminar.
     */
    fun deletePublication(publicationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deletePublication(publicationId)
            if (result.msg == "Publicación eliminada correctamente") {
                _publications.value = _publications.value.filterNot { it.id == publicationId }
            }
            _isLoading.value = false
        }
    }
}
