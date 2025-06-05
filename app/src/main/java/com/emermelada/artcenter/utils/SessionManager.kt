package com.emermelada.artcenter.utils

import com.emermelada.artcenter.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

/**
 * Objeto singleton que gestiona la sesión del usuario en memoria.
 *
 * Contiene las propiedades de sesión (token, id y rol) y métodos para guardar,
 * limpiar y sincronizar esos valores con el DataStore.
 */
object SessionManager {

    /**
     * Identificador del usuario autenticado. Se asigna en [saveSession] o en [syncWithDataStore].
     * Puede ser nulo si no hay sesión activa.
     */
    var id: Int? = null

    /**
     * Rol del usuario autenticado. Se asigna en [saveSession] o en [syncWithDataStore].
     * Puede ser nulo si no hay sesión activa.
     */
    var rol: String? = null

    /**
     * Token de autenticación (Bearer) del usuario. Se asigna en [saveSession] o en [syncWithDataStore].
     * Puede ser nulo si no hay sesión activa.
     */
    var bearerToken: String? = null

    /**
     * Guarda los valores de sesión en memoria.
     *
     * @param token Token de autenticación obtenido del servidor.
     * @param userId Identificador único del usuario.
     * @param userRole Rol asignado al usuario en el sistema.
     */
    fun saveSession(token: String, userId: Int, userRole: String) {
        bearerToken = token
        id = userId
        rol = userRole
    }

    /**
     * Limpia los valores de sesión en memoria, eliminando token, id y rol.
     */
    fun clearSession() {
        bearerToken = null
        id = null
        rol = null
    }

    /**
     * Sincroniza los valores de sesión en memoria con los almacenados en el DataStore.
     *
     * Lee los flujos de [PreferencesRepository] y asigna los valores obtenidos a las propiedades
     * `bearerToken`, `id` y `rol`. Debe llamarse al iniciar la aplicación para restaurar sesión.
     *
     * @param preferencesRepository Instancia de [PreferencesRepository] que provee los flujos de datos persistidos.
     */
    fun syncWithDataStore(preferencesRepository: PreferencesRepository) {
        runBlocking {
            bearerToken = preferencesRepository.userTokenFlow.firstOrNull()
            id = preferencesRepository.userIdFlow.firstOrNull()?.toIntOrNull()
            rol = preferencesRepository.userRoleFlow.firstOrNull()
        }
    }
}
