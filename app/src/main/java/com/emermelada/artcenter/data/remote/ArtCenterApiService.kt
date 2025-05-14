package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.profile.ProfilePictureResponse
import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.data.model.profile.UserUpdateRequest
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.data.model.subcategories.SubcategoryRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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

    // Métodos para Subcategorías

    @POST("subcategorias")
    fun createSubcategory(@Body body: SubcategoryRequest): Call<ResponseBody>

    @GET("subcategorias")
    fun getAllSubcategories(): Call<List<Subcategory>>

    @GET("subcategorias/categoria/{idCategoria}")
    fun getSubcategoriesByCategory(@Path("idCategoria") idCategoria: Int): Call<List<Subcategory>>

    @GET("subcategorias/{idCategoria}/{idSubcategoria}")
    fun getSubcategoryById(@Path("idCategoria") idCategoria: Int, @Path("idSubcategoria") idSubcategoria: Int): Call<Subcategory>
}
