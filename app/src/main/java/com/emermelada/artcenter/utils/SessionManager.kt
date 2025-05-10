package com.emermelada.artcenter.utils

import com.emermelada.artcenter.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object SessionManager {
    var id: Int? = null
    var rol: String? = null
    var bearerToken: String? = null

    fun saveSession(token: String, userId: Int, userRole: String) {
        bearerToken = token
        id = userId
        rol = userRole
    }

    fun clearSession() {
        bearerToken = null
        id = null
        rol = null
    }

    // Nueva función para sincronizar datos con DataStore al iniciar la app
    fun syncWithDataStore(preferencesRepository: PreferencesRepository) {
        runBlocking {
            bearerToken = preferencesRepository.userTokenFlow.firstOrNull()
            id = preferencesRepository.userIdFlow.firstOrNull()?.toIntOrNull()
            rol = preferencesRepository.userRoleFlow.firstOrNull()
        }
    }
}