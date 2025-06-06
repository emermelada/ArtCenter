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

/**
 * ViewModel encargado de gestionar la lógica del feed de publicaciones.
 *
 * - Mantiene un flujo de [PublicationSimple] para las publicaciones cargadas.
 * - Expone un indicador de carga para mostrar el estado de la operación.
 * - Permite cargar páginas de publicaciones, alternar el estado de guardado ("save") y "like",
 *   y eliminar publicaciones del feed.
 *
 * @property publicationRepository Repositorio que proporciona métodos para obtener, guardar, dar like y eliminar publicaciones.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    /**
     * Estado que contiene la lista acumulada de publicaciones mostradas en el feed.
     *
     * - Inicialmente, contiene una lista vacía.
     * - Al cargar una nueva página, los elementos se agregan al final de esta lista.
     */
    private val _publications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val publications: StateFlow<List<PublicationSimple>> = _publications

    /**
     * Indicador booleano que señala si actualmente se está cargando contenido.
     *
     * - `true` mientras se procesa la solicitud de carga.
     * - `false` cuando la operación ha finalizado (exitosamente o no).
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Carga una página de publicaciones y las agrega a la lista existente.
     *
     * - Cambia [_isLoading] a `true` antes de iniciar la petición.
     * - Llama a [PublicationRepository.getAllPublications] con el número de página indicado.
     * - Si la respuesta contiene datos, concatena la lista devuelta con las ya cargadas.
     * - Finalmente, restablece [_isLoading] a `false`.
     *
     * @param page Número de página a cargar (indexada desde 0).
     */
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

    /**
     * Alterna el estado de "guardado" (bookmark) para una publicación específica.
     *
     * - Llama a [PublicationRepository.toggleBookmark] para persistir el cambio en el servidor.
     * - Si el código de respuesta está en 200..299, actualiza localmente la propiedad `saved`
     *   de la publicación en la lista, invirtiendo su valor actual.
     *
     * @param pub Objeto [PublicationSimple] que contiene el identificador y el estado actual de 'saved'.
     */
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

    /**
     * Alterna el estado de "like" para una publicación específica.
     *
     * - Llama a [PublicationRepository.toggleLike] para persistir el cambio en el servidor.
     * - Si el código de respuesta está en 200..299, actualiza localmente la propiedad `liked`
     *   de la publicación en la lista, invirtiendo su valor actual.
     *
     * @param pub Objeto [PublicationSimple] que contiene el identificador y el estado actual de 'liked'.
     */
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

    /**
     * Elimina una publicación del feed.
     *
     * - Llama a [PublicationRepository.deletePublication] con el identificador de la publicación.
     * - Si el código de respuesta está en 200..299, elimina localmente esa publicación de la lista.
     *
     * @param publicationId Identificador de la publicación a eliminar.
     */
    fun deletePublication(publicationId: Int) {
        viewModelScope.launch {
            val result = publicationRepository.deletePublication(publicationId)
            if (result.code in 200..299) {
                _publications.value = _publications.value.filter { it.id != publicationId }
            }
        }
    }
}
