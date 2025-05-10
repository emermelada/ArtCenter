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

// Extensión para obtener el DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val USER_BEARER_TOKEN = stringPreferencesKey("user_bearer_token")
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_ROLE = stringPreferencesKey("user_role")

    // Flujos para obtener los valores almacenados
    val userTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_BEARER_TOKEN]
    }

    val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userRoleFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ROLE]
    }

    // Funciones para guardar los valores
    suspend fun saveUserBearerToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_BEARER_TOKEN] = token
        }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    suspend fun saveUserRole(role: String) {
        dataStore.edit { preferences ->
            preferences[USER_ROLE] = role
        }
    }

    // Función para cerrar sesión (borrar datos del usuario)
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_BEARER_TOKEN)
            preferences.remove(USER_ID)
            preferences.remove(USER_ROLE)
        }
    }
}