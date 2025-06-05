package com.emermelada.artcenter.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extensión de [Context] para obtener la instancia de [DataStore] que almacena las preferencias del usuario.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repositorio responsable de almacenar y recuperar datos de usuario en el [DataStore] de preferencias.
 *
 * @property dataStore Instancia de [DataStore] donde se guardan las preferencias (token, id y rol de usuario).
 */
@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val USER_BEARER_TOKEN = stringPreferencesKey("user_bearer_token")
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_ROLE = stringPreferencesKey("user_role")

    /**
     * Flujo que emite el token de autenticación almacenado. Puede ser nulo si no existe.
     */
    val userTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_BEARER_TOKEN]
    }

    /**
     * Flujo que emite el identificador de usuario almacenado. Puede ser nulo si no existe.
     */
    val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    /**
     * Flujo que emite el rol de usuario almacenado. Puede ser nulo si no existe.
     */
    val userRoleFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ROLE]
    }

    /**
     * Guarda el token de autenticación del usuario.
     *
     * @param token Cadena que contiene el nuevo token a almacenar.
     */
    suspend fun saveUserBearerToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_BEARER_TOKEN] = token
        }
    }

    /**
     * Guarda el identificador de usuario.
     *
     * @param userId Cadena que contiene el identificador a almacenar.
     */
    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    /**
     * Guarda el rol de usuario.
     *
     * @param role Cadena que contiene el rol a almacenar.
     */
    suspend fun saveUserRole(role: String) {
        dataStore.edit { preferences ->
            preferences[USER_ROLE] = role
        }
    }

    /**
     * Elimina todos los datos de usuario almacenados (token, id y rol).
     */
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_BEARER_TOKEN)
            preferences.remove(USER_ID)
            preferences.remove(USER_ROLE)
        }
    }
}
