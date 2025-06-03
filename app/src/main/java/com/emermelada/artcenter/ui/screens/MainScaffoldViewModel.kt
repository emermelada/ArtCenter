package com.emermelada.artcenter.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermelada.artcenter.data.repositories.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainScaffoldViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
): ViewModel() {
    /** Estado observable que representa el estado de la UI para las salas. */
    val userRol: StateFlow<String> = preferencesRepository.userRoleFlow
        .map { it ?: "" } // ← aseguramos que no sea null
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    /** Estado observable que representa el id de usuario */
    val userId: StateFlow<String> = preferencesRepository.userIdFlow
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
}