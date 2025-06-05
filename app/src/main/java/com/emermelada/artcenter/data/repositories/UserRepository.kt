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

/**
 * Repositorio responsable de las operaciones relacionadas con el usuario.
 *
 * Utiliza la instancia de Retrofit para realizar llamadas a la API remota y envuelve
 * las respuestas en objetos [Result].
 */
@Singleton
class UserRepository {
    private val api = RetroFitInstance.api

    /**
     * Obtiene la información del usuario autenticado.
     *
     * @return Un [Result] que contiene en `data` un objeto [User] si la llamada fue exitosa,
     *         o `data = null` con un mensaje de error en `msg` si no lo fue. Además,
     *         incluye el código HTTP de la respuesta.
     */
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

    /**
     * Actualiza solo el nombre de usuario del usuario autenticado.
     *
     * @param username Nuevo nombre de usuario.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado de la operación,
     *         así como el código HTTP de la respuesta.
     */
    suspend fun updateUsername(username: String): Result<Unit> {
        val updateRequest = UserUpdateRequest(username = username, urlFotoPerfil = null)
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

    /**
     * Actualiza la foto de perfil del usuario autenticado.
     *
     * @param file Parte multipart que contiene el archivo de imagen a subir.
     * @return Un [Result] que contiene en `data` la URL de la nueva foto si la operación fue exitosa,
     *         o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         En caso de éxito, el código se devuelve como 200; en caso de excepción, como 500.
     */
    suspend fun updateProfilePicture(file: MultipartBody.Part): Result<String> {
        return try {
            val response = api.uploadProfilePicture(file)
            Result(
                data = response.urlFotoPerfil,
                msg = "Foto de perfil actualizada correctamente",
                code = 200
            )
        } catch (e: Exception) {
            Result(
                data = null,
                msg = "Error al actualizar la foto: ${e.localizedMessage}",
                code = 500
            )
        }
    }
}