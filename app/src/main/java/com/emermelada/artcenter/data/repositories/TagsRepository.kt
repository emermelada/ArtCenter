package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.model.tags.Tag
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

@Singleton
class TagsRepository {
    private val api = RetroFitInstance.api

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
