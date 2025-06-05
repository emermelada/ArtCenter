package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.publications.Publication
import com.emermelada.artcenter.data.model.publications.PublicationRequest
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Singleton

/**
 * Repositorio responsable de las operaciones relacionadas con publicaciones.
 *
 * Utiliza la instancia de Retrofit para realizar llamadas a la API remota y envuelve
 * las respuestas en objetos [Result].
 */
@Singleton
class PublicationRepository {
    private val api = RetroFitInstance.api

    /**
     * Obtiene todas las publicaciones disponibles desde el servidor de manera paginada.
     *
     * @param page Número de página a recuperar (indexada desde 0).
     * @return Un [Result] que contiene en `data` una lista de [PublicationSimple] si la llamada
     *         fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta.
     */
    suspend fun getAllPublications(page: Int): Result<List<PublicationSimple>> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllPublications(page).execute()
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
     * Obtiene los detalles completos de una publicación específica por su identificador.
     *
     * @param id Identificador de la publicación a recuperar.
     * @return Un [Result] que contiene en `data` un objeto [Publication] si la llamada fue exitosa,
     *         o `data = null` con un mensaje de error en `msg` si no lo fue. Además,
     *         incluye el código HTTP de la respuesta.
     */
    suspend fun getPublicationById(id: Int): Result<Publication> {
        return withContext(Dispatchers.IO) {
            val response = api.getPublicationById(id).execute()
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
     * Crea una nueva publicación que incluye un archivo multimedia y datos adicionales.
     *
     * @param imageFile Archivo de imagen a subir.
     * @param publicationRequest Objeto [PublicationRequest] que contiene la descripción y el identificador de etiqueta.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado de la creación,
     *         así como el código HTTP de la respuesta. Si ocurre una excepción, devuelve `code = 500`.
     */
    suspend fun createPublication(
        imageFile: File,
        publicationRequest: PublicationRequest
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            val descripcionBody =
                publicationRequest.descripcion?.toRequestBody("text/plain".toMediaTypeOrNull())
            val idEtiquetaBody = publicationRequest.id_etiqueta?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.createPublication(imagePart, descripcionBody, idEtiquetaBody)

            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = "Publicación creada correctamente",
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}").optString("msg")
                Result(data = null, msg = errorMsg, code = response.code())
            }
        } catch (e: Exception) {
            Result(data = null, msg = e.localizedMessage ?: "Error desconocido", code = 500)
        }
    }

    /**
     * Obtiene las publicaciones creadas por el usuario autenticado de manera paginada.
     *
     * @param page Número de página a recuperar (indexada desde 0).
     * @return Un [Result] que contiene en `data` una lista de [PublicationSimple] si la llamada
     *         fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta.
     */
    suspend fun getMyPublications(page: Int): Result<List<PublicationSimple>> =
        withContext(Dispatchers.IO) {
            val response = api.getMyPublications(page).execute()
            if (response.isSuccessful) {
                Result(
                    data = response.body(),
                    msg = null,
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}")
                    .optString("msg")
                Result(
                    data = null,
                    msg = errorMsg,
                    code = response.code()
                )
            }
        }

    /**
     * Obtiene las publicaciones guardadas por el usuario autenticado de manera paginada.
     *
     * @param page Número de página a recuperar (indexada desde 0).
     * @return Un [Result] que contiene en `data` una lista de [PublicationSimple] si la llamada
     *         fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta.
     */
    suspend fun getSavedPublications(page: Int): Result<List<PublicationSimple>> =
        withContext(Dispatchers.IO) {
            val response = api.getSavedPublications(page).execute()
            if (response.isSuccessful) {
                Result(
                    data = response.body(),
                    msg = null,
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}")
                    .optString("msg")
                Result(
                    data = null,
                    msg = errorMsg,
                    code = response.code()
                )
            }
        }

    /**
     * Marca o desmarca una publicación como guardada para el usuario autenticado.
     *
     * @param publicationId Identificador de la publicación a guardar o desmarcar.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado de la operación,
     *         así como el código HTTP de la respuesta.
     */
    suspend fun toggleBookmark(publicationId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            val response = api.bookmarkPublication(publicationId).execute()
            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = response.body()?.string() ?: "Operación realizada",
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}")
                    .optString("msg")
                Result(data = null, msg = errorMsg, code = response.code())
            }
        }

    /**
     * Marca o desmarca el "like" de una publicación para el usuario autenticado.
     *
     * @param publicationId Identificador de la publicación a la que se aplica o remueve el "like".
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado de la operación,
     *         así como el código HTTP de la respuesta.
     */
    suspend fun toggleLike(publicationId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            val response = api.likePublication(publicationId).execute()
            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = response.body()?.string() ?: "Operación realizada",
                    code = response.code()
                )
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}")
                    .optString("msg")
                Result(data = null, msg = errorMsg, code = response.code())
            }
        }

    /**
     * Busca publicaciones por término en categoría, subcategoría o descripción de forma paginada.
     *
     * @param query Texto a buscar en las publicaciones.
     * @param page Número de página a recuperar (indexada desde 0). Valor por defecto: 0.
     * @return Un [Result] que contiene en `data` una lista de [PublicationSimple] si la llamada
     *         fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta.
     */
    suspend fun searchPublications(query: String, page: Int = 0): Result<List<PublicationSimple>> =
        withContext(Dispatchers.IO) {
            val response = api.searchPublications(query, page).execute()
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

    /**
     * Elimina una publicación por su identificador.
     *
     * @param publicationId Identificador de la publicación a eliminar.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado de la eliminación,
     *         así como el código HTTP de la respuesta. Si ocurre una excepción, devuelve `code = 500`.
     */
    suspend fun deletePublication(publicationId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.deletePublicationById(publicationId).execute()
                if (response.isSuccessful) {
                    Result(
                        data = null,
                        msg = "Publicación eliminada correctamente",
                        code = response.code()
                    )
                } else {
                    val errorMsg =
                        JSONObject(response.errorBody()?.string() ?: "{}").optString("msg")
                    Result(data = null, msg = errorMsg, code = response.code())
                }
            } catch (e: Exception) {
                Result(data = null, msg = e.localizedMessage ?: "Error desconocido", code = 500)
            }
        }
}
