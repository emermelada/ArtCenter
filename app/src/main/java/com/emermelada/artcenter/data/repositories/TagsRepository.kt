package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.model.tags.Tag
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

/**
 * Repositorio responsable de las operaciones relacionadas con etiquetas (tags).
 *
 * Utiliza la instancia de Retrofit para realizar llamadas a la API remota
 * y envuelve las respuestas en objetos [Result].
 */
@Singleton
class TagsRepository {
    private val api = RetroFitInstance.api

    /**
     * Obtiene todas las etiquetas disponibles en el servidor.
     *
     * @return Un [Result] que contiene en `data` una lista de [Tag] si la llamada fue exitosa,
     *         o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta en `code`.
     */
    suspend fun getAllTags(): Result<List<Tag>> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllTags().execute()
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
}
