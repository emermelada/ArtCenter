package com.emermelada.artcenter.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.data.repositories.PublicationRepository
import com.emermelada.artcenter.data.repositories.UserRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica para la pantalla de perfil de usuario.
 *
 * - Carga la información del usuario.
 * - Permite actualizar nombre de usuario y foto de perfil.
 * - Carga las publicaciones del usuario y las guardadas.
 * - Administra la eliminación, "like" y "save" de publicaciones en las listas correspondientes.
 *
 * @property userRepository Repositorio para obtener y actualizar datos del usuario.
 * @property publicationRepository Repositorio para obtener y modificar publicaciones.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    /**
     * Estado que representa la información del usuario.
     *
     * - UiState.Loading: mientras se solicita la información.
     * - UiState.Success: cuando se carga correctamente (data: objeto usuario).
     * - UiState.Error: si ocurre un error (mensaje en data.msg).
     */
    private val _userInfoState = MutableStateFlow<UiState>(UiState.Loading)
    val userInfoState: StateFlow<UiState> get() = _userInfoState

    /**
     * Estado que indica el resultado de la última operación de actualización (nombre o foto).
     *
     * - UiState.Loading: mientras se procesa la solicitud.
     * - UiState.Success: cuando la actualización es exitosa (data: mensaje).
     * - UiState.Error: si ocurre un error al actualizar (mensaje en data.msg).
     */
    private val _updateState = MutableStateFlow<UiState>(UiState.Loading)
    val updateState: StateFlow<UiState> get() = _updateState

    /**
     * Lista de publicaciones propias del usuario.
     *
     * Se va poblando de forma incremental conforme se cargan más páginas.
     */
    private val _myPublications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val myPublications: StateFlow<List<PublicationSimple>> = _myPublications

    /**
     * Lista de publicaciones guardadas por el usuario.
     *
     * Se va poblando de forma incremental conforme se cargan más páginas.
     */
    private val _savedPublications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val savedPublications: StateFlow<List<PublicationSimple>> = _savedPublications

    /**
     * Indicador de carga para las listas de publicaciones (propias o guardadas).
     *
     * - `true` mientras se solicitan más publicaciones.
     * - `false` cuando la solicitud finaliza.
     */
    private val _isLoadingPublications = MutableStateFlow(false)
    val isLoadingPublications: StateFlow<Boolean> = _isLoadingPublications

    /**
     * Estado que indica el resultado de la última operación de eliminación de publicación.
     *
     * - UiState.Loading: mientras se procesa la eliminación.
     * - UiState.Success: cuando la eliminación es exitosa (data: mensaje).
     * - UiState.Error: si ocurre un error al eliminar (mensaje en data.msg).
     */
    private val _deletePublicationState = MutableStateFlow<UiState>(UiState.Loading)
    val deletePublicationState: StateFlow<UiState> get() = _deletePublicationState

    // Variables internas que almacenan la página actual para paginación incremental.
    private var myPage = 0
    private var savedPage = 0

    /**
     * Carga la siguiente página de "mis publicaciones".
     *
     * - Actualiza [_isLoadingPublications] a `true`.
     * - Llama a [PublicationRepository.getMyPublications] con el número de página actual.
     * - Si la respuesta contiene datos, los agrega al final de [_myPublications] y aumenta `myPage`.
     * - Finalmente, actualiza [_isLoadingPublications] a `false`.
     */
    fun loadMyPublications() {
        viewModelScope.launch {
            _isLoadingPublications.value = true
            val result = publicationRepository.getMyPublications(myPage)
            result.data?.let {
                _myPublications.value = _myPublications.value + it
                myPage++
            }
            _isLoadingPublications.value = false
        }
    }

    /**
     * Carga la siguiente página de "publicaciones guardadas".
     *
     * - Actualiza [_isLoadingPublications] a `true`.
     * - Llama a [PublicationRepository.getSavedPublications] con el número de página actual.
     * - Si la respuesta contiene datos, los agrega al final de [_savedPublications] y aumenta `savedPage`.
     * - Finalmente, actualiza [_isLoadingPublications] a `false`.
     */
    fun loadSavedPublications() {
        viewModelScope.launch {
            _isLoadingPublications.value = true
            val result = publicationRepository.getSavedPublications(savedPage)
            result.data?.let {
                _savedPublications.value = _savedPublications.value + it
                savedPage++
            }
            _isLoadingPublications.value = false
        }
    }

    /**
     * Obtiene la información del usuario autenticado.
     *
     * - Actualiza [_userInfoState] a UiState.Loading antes de la llamada.
     * - Si la respuesta contiene datos, emite UiState.Success con el objeto usuario.
     * - Si no, emite UiState.Error con el mensaje correspondiente.
     */
    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfoState.value = UiState.Loading
            val result = userRepository.getUserInfo()
            if (result.data != null) {
                _userInfoState.value = UiState.Success(result.data)
            } else {
                _userInfoState.value =
                    UiState.Error(result.msg ?: "Error al cargar la información del usuario")
            }
        }
    }

    /**
     * Actualiza solo el nombre de usuario.
     *
     * - Actualiza [_updateState] a UiState.Loading antes de la llamada.
     * - Si la respuesta retorna código 200..299, emite UiState.Success con mensaje de éxito.
     * - Si no, emite UiState.Error con el mensaje proporcionado o un mensaje por defecto.
     *
     * @param username Nuevo nombre de usuario a establecer.
     */
    fun updateUsername(username: String) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            val result = userRepository.updateUsername(username)
            if (result.code in 200..299) {
                _updateState.value = UiState.Success("Nombre de usuario actualizado correctamente")
            } else {
                _updateState.value =
                    UiState.Error(result.msg ?: "Error al actualizar el nombre")
            }
        }
    }

    /**
     * Actualiza la foto de perfil del usuario.
     *
     * - Llama a [UserRepository.updateProfilePicture] para subir el archivo.
     * - Si la respuesta contiene la URL nueva, recarga la información del usuario con [fetchUserInfo]
     *   y emite UiState.Success con mensaje de éxito.
     * - Si no, emite UiState.Error con el mensaje proporcionado o un mensaje por defecto.
     *
     * @param file Parte multipart que contiene la imagen a subir.
     */
    fun updateProfilePicture(file: MultipartBody.Part) {
        viewModelScope.launch {
            val result = userRepository.updateProfilePicture(file)
            if (result.data != null) {
                fetchUserInfo()
                _updateState.value = UiState.Success("Foto de perfil actualizada correctamente")
            } else {
                _updateState.value =
                    UiState.Error(result.msg ?: "Error al actualizar la foto de perfil")
            }
        }
    }

    /**
     * Alterna el estado de "guardado" (bookmark) para una publicación tanto en la lista de mis publicaciones
     * como en la lista de guardados.
     *
     * - Llama a [PublicationRepository.toggleBookmark] con el ID de la publicación.
     * - Si el código de respuesta está en 200..299, actualiza localmente el campo `saved` de la publicación.
     *   - En [_myPublications], invierte el valor de `saved`.
     *   - En [_savedPublications], si antes no estaba guardada, la añade al inicio; si antes ya estaba guardada,
     *     la elimina de la lista.
     *
     * @param pub Objeto [PublicationSimple] que contiene el identificador y el estado actual de `saved`.
     */
    fun toggleSave(pub: PublicationSimple) {
        viewModelScope.launch {
            val result = publicationRepository.toggleBookmark(pub.id)
            if (result.code in 200..299) {
                _myPublications.value = _myPublications.value.map {
                    if (it.id == pub.id) it.copy(saved = !it.saved) else it
                }
                if (!pub.saved) {
                    _savedPublications.value = listOf(pub.copy(saved = true)) + _savedPublications.value
                } else {
                    _savedPublications.value = _savedPublications.value.filter { it.id != pub.id }
                }
            }
        }
    }

    /**
     * Alterna el estado de "like" para una publicación tanto en la lista de mis publicaciones
     * como en la lista de guardados.
     *
     * - Llama a [PublicationRepository.toggleLike] con el ID de la publicación.
     * - Si el código de respuesta está en 200..299, invierte el campo `liked` en ambas listas.
     *
     * @param pub Objeto [PublicationSimple] que contiene el identificador y el estado actual de `liked`.
     */
    fun toggleLike(pub: PublicationSimple) {
        viewModelScope.launch {
            val result = publicationRepository.toggleLike(pub.id)
            if (result.code in 200..299) {
                _myPublications.value = _myPublications.value.map {
                    if (it.id == pub.id) it.copy(liked = !it.liked) else it
                }
                _savedPublications.value = _savedPublications.value.map {
                    if (it.id == pub.id) it.copy(liked = !it.liked) else it
                }
            }
        }
    }

    /**
     * Restablece el estado de actualización (_updateState) a "cargando" para reutilizar la UI.
     */
    fun clearUpdateState() {
        _updateState.value = UiState.Loading
    }

    /**
     * Elimina una publicación del perfil del usuario.
     *
     * - Actualiza [_deletePublicationState] a UiState.Loading antes de la llamada.
     * - Llama a [PublicationRepository.deletePublication] con el ID de la publicación.
     * - Si el código de respuesta está en 200..299, elimina esa publicación de [_myPublications]
     *   y emite UiState.Success con mensaje de éxito.
     * - Si no, emite UiState.Error con el mensaje proporcionado o un mensaje por defecto.
     *
     * @param publicationId Identificador de la publicación a eliminar.
     */
    fun deletePublication(publicationId: Int) {
        viewModelScope.launch {
            _deletePublicationState.value = UiState.Loading
            val result = publicationRepository.deletePublication(publicationId)
            if (result.code in 200..299) {
                _myPublications.value = _myPublications.value.filter { it.id != publicationId }
                _deletePublicationState.value = UiState.Success("Publicación eliminada correctamente")
            } else {
                _deletePublicationState.value =
                    UiState.Error(result.msg ?: "Error al eliminar la publicación")
            }
        }
    }

    /**
     * Restablece el estado de eliminación (_deletePublicationState) a "cargando" para reutilizar la UI.
     */
    fun clearDeleteState() {
        _deletePublicationState.value = UiState.Loading
    }
}
