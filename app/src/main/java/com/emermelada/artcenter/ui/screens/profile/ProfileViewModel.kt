package com.emermelada.artcenter.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            if (result.data != null) {
                _updateState.value = UiState.Success(result.msg)
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
}
