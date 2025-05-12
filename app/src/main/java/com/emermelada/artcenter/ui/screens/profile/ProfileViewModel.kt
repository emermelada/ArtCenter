package com.emermelada.artcenter.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.UserRepository
import com.emermelada.artcenter.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
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

    // Actualizar solo la foto de perfil
    fun updateProfilePicture(urlFotoPerfil: String) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            val result = userRepository.updateProfilePicture(urlFotoPerfil)
            if (result.data != null) {
                _updateState.value = UiState.Success(result.msg)
            } else {
                _updateState.value = UiState.Error(result.msg ?: "Error al actualizar la foto de perfil")
            }
        }
    }
}
