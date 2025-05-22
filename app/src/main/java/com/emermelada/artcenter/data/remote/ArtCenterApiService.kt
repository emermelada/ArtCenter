package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.profile.ProfilePictureResponse
import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.data.model.profile.UserUpdateRequest
import com.emermelada.artcenter.data.model.publications.Publication
import com.emermelada.artcenter.data.model.publications.PublicationRequest
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.data.model.subcategories.SubcategoryRequest
import com.emermelada.artcenter.data.model.tags.Tag
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ArtCenterApiService {
    // Métodos para Usuario
    @GET("user")
    fun getUserInfo(): Call<User>

    @PUT("user/username")
    fun updateUsername(@Body body: UserUpdateRequest): Call<ResponseBody>

    @Multipart
    @PUT("user/profile-picture")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part
    ): ProfilePictureResponse

    // Métodos para categorias
    @POST("categorias")
    fun createCategory(@Body body: Map<String, String>): Call<ResponseBody>

    @GET("categorias")
    fun getAllCategories(): Call<List<CategorySimple>>

    @GET("categorias/{id}")
    fun getCategoryById(@Path("id") id: Int): Call<Category>

    // Método para eliminar categoría por id
    @DELETE("categorias/{id}")
    fun deleteCategoryById(@Path("id") id: Int): Call<ResponseBody>

    // Método para actualizar categoría por id
    @PUT("categorias/{id}")
    fun updateCategoryById(@Path("id") id: Int, @Body category: Category): Call<ResponseBody>

    // Métodos para Subcategorías

    @POST("subcategorias")
    fun createSubcategory(@Body body: SubcategoryRequest): Call<ResponseBody>

    @GET("subcategorias")
    fun getAllSubcategories(): Call<List<Subcategory>>

    @GET("subcategorias/categoria/{idCategoria}")
    fun getSubcategoriesByCategory(@Path("idCategoria") idCategoria: Int): Call<List<Subcategory>>

    @GET("subcategorias/{idCategoria}/{idSubcategoria}")
    fun getSubcategoryById(@Path("idCategoria") idCategoria: Int, @Path("idSubcategoria") idSubcategoria: Int): Call<Subcategory>

    @DELETE("subcategorias/{idCategoria}/{idSubcategoria}")
    fun deleteSubcategory(
        @Path("idCategoria") idCategoria: Int,
        @Path("idSubcategoria") idSubcategoria: Int
    ): Call<ResponseBody>

    @PUT("subcategorias/{idCategoria}/{idSubcategoria}")
    fun updateSubcategory(
        @Path("idCategoria") idCategoria: Int,
        @Path("idSubcategoria") idSubcategoria: Int,
        @Body subcategory: Subcategory
    ): Call<ResponseBody>

    // Métodos para publicaciones

    @GET("publicaciones")
    fun getAllPublications(): Call<List<PublicationSimple>>

    @GET("publicaciones/{id}")
    fun getPublicationById(@Path("id") id: Int): Call<Publication>

    @Multipart
    @POST("publicaciones")
    suspend fun createPublication(
        @Part file: MultipartBody.Part,
        @Part("descripcion") descripcion: RequestBody?,
        @Part("id_etiqueta") id_etiqueta: RequestBody?
    ): Response<ResponseBody>

    // Métodos para etiquetas

    @GET("etiquetas")
    fun getAllTags(): Call<List<Tag>>
}
