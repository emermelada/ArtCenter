package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.data.model.profile.UserUpdateRequest
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
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

    suspend fun updateProfilePicture(file: MultipartBody.Part): Result<String> {
        return try {
            // Llamamos a la función suspendida de Retrofit que devuelve el objeto con el mensaje y la URL
            val response = api.uploadProfilePicture(file)  // Ahora devuelve ProfilePictureResponse

            // Si la solicitud es exitosa, retornamos el resultado
            Result(
                data = response.urlFotoPerfil,  // Devolvemos la URL de la imagen
                msg = "Foto de perfil actualizada correctamente",
                code = 200  // El código de éxito HTTP
            )
        } catch (e: Exception) {
            // Si ocurre un error, capturamos la excepción
            Result(
                data = null,
                msg = "Error al actualizar la foto: ${e.localizedMessage}",
                code = 500
            )
        }
    }

}