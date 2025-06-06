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

/**
 * ViewModel responsable de manejar el flujo de registro y, tras un registro exitoso, iniciar sesión automáticamente.
 *
 * @property authRepository Repositorio que se encarga de las llamadas a la API para autenticación y registro.
 * @property preferencesRepository Repositorio para guardar los datos de sesión en DataStore.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /**
     * Flujo que emite el resultado de la operación de registro:
     * - `true` si el registro y el inicio de sesión posterior fueron exitosos,
     * - `false` si el registro falló (no cambia a true cuando el registro falla).
     * - `null` antes de realizar cualquier intento.
     */
    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> get() = _registerResult.asStateFlow()

    /**
     * Flujo que emite el estado de la interfaz relacionado con el proceso de registro:
     * - [UiState.Loading] mientras se realiza la petición de registro o login,
     * - [UiState.Success] si el registro (y login) fueron exitosos,
     * - [UiState.Error] si ocurre algún error durante el registro o login.
     */
    private val _uiStateRegister = MutableStateFlow<UiState>(UiState.Loading)
    val uiStateRegister: StateFlow<UiState> get() = _uiStateRegister

    /**
     * Manejador de excepciones para las corrutinas que realizan el registro o login.
     * Captura distintos tipos de errores y actualiza [_uiStateRegister] con un mensaje descriptivo.
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de red: ${throwable.message}"
            is HttpException -> "Error del servidor: ${throwable.message}"
            else -> "Error inesperado: ${throwable.localizedMessage}"
        }
        _uiStateRegister.value = UiState.Error(errorMessage)
    }

    /**
     * Inicia el proceso de registro con las credenciales proporcionadas.
     *
     * - Muestra [UiState.Loading] mientras se hace la petición de registro.
     * - Construye un [RegisterRequest] con correo, contraseña y nombre de usuario.
     * - Llama a [AuthRepository.register], y si la respuesta contiene datos, invoca [login] automáticamente.
     * - Si la respuesta no contiene datos, emite [UiState.Error] con el mensaje de error adecuado:
     *   - Código 409: "Este correo ya está registrado."
     *   - Otro código: muestra "Error desconocido: <código>".
     *
     * @param email Correo electrónico ingresado por el usuario.
     * @param password Contraseña ingresada por el usuario.
     * @param username Nombre de usuario elegido.
     */
    fun register(email: String, password: String, username: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiStateRegister.value = UiState.Loading
            val registerRequest = RegisterRequest(email, password, username)
            val response = authRepository.register(registerRequest)
            if (response.data != null) {
                login(email, password)
            } else {
                when (response.code) {
                    409 -> _uiStateRegister.value = UiState.Error("Este correo ya está registrado.")
                    else -> _uiStateRegister.value =
                        UiState.Error(response.msg ?: "Error desconocido: ${response.code}")
                }
            }
        }
    }

    /**
     * Inicia sesión inmediatamente después de un registro exitoso.
     *
     * - Realiza una llamada a [AuthRepository.login] con el mismo correo y contraseña.
     * - Si el login es exitoso, guarda sesión en [SessionManager] y en DataStore vía [preferencesRepository].
     * - Actualiza [_registerResult] a `true` y [_uiStateRegister] a [UiState.Success].
     * - Si el login falla con código 400, emite [UiState.Error] con "Las credenciales son incorrectas."
     * - Para cualquier otro código, emite [UiState.Error] con "Error desconocido: <código>".
     *
     * @param email Correo electrónico registrado.
     * @param password Contraseña utilizada en el registro.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            val loginRequest = LoginRequest(email, password)
            val response = authRepository.login(loginRequest)
            if (response.data != null) {
                // Guardar en SessionManager
                SessionManager.saveSession(response.data.token, response.data.id, response.data.rol)

                // Guardar en DataStore de forma asíncrona
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
