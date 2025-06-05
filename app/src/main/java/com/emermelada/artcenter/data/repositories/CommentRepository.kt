package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.comments.CommentCreateRequest
import com.emermelada.artcenter.data.model.comments.CommentDeleteResponse
import com.emermelada.artcenter.data.model.comments.CommentSimple
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

/**
 * Repositorio responsable de las operaciones relacionadas con comentarios.
 *
 * Utiliza la instancia de Retrofit para realizar llamadas a la API remota y envuelve
 * las respuestas en objetos [Result].
 */
@Singleton
class CommentRepository {
    private val api = RetroFitInstance.api

    /**
     * Obtiene los comentarios asociados a una publicación específica.
     *
     * @param publicationId Identificador de la publicación cuyos comentarios se desean recuperar.
     * @return Un [Result] que contiene en `data` una lista de [CommentSimple] si la llamada
     *         fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue. Además,
     *         incluye el código HTTP de la respuesta.
     */
    suspend fun getCommentsByPublication(publicationId: Int): Result<List<CommentSimple>> {
        return withContext(Dispatchers.IO) {
            val response = api.getCommentsByPublication(publicationId).execute()
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
     * Crea un nuevo comentario en una publicación específica.
     *
     * @param publicationId Identificador de la publicación donde se insertará el comentario.
     * @param contenido Texto que contendrá el comentario que el usuario desea publicar.
     * @return Un [Result] que contiene en `data` el identificador del comentario recién creado
     *         si la llamada fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta.
     */
    suspend fun createComment(publicationId: Int, contenido: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            val request = CommentCreateRequest(contenido)
            val response = api.createComment(publicationId, request).execute()
            if (response.isSuccessful) {
                val body = response.body()
                Result(
                    data = body?.id_comentario,
                    msg = body?.msg,
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
     * Elimina un comentario específico por su identificador.
     *
     * @param commentId Identificador del comentario a eliminar.
     * @return Un [Result] que contiene en `data` null y un mensaje en `msg` que indica el resultado de la eliminación
     *         si la llamada fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue.
     *         Además, incluye el código HTTP de la respuesta.
     */
    suspend fun deleteComment(commentId: Int): Result<CommentDeleteResponse> {
        return withContext(Dispatchers.IO) {
            val response = api.deleteComment(commentId).execute()
            if (response.isSuccessful) {
                Result(
                    data = null,
                    msg = response.body()?.msg ?: "Comentario eliminado correctamente",
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
