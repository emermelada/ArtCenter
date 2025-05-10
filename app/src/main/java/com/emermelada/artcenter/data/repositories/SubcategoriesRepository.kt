package com.emermelada.artcenter.data.repositories

import android.util.Log
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.data.model.subcategories.SubcategoryRequest
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

@Singleton
class SubcategoriesRepository {
    private val api = RetroFitInstance.api

    // Método para crear una subcategoría
    suspend fun createSubcategory(subcategoryRequest: SubcategoryRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response = api.createSubcategory(subcategoryRequest).execute()

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

    // Método para obtener todas las subcategorías
    suspend fun getAllSubcategories(): Result<List<Subcategory>> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllSubcategories().execute()
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

    // Método para obtener las subcategorías por categoría
    suspend fun getSubcategoriesByCategory(idCategoria: Int): Result<List<Subcategory>> {
        return withContext(Dispatchers.IO) {
            val response = api.getSubcategoriesByCategory(idCategoria).execute()
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

    // Método para obtener una subcategoría por su ID
    suspend fun getSubcategoryById(idCategoria: Int, idSubcategoria: Int): Result<Subcategory> {
        return withContext(Dispatchers.IO) {
            val response = api.getSubcategoryById(idCategoria, idSubcategoria).execute()
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
