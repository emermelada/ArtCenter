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

/**
 * ViewModel encargado de manejar el proceso de inicio de sesión.
 *
 * @property authRepository Repositorio para realizar la llamada de autenticación.
 * @property preferencesRepository Repositorio para guardar los datos de sesión en DataStore.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /**
     * Flujo que emite el resultado de la operación de inicio de sesión:
     * - `true` si el inicio fue exitoso
     * - `false` si falló
     * - `null` antes de realizar cualquier intento
     */
    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> get() = _loginResult.asStateFlow()

    /**
     * Flujo que emite el estado de la interfaz relacionado con el proceso de inicio:
     * - [UiState.Loading] mientras se realiza la petición
     * - [UiState.Success] si la respuesta contiene datos
     * - [UiState.Error] si ocurre algún error o las credenciales son inválidas
     */
    private val _uiStateLogin = MutableStateFlow<UiState>(UiState.Loading)
    val uiStatelogin: StateFlow<UiState> get() = _uiStateLogin

    /**
     * Manejador de excepciones para el coroutine que procesa el inicio de sesión.
     * Captura distintos tipos de errores y actualiza [_uiStateLogin] con un mensaje descriptivo.
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de red: ${throwable.message}"
            is HttpException -> "Error del servidor: ${throwable.message}"
            else -> "Error inesperado: ${throwable.localizedMessage}"
        }
        _uiStateLogin.value = UiState.Error(errorMessage)
    }

    /**
     * Inicia el proceso de autenticación con las credenciales proporcionadas.
     *
     * Al ejecutarse:
     * 1. Actualiza [_uiStateLogin] a [UiState.Loading].
     * 2. Construye un [LoginRequest] con el correo y la contraseña.
     * 3. Llama a [AuthRepository.login] para obtener la respuesta.
     * 4. Si la respuesta contiene datos:
     *    - Guarda el ID, el rol y el token en [PreferencesRepository].
     *    - Sincroniza esos valores con [SessionManager].
     *    - Actualiza [_loginResult] a `true` y [_uiStateLogin] a [UiState.Success].
     * 5. Si la respuesta no contiene datos:
     *    - Actualiza [_loginResult] a `false`.
     *    - Si el código es 401, emite un mensaje de credenciales incorrectas.
     *    - En otro caso, emite un mensaje de error desconocido con el código.
     *
     * @param email Correo electrónico ingresado por el usuario.
     * @param password Contraseña ingresada por el usuario.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiStateLogin.value = UiState.Loading
            val loginRequest = LoginRequest(email, password)
            val response = authRepository.login(loginRequest)

            if (response.data != null) {
                preferencesRepository.saveUserId(response.data.id.toString())
                preferencesRepository.saveUserRole(response.data.rol)
                preferencesRepository.saveUserBearerToken(response.data.token)
                SessionManager.syncWithDataStore(preferencesRepository)
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
