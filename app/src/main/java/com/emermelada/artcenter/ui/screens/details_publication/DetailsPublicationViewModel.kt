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

/**
 * ViewModel que gestiona la lógica y el estado para la pantalla de detalle de publicación.
 *
 * - Carga la publicación completa (incluyendo estados iniciales de liked, saved y contador de likes).
 * - Carga y administra comentarios relacionados con la publicación.
 * - Permite alternar el estado de "like" y "saved" de manera local y sincronizar con el servidor.
 * - Elimina comentarios y actualiza la lista tras la eliminación.
 *
 * @property publicationRepository Repositorio para obtener detalles de la publicación y acciones relacionadas (like, bookmark).
 * @property commentRepository Repositorio para obtener, crear y eliminar comentarios asociados a la publicación.
 */
@HiltViewModel
class DetailsPublicationViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    /**
     * Estado que representa la publicación completa obtenida desde el repositorio.
     *
     * - UiState.Loading: mientras se realiza la petición de carga.
     * - UiState.Success: cuando la publicación se carga correctamente (data: objeto con todos los datos).
     * - UiState.Error: cuando ocurre un error al cargar la publicación (mensaje en data.msg).
     */
    private val _publicationState = MutableStateFlow<UiState>(UiState.Loading)
    val publicationState: StateFlow<UiState> = _publicationState.asStateFlow()

    /**
     * Estado booleano que indica si la publicación está marcada como "liked" localmente.
     */
    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    /**
     * Estado booleano que indica si la publicación está marcada como "saved" (guardada) localmente.
     */
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    /**
     * Contador local de "likes" para mostrar en la interfaz sin recargar toda la publicación.
     */
    private val _likesCount = MutableStateFlow(0)
    val likesCount: StateFlow<Int> = _likesCount.asStateFlow()

    /**
     * Estado que representa la lista de comentarios asociados a la publicación.
     *
     * - UiState.Loading: mientras se realiza la petición de carga de comentarios.
     * - UiState.Success: cuando se cargan correctamente (data: lista de comentarios).
     * - UiState.Error: cuando ocurre un error al cargar comentarios (mensaje en data.msg).
     */
    private val _commentsState = MutableStateFlow<UiState>(UiState.Loading)
    val commentsState: StateFlow<UiState> = _commentsState.asStateFlow()

    /**
     * Estado que representa la operación de añadir un comentario.
     *
     * - UiState.Idle: estado inicial, sin operación en curso.
     * - UiState.Loading: mientras se realiza la petición para añadir un comentario.
     * - UiState.Success: cuando el comentario se añade correctamente (data: ID del comentario nuevo).
     * - UiState.Error: cuando ocurre un error al añadir el comentario (mensaje en data.msg).
     */
    private val _addCommentState = MutableStateFlow<UiState>(UiState.Idle)
    val addCommentState: StateFlow<UiState> = _addCommentState.asStateFlow()

    /**
     * Carga la publicación completa desde el repositorio y actualiza los estados internos.
     *
     * - Actualiza [_publicationState] a UiState.Loading antes de la petición.
     * - Si la respuesta contiene datos, emite UiState.Success con el objeto de publicación.
     *   Además, inicializa [_isLiked], [_isSaved] y [_likesCount] a partir de la respuesta.
     * - Si ocurre un error o no hay datos, emite UiState.Error con el mensaje correspondiente.
     *
     * @param id Identificador de la publicación a cargar.
     */
    fun loadPublication(id: Int) {
        viewModelScope.launch {
            _publicationState.value = UiState.Loading
            try {
                val result = publicationRepository.getPublicationById(id)
                if (result.data != null) {
                    val pub = result.data
                    _publicationState.value = UiState.Success(pub)
                    _isLiked.value = pub.liked
                    _isSaved.value = pub.saved
                    _likesCount.value = pub.likes
                } else {
                    _publicationState.value =
                        UiState.Error(result.msg ?: "Error cargando publicación")
                }
            } catch (e: Exception) {
                _publicationState.value =
                    UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Carga la lista de comentarios asociados a una publicación.
     *
     * - Actualiza [_commentsState] a UiState.Loading antes de la petición.
     * - Si la respuesta contiene datos, emite UiState.Success con la lista de comentarios.
     * - Si ocurre un error o no hay datos, emite UiState.Error con el mensaje correspondiente.
     *
     * @param publicationId Identificador de la publicación cuyos comentarios se desean cargar.
     */
    fun loadComments(publicationId: Int) {
        viewModelScope.launch {
            _commentsState.value = UiState.Loading
            try {
                val result = commentRepository.getCommentsByPublication(publicationId)
                if (result.data != null) {
                    _commentsState.value = UiState.Success(result.data)
                } else {
                    _commentsState.value =
                        UiState.Error(result.msg ?: "Error cargando comentarios")
                }
            } catch (e: Exception) {
                _commentsState.value =
                    UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Añade un nuevo comentario a la publicación.
     *
     * - Actualiza [_addCommentState] a UiState.Loading antes de la petición.
     * - Si la respuesta contiene un ID de comentario, emite UiState.Success con dicho ID.
     * - Si ocurre un error o no hay ID, emite UiState.Error con el mensaje correspondiente.
     *
     * @param publicationId Identificador de la publicación a la que se añade el comentario.
     * @param contenido Texto del comentario a añadir.
     */
    fun addComment(publicationId: Int, contenido: String) {
        viewModelScope.launch {
            _addCommentState.value = UiState.Loading
            try {
                val result = commentRepository.createComment(publicationId, contenido)
                if (result.data != null) {
                    _addCommentState.value = UiState.Success(result.data)
                } else {
                    _addCommentState.value =
                        UiState.Error(result.msg ?: "Error al agregar comentario")
                }
            } catch (e: Exception) {
                _addCommentState.value =
                    UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Alterna el estado de "like" de manera optimista y sincroniza el cambio con el servidor.
     *
     * - Invierte localmente [_isLiked] y ajusta [_likesCount] en consecuencia.
     * - Llama a [PublicationRepository.toggleLike] para persistir el cambio en el servidor.
     * - Si la llamada falla o retorna código distinto a 200..299, revierte los cambios locales.
     *
     * @param publicationId Identificador de la publicación cuyo estado de "like" se alterna.
     */
    fun toggleLike(publicationId: Int) {
        viewModelScope.launch {
            val nextLiked = !_isLiked.value
            _isLiked.value = nextLiked
            _likesCount.value = _likesCount.value + if (nextLiked) +1 else -1

            try {
                val result = publicationRepository.toggleLike(publicationId)
                if (result.code !in 200..299) {
                    _isLiked.value = !nextLiked
                    _likesCount.value = _likesCount.value + if (nextLiked) -1 else +1
                }
            } catch (_: Exception) {
                _isLiked.value = !nextLiked
                _likesCount.value = _likesCount.value + if (nextLiked) -1 else +1
            }
        }
    }

    /**
     * Alterna el estado de "saved" (guardado) de manera optimista y sincroniza con el servidor.
     *
     * - Invierte localmente [_isSaved].
     * - Llama a [PublicationRepository.toggleBookmark] para persistir el cambio.
     * - Si la llamada falla o retorna código distinto a 200..299, revierte el cambio local.
     *
     * @param publicationId Identificador de la publicación cuyo estado de "guardado" se alterna.
     */
    fun toggleBookmark(publicationId: Int) {
        viewModelScope.launch {
            val nextSaved = !_isSaved.value
            _isSaved.value = nextSaved

            try {
                val result = publicationRepository.toggleBookmark(publicationId)
                if (result.code !in 200..299) {
                    _isSaved.value = !nextSaved
                }
            } catch (_: Exception) {
                _isSaved.value = !nextSaved
            }
        }
    }

    /**
     * Elimina un comentario especificado y recarga la lista de comentarios tras la eliminación.
     *
     * - Llama a [CommentRepository.deleteComment] para eliminar el comentario.
     * - Si la respuesta retorna código 200, invoca [loadComments] para refrescar la lista.
     * - Si falla, emite UiState.Error en [_commentsState] con el mensaje correspondiente.
     *
     * @param commentId Identificador del comentario a eliminar.
     * @param publicationId Identificador de la publicación a la que pertenece el comentario, utilizado para recargar.
     */
    fun deleteComment(commentId: Int, publicationId: Int) {
        viewModelScope.launch {
            try {
                val result = commentRepository.deleteComment(commentId)
                if (result.code == 200) {
                    loadComments(publicationId)
                } else {
                    _commentsState.value =
                        UiState.Error(result.msg ?: "Error eliminando comentario")
                }
            } catch (e: Exception) {
                _commentsState.value =
                    UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}
