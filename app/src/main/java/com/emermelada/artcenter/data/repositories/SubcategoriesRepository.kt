package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.result.Result
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.data.model.subcategories.SubcategoryRequest
import com.emermelada.artcenter.data.remote.RetroFitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

/**
 * Repositorio responsable de las operaciones relacionadas con subcategorias.
 *
 * Utiliza la instancia de Retrofit para realizar llamadas a la API remota y envuelve
 * las respuestas en objetos [Result].
 */
@Singleton
class SubcategoriesRepository {
    private val api = RetroFitInstance.api

    /**
     * Crea una nueva subcategoría en el servidor.
     *
     * @param subcategoryRequest Objeto [SubcategoryRequest] que contiene los datos de la subcategoría a crear.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado, así como el código HTTP de la respuesta.
     */
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

    /**
     * Obtiene las subcategorías asociadas a una categoría específica.
     *
     * @param idCategoria Identificador de la categoría cuyos subelementos se desean recuperar.
     * @return Un [Result] que contiene en `data` una lista de [Subcategory] correspondientes a la categoría indicada
     *         si la llamada fue exitosa, o `data = null` con un mensaje de error en `msg` si no lo fue. Además, incluye el código HTTP de la respuesta.
     */
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

    /**
     * Recupera una subcategoría específica dentro de una categoría.
     *
     * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a recuperar.
     * @return Un [Result] que contiene en `data` un objeto [Subcategory] si la llamada fue exitosa,
     *         o `data = null` con un mensaje de error en `msg` si no lo fue. Además, incluye el código HTTP de la respuesta.
     */
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

    /**
     * Elimina una subcategoría específica dentro de una categoría.
     *
     * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a eliminar.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado, así como el código HTTP de la respuesta.
     */
    suspend fun deleteSubcategory(idCategoria: Int, idSubcategoria: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response = api.deleteSubcategory(idCategoria, idSubcategoria).execute()
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
     * Actualiza una subcategoría existente dentro de una categoría.
     *
     * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a actualizar.
     * @param subcategory Objeto [Subcategory] con los nuevos datos de la subcategoría.
     * @return Un [Result] con `data = null` y un mensaje (`msg`) indicando el resultado, así como el código HTTP de la respuesta.
     */
    suspend fun updateSubcategory(
        idCategoria: Int,
        idSubcategoria: Int,
        subcategory: Subcategory
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val response = api.updateSubcategory(idCategoria, idSubcategoria, subcategory).execute()
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
