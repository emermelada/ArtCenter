package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

@Singleton
class CategoriesRepository {

    private val api = RetroFitInstance.api

    suspend fun createCategory(nombre: String, descripcion: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response = api.createCategory(mapOf("nombre" to nombre, "descripcion" to descripcion)).execute()
            if (response.code() in listOf(200, 201)) {
                val msg = JSONObject(response.body()?.string() ?: "{}").optString("msg")
                Result(
                    data = null,
                    msg = msg,
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

    suspend fun getAllCategories(): Result<List<CategorySimple>> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllCategories().execute()
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

    suspend fun getCategoryById(id: Int): Result<Category> {
        return withContext(Dispatchers.IO) {
            val response = api.getCategoryById(id).execute()
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
