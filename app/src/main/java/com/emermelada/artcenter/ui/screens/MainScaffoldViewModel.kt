package com.emermelada.artcenter.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel para el Scaffold principal de la aplicación.
 *
 * Proporciona estados observables con el rol y el ID del usuario, obtenidos desde DataStore
 * a través de [PreferencesRepository].
 *
 * @property preferencesRepository Repositorio para acceder a las preferencias de usuario.
 */
@HiltViewModel
class MainScaffoldViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /**
     * Flujo observable que emite el rol del usuario.
     *
     * Convierte el flujo original en un [StateFlow] que no emite valores nulos, reemplazándolos
     * por cadena vacía. Comparte la suscripción mientras haya al menos un suscriptor.
     */
    val userRol: StateFlow<String> = preferencesRepository.userRoleFlow
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    /**
     * Flujo observable que emite el ID de usuario.
     *
     * Convierte el flujo original en un [StateFlow] que no emite valores nulos, reemplazándolos
     * por cadena vacía. Comparte la suscripción mientras haya al menos un suscriptor.
     */
    val userId: StateFlow<String> = preferencesRepository.userIdFlow
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
}
