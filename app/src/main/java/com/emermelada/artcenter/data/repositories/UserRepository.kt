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

    // Actualizar la información del usuario
    suspend fun updateUserInfo(username: String?, urlFotoPerfil: String?): Result<Unit> {
        val updateRequest = UserUpdateRequest(username, urlFotoPerfil)
        return withContext(Dispatchers.IO) {
            val response = api.updateUserInfo(updateRequest).execute()
            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = "Usuario actualizado correctamente",
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