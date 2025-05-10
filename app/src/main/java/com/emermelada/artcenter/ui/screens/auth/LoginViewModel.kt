package com.emermelada.artcenter.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.model.auth.LoginRequest
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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
): ViewModel(){
    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> get() = _loginResult.asStateFlow()

    private val _uiStateLogin = MutableStateFlow<UiState>(UiState.Loading)
    val uiStatelogin: StateFlow<UiState> get() = _uiStateLogin

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de red: ${throwable.message}"
            is HttpException -> "Error del servidor: ${throwable.message}"
            else -> "Error inesperado: ${throwable.localizedMessage}"
        }
        _uiStateLogin.value = UiState.Error(errorMessage)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiStateLogin.value = UiState.Loading
            val loginRequest = LoginRequest(email, password)
            val response = authRepository.login(loginRequest)

            if (response.data != null) {
                // Guardar en DataStore
                preferencesRepository.saveUserId(response.data.id.toString())
                preferencesRepository.saveUserRole(response.data.rol)
                preferencesRepository.saveUserBearerToken(response.data.token)

                // Sincronizar con SessionManager justo después de guardar
                SessionManager.syncWithDataStore(preferencesRepository)

                // Guardar en SessionManager también por si acaso
                SessionManager.saveSession(response.data.token, response.data.id, response.data.rol)

                _loginResult.value = true
                _uiStateLogin.value = UiState.Success(response.data)
            } else {
                _loginResult.value = false
                if (response.code == 401) {
                    _uiStateLogin.value =
                        UiState.Error(response.msg ?: "Las credenciales son incorrectas.")
                } else {
                    _uiStateLogin.value =
                        UiState.Error(response.msg ?: "Error desconocido: ${response.code}")
                }
            }
        }
    }
}