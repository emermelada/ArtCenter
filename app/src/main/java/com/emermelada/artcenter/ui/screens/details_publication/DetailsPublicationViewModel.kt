package com.emermelada.artcenter.ui.screens.details_publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.CommentRepository
import com.emermelada.artcenter.data.repositories.PublicationRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsPublicationViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    // 1) Estado original de la publicación (sin liked/saved) para cargar datos base
    private val _publicationState = MutableStateFlow<UiState>(UiState.Loading)
    val publicationState: StateFlow<UiState> = _publicationState.asStateFlow()

    // 2) Estados independientes de “liked” y “saved”
    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    // 3) Contador de “likes” local, para mostrar en la UI sin recargar todo
    private val _likesCount = MutableStateFlow(0)
    val likesCount: StateFlow<Int> = _likesCount.asStateFlow()

    // 4) Estado de comentarios (como antes)
    private val _commentsState = MutableStateFlow<UiState>(UiState.Loading)
    val commentsState: StateFlow<UiState> = _commentsState.asStateFlow()

    // 5) Estado de “añadir comentario” (como antes)
    private val _addCommentState = MutableStateFlow<UiState>(UiState.Idle)
    val addCommentState: StateFlow<UiState> = _addCommentState.asStateFlow()

    /**
     * Carga la publicación completa y parametriza los estados de liked/saved/likesCount.
     */
    fun loadPublication(id: Int) {
        viewModelScope.launch {
            _publicationState.value = UiState.Loading
            try {
                val result = publicationRepository.getPublicationById(id)
                if (result.data != null) {
                    val pub = result.data
                    _publicationState.value = UiState.Success(pub)
                    // Inicializamos liked/saved/likesCount según lo que devolvió el servidor
                    _isLiked.value = pub.liked
                    _isSaved.value = pub.saved
                    _likesCount.value = pub.likes
                } else {
                    _publicationState.value = UiState.Error(result.msg ?: "Error cargando publicación")
                }
            } catch (e: Exception) {
                _publicationState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Carga comentarios (igual que antes).
     */
    fun loadComments(publicationId: Int) {
        viewModelScope.launch {
            _commentsState.value = UiState.Loading
            try {
                val result = commentRepository.getCommentsByPublication(publicationId)
                if (result.data != null) {
                    _commentsState.value = UiState.Success(result.data)
                } else {
                    _commentsState.value = UiState.Error(result.msg ?: "Error cargando comentarios")
                }
            } catch (e: Exception) {
                _commentsState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Añade un comentario (igual que antes).
     */
    fun addComment(publicationId: Int, contenido: String) {
        viewModelScope.launch {
            _addCommentState.value = UiState.Loading
            try {
                val result = commentRepository.createComment(publicationId, contenido)
                if (result.data != null) {
                    _addCommentState.value = UiState.Success(result.data)
                } else {
                    _addCommentState.value = UiState.Error(result.msg ?: "Error al agregar comentario")
                }
            } catch (e: Exception) {
                _addCommentState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Alterna el “like” sin recargar toda la publicación.
     * - Actualiza localmente _isLiked y _likesCount.
     * - Llama al repositorio para persistir el cambio en el servidor.
     */
    fun toggleLike(publicationId: Int) {
        viewModelScope.launch {
            // Optimismo: invertimos el estado localmente antes de la llamada de red
            val nextLiked = !_isLiked.value
            _isLiked.value = nextLiked
            // Ajustamos localmente el contador de likes
            _likesCount.value = _likesCount.value + if (nextLiked) +1 else -1

            // Llamamos al repositorio; si falla, revertimos
            try {
                val result = publicationRepository.toggleLike(publicationId)
                if (result.code !in 200..299) {
                    // Si hubo error, revertimos
                    _isLiked.value = !nextLiked
                    _likesCount.value = _likesCount.value + if (nextLiked) -1 else +1
                }
            } catch (_: Exception) {
                // En caso de excepción, revertimos
                _isLiked.value = !nextLiked
                _likesCount.value = _likesCount.value + if (nextLiked) -1 else +1
            }
        }
    }

    /**
     * Alterna el “guardado” (bookmark) sin recargar toda la publicación.
     * - Actualiza localmente _isSaved.
     * - Llama al repositorio para persistir en el servidor. Si falla, revertimos.
     */
    fun toggleBookmark(publicationId: Int) {
        viewModelScope.launch {
            val nextSaved = !_isSaved.value
            _isSaved.value = nextSaved

            try {
                val result = publicationRepository.toggleBookmark(publicationId)
                if (result.code !in 200..299) {
                    // Si hay error, revertimos
                    _isSaved.value = !nextSaved
                }
            } catch (_: Exception) {
                // En caso de excepción, revertimos
                _isSaved.value = !nextSaved
            }
        }
    }

    fun deleteComment(commentId: Int, publicationId: Int) {
        viewModelScope.launch {
            try {
                val result = commentRepository.deleteComment(commentId)
                if (result.code == 200) {
                    loadComments(publicationId)
                }else {
                    _commentsState.value = UiState.Error(result.msg ?: "Error eliminando comentario")
                }
            } catch (e: Exception) {
                _commentsState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}
