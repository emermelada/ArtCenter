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

@Singleton
class PublicationRepository {
    private val api = RetroFitInstance.api

    // Obtener todas las publicaciones
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

    // Obtener publicación completa por id
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

    suspend fun createPublication(
        imageFile: File,
        publicationRequest: PublicationRequest
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Preparar el archivo imagen
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            // Preparar cada campo como RequestBody de texto plano
            val descripcionBody = publicationRequest.descripcion?.toRequestBody("text/plain".toMediaTypeOrNull())
            val idEtiquetaBody = publicationRequest.id_etiqueta?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            // Llamar al API enviando las partes separadas
            val response = api.createPublication(imagePart, descripcionBody, idEtiquetaBody)

            if (response.isSuccessful) {
                Result(data = null, msg = "Publicación creada correctamente", code = response.code())
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}").optString("msg")
                Result(data = null, msg = errorMsg, code = response.code())
            }
        } catch (e: Exception) {
            Result(data = null, msg = e.localizedMessage ?: "Error desconocido", code = 500)
        }
    }

    // Obtener mis publicaciones
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

    // Obtener publicaciones guardadas
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

    // Guardar / quitar guardado de una publicación
    suspend fun toggleBookmark(publicationId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            val response = api.bookmarkPublication(publicationId).execute()
            if (response.isSuccessful) {
                // 201 = guardado, 200 = eliminado
                Result(data = null, msg = response.body()?.string() ?: "Operación realizada", code = response.code())
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}")
                    .optString("msg")
                Result(data = null, msg = errorMsg, code = response.code())
            }
        }

    // Dar o quitar like a una publicación
    suspend fun toggleLike(publicationId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            val response = api.likePublication(publicationId).execute()
            if (response.isSuccessful) {
                // 201 = like, 200 = unlike
                Result(data = null, msg = response.body()?.string() ?: "Operación realizada", code = response.code())
            } else {
                val errorMsg = JSONObject(response.errorBody()?.string() ?: "{}")
                    .optString("msg")
                Result(data = null, msg = errorMsg, code = response.code())
            }
        }

    // Buscar publicaciones por término en categoría, subcategoría o descripción
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

}
