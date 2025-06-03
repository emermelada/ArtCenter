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

@Singleton
class CommentRepository {
    private val api = RetroFitInstance.api

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
