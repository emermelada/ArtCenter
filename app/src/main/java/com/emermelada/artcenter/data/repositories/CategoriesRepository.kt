package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

/**
 * Repositorio responsable de las operaciones relacionadas con categorías.
 *
 * Utiliza la instancia de Retrofit para realizar llamadas a la API remota y envuelve
 * las respuestas en objetos [Result].
 */
@Singleton
class CategoriesRepository {

    private val api = RetroFitInstance.api

    /**
     * Crea una nueva categoría en el servidor.
     *
     * @param nombre Nombre de la nueva categoría.
     * @param descripcion Descripción de la nueva categoría.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado,
     *         así como el código HTTP de la respuesta.
     */
    suspend fun createCategory(nombre: String, descripcion: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response =
                api.createCategory(mapOf("nombre" to nombre, "descripcion" to descripcion))
                    .execute()
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

    /**
     * Obtiene todas las categorías disponibles desde el servidor.
     *
     * @return Un [Result] que contiene en `data` una lista de [CategorySimple] si la llamada
     *         fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue. Además,
     *         incluye el código HTTP de la respuesta.
     */
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

    /**
     * Obtiene los detalles completos de una categoría específica.
     *
     * @param id Identificador de la categoría a recuperar.
     * @return Un [Result] que contiene en `data` el objeto [Category] si la llamada fue exitosa,
     *         o `data = null` con un mensaje de error en `msg` si no lo fue. Además,
     *         incluye el código HTTP de la respuesta.
     */
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

    /**
     * Elimina una categoría específica por su identificador.
     *
     * @param id Identificador de la categoría a eliminar.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado,
     *         así como el código HTTP de la respuesta.
     */
    suspend fun deleteCategoryById(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response = api.deleteCategoryById(id).execute()
            if (response.isSuccessful) {
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

    /**
     * Actualiza una categoría existente en el servidor.
     *
     * @param id Identificador de la categoría a actualizar.
     * @param category Objeto [Category] con los nuevos datos de la categoría (id, nombre, descripción).
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado,
     *         así como el código HTTP de la respuesta.
     */
    suspend fun updateCategoryById(id: Int, category: Category): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response = api.updateCategoryById(id, category).execute()
            if (response.isSuccessful) {
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
}
