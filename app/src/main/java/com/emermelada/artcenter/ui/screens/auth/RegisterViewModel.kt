package com.emermelada.artcenter.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.auth.LoginRequest
import com.emermelada.artcenter.data.model.auth.RegisterRequest
import com.emermelada.artcenter.data.repositories.AuthRepository
import com.emermelada.artcenter.data.repositories.PreferencesRepository
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
): ViewModel(){
    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> get() = _registerResult.asStateFlow()

    private val _uiStateRegister = MutableStateFlow<UiState>(UiState.Loading)
    val uiStateRegister: StateFlow<UiState> get() = _uiStateRegister

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de red: ${throwable.message}"
            is HttpException -> "Error del servidor: ${throwable.message}"
            else -> "Error inesperado: ${throwable.localizedMessage}"
        }
        _uiStateRegister.value = UiState.Error(errorMessage)
    }

    fun register(email: String, password: String, username: String){
        viewModelScope.launch(exceptionHandler) {
            _uiStateRegister.value = UiState.Loading
            val registerRequest = RegisterRequest(email, password, username)
            val response = authRepository.register(registerRequest)
            if (response.data != null) {
                login(email, password)
            } else {
                when (response.code) {
                    409 -> _uiStateRegister.value = UiState.Error("Este correo ya está registrado.")
                    else -> _uiStateRegister.value = UiState.Error(response.msg ?: "Error desconocido: ${response.code}")
                }
            }
        }
    }

    fun login(email: String, password: String) {
        // Lanzar una corrutina en el viewModel
        viewModelScope.launch(exceptionHandler) {
            // Operación asíncrona
            val loginRequest = LoginRequest(email, password)
            val response = authRepository.login(loginRequest)
            if (response.data != null) {
                SessionManager.saveSession(response.data.token, response.data.id, response.data.rol)

                //guardar en DataStorage
                viewModelScope.launch {
                    preferencesRepository.saveUserId(response.data.id.toString())
                    preferencesRepository.saveUserRole(response.data.rol)
                    preferencesRepository.saveUserBearerToken(response.data.token)
                }

                _registerResult.value = true
                _uiStateRegister.value = UiState.Success(response.data)
            } else {
                if (response.code == 400) {
                    _uiStateRegister.value =
                        UiState.Error(response.msg ?: "Las credenciales son incorrectas.")
                } else {
                    _uiStateRegister.value =
                        UiState.Error(response.msg ?: "Error desconocido: ${response.code}")
                }
            }
        }
    }
}