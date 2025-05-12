package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.data.model.profile.UserUpdateRequest
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

@Singleton
class UserRepository {
    private val api = RetroFitInstance.api

    // Obtener la información del usuario
    suspend fun getUserInfo(): Result<User> {
        return withContext(Dispatchers.IO) {
            val response = api.getUserInfo().execute()
            if (response.isSuccessful) {
                Result(
                    data = response.body(),
                    msg = null,
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}").optString("msg")
                Result(
                    data = null,
                    msg = errorMsg,
                    code = response.code()
                )
            }
        }
    }

    // Actualizar solo el nombre de usuario
    suspend fun updateUsername(username: String): Result<Unit> {
        val updateRequest = UserUpdateRequest(username = username, urlFotoPerfil = null) // Solo nombre, foto null
        return withContext(Dispatchers.IO) {
            val response = api.updateUsername(updateRequest).execute()
            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = "Nombre actualizado correctamente",
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}").optString("msg")
                Result(
                    data = null,
                    msg = errorMsg,
                    code = response.code()
                )
            }
        }
    }

    // Actualizar solo la foto de perfil
    suspend fun updateProfilePicture(urlFotoPerfil: String): Result<Unit> {
        val updateRequest = UserUpdateRequest(username = null, urlFotoPerfil = urlFotoPerfil) // Solo foto, nombre null
        return withContext(Dispatchers.IO) {
            val response = api.updateProfilePicture(updateRequest).execute()
            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = "Foto de perfil actualizada correctamente",
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}").optString("msg")
                Result(
                    data = null,
                    msg = errorMsg,
                    code = response.code()
                )
            }
        }
    }
}