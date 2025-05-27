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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
): ViewModel() {

    private val _userInfoState = MutableStateFlow<UiState>(UiState.Loading)
    val userInfoState: StateFlow<UiState> get() = _userInfoState

    private val _updateState = MutableStateFlow<UiState>(UiState.Loading)
    val updateState: StateFlow<UiState> get() = _updateState

    // — Estados para mis publicaciones y publicaciones guardadas —
    private val _myPublications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val myPublications: StateFlow<List<PublicationSimple>> = _myPublications

    private val _savedPublications = MutableStateFlow<List<PublicationSimple>>(emptyList())
    val savedPublications: StateFlow<List<PublicationSimple>> = _savedPublications

    private val _isLoadingPublications = MutableStateFlow(false)
    val isLoadingPublications: StateFlow<Boolean> = _isLoadingPublications

    private var myPage = 0
    private var savedPage = 0

    /** Carga “tus publicaciones” con paginación incremental */
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

    /** Carga “publicaciones guardadas” con paginación incremental */
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

    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfoState.value = UiState.Loading
            val result = userRepository.getUserInfo()
            if (result.data != null) {
                _userInfoState.value = UiState.Success(result.data)
            } else {
                _userInfoState.value = UiState.Error(result.msg ?: "Error al cargar la información del usuario")
            }
        }
    }

    // Actualizar solo el nombre de usuario
    fun updateUsername(username: String) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            val result = userRepository.updateUsername(username)
            if (result.code in 200..299) {
                // Forzamos mensaje de éxito aunque data sea nulo
                _updateState.value = UiState.Success("Nombre de usuario actualizado correctamente")
            } else {
                _updateState.value = UiState.Error(result.msg ?: "Error al actualizar el nombre")
            }
        }
    }

    fun updateProfilePicture(file: MultipartBody.Part) {
        viewModelScope.launch {
            // Iniciar la actualización del perfil
            val result = userRepository.updateProfilePicture(file)

            if (result.data != null) {
                // Recargar la información del usuario con la nueva foto de perfil
                fetchUserInfo()  // Esto asegura que se actualice la URL de la foto

                // Establecer el mensaje de éxito en el estado de actualización
                _updateState.value = UiState.Success("Foto de perfil actualizada correctamente")
            } else {
                // En caso de error, actualiza el estado de error en la UI
                _updateState.value = UiState.Error(result.msg ?: "Error al actualizar la foto de perfil")
            }
        }
    }

    /** Alterna guardado en ambas vistas */
    fun toggleSave(pub: PublicationSimple) {
        viewModelScope.launch {
            val result = publicationRepository.toggleBookmark(pub.id)
            if (result.code in 200..299) {
                // myPublications
                _myPublications.value = _myPublications.value.map {
                    if (it.id == pub.id) it.copy(saved = !it.saved) else it
                }
                // savedPublications: si acabas de guardar lo añades al principio
                if (!pub.saved) {
                    _savedPublications.value = listOf(pub.copy(saved = true)) + _savedPublications.value
                } else {
                    // si acabas de desguardar, lo quitas
                    _savedPublications.value = _savedPublications.value.filter { it.id != pub.id }
                }
            }
        }
    }

    /** Alterna like en ambas vistas */
    fun toggleLike(pub: PublicationSimple) {
        viewModelScope.launch {
            val result = publicationRepository.toggleLike(pub.id)
            if (result.code in 200..299) {
                // actualiza liked
                _myPublications.value = _myPublications.value.map {
                    if (it.id == pub.id) it.copy(liked = !it.liked) else it
                }
                _savedPublications.value = _savedPublications.value.map {
                    if (it.id == pub.id) it.copy(liked = !it.liked) else it
                }
            }
        }
    }

    fun clearUpdateState() {
        _updateState.value = UiState.Loading
    }
}
