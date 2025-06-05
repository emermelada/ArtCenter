package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.data.model.comments.CommentCreateRequest
import com.emermelada.artcenter.data.model.comments.CommentCreateResponse
import com.emermelada.artcenter.data.model.comments.CommentDeleteResponse
import com.emermelada.artcenter.data.model.comments.CommentSimple
import com.emermelada.artcenter.data.model.profile.ProfilePictureResponse
import com.emermelada.artcenter.data.model.profile.User
import com.emermelada.artcenter.data.model.profile.UserUpdateRequest
import com.emermelada.artcenter.data.model.publications.Publication
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
import retrofit2.http.Query

/**
 * Define los endpoints remotos para la comunicación con el servidor de ArtCenter.
 * Incluye operaciones para usuarios, categorías, subcategorías, publicaciones, etiquetas y comentarios.
 */
interface ArtCenterApiService {

    /**
     * Obtiene la información básica del usuario autenticado.
     *
     * @return Un objeto [Call] que, al ejecutar, retorna la información del usuario en forma de [User].
     */
    @GET("user")
    fun getUserInfo(): Call<User>

    /**
     * Actualiza el nombre de usuario del usuario autenticado.
     *
     * @param body Objeto de tipo [UserUpdateRequest] que contiene el nuevo nombre de usuario.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @PUT("user/username")
    fun updateUsername(@Body body: UserUpdateRequest): Call<ResponseBody>

    /**
     * Sube (actualiza) la foto de perfil del usuario autenticado.
     *
     * @param file Parte multipart que contiene la imagen a subir.
     * @return Objeto [ProfilePictureResponse] con el mensaje de resultado y la URL de la nueva foto de perfil.
     */
    @Multipart
    @PUT("user/profile-picture")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part
    ): ProfilePictureResponse

    /**
     * Crea una nueva categoría en el sistema.
     *
     * @param body Mapa con campos tipo String que representan los datos de la categoría (por ejemplo, "nombre" y "descripcion").
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @POST("categorias")
    fun createCategory(@Body body: Map<String, String>): Call<ResponseBody>

    /**
     * Recupera todas las categorías disponibles en el sistema.
     *
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [CategorySimple] con información mínima de cada categoría.
     */
    @GET("categorias")
    fun getAllCategories(): Call<List<CategorySimple>>

    /**
     * Obtiene los detalles completos de una categoría específica.
     *
     * @param id Identificador de la categoría a recuperar.
     * @return Un objeto [Call] que, al ejecutar, retorna la categoría en forma de [Category].
     */
    @GET("categorias/{id}")
    fun getCategoryById(@Path("id") id: Int): Call<Category>

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id Identificador de la categoría a eliminar.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @DELETE("categorias/{id}")
    fun deleteCategoryById(@Path("id") id: Int): Call<ResponseBody>

    /**
     * Actualiza una categoría existente por su identificador.
     *
     * @param id Identificador de la categoría a actualizar.
     * @param category Objeto de tipo [Category] que contiene los nuevos datos (id, nombre, descripción).
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @PUT("categorias/{id}")
    fun updateCategoryById(@Path("id") id: Int, @Body category: Category): Call<ResponseBody>

    /**
     * Crea una nueva subcategoría dentro de una categoría específica.
     *
     * @param body Objeto de tipo [SubcategoryRequest] con los datos de la subcategoría a crear.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @POST("subcategorias")
    fun createSubcategory(@Body body: SubcategoryRequest): Call<ResponseBody>

    /**
     * Obtiene las subcategorías asociadas a una categoría en particular.
     *
     * @param idCategoria Identificador de la categoría cuyos subelementos se desean obtener.
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [Subcategory] correspondientes a la categoría indicada.
     */
    @GET("subcategorias/categoria/{idCategoria}")
    fun getSubcategoriesByCategory(@Path("idCategoria") idCategoria: Int): Call<List<Subcategory>>

    /**
     * Recupera la información de una subcategoría específica dentro de una categoría.
     *
     * @param idCategoria Identificador de la categoría.
     * @param idSubcategoria Identificador de la subcategoría.
     * @return Un objeto [Call] que, al ejecutar, retorna la subcategoría en forma de [Subcategory].
     */
    @GET("subcategorias/{idCategoria}/{idSubcategoria}")
    fun getSubcategoryById(
        @Path("idCategoria") idCategoria: Int,
        @Path("idSubcategoria") idSubcategoria: Int
    ): Call<Subcategory>

    /**
     * Elimina una subcategoría específica dentro de una categoría.
     *
     * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a eliminar.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @DELETE("subcategorias/{idCategoria}/{idSubcategoria}")
    fun deleteSubcategory(
        @Path("idCategoria") idCategoria: Int,
        @Path("idSubcategoria") idSubcategoria: Int
    ): Call<ResponseBody>

    /**
     * Actualiza una subcategoría existente dentro de una categoría.
     *
     * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
     * @param idSubcategoria Identificador de la subcategoría a actualizar.
     * @param subcategory Objeto de tipo [Subcategory] con los nuevos datos de la subcategoría.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @PUT("subcategorias/{idCategoria}/{idSubcategoria}")
    fun updateSubcategory(
        @Path("idCategoria") idCategoria: Int,
        @Path("idSubcategoria") idSubcategoria: Int,
        @Body subcategory: Subcategory
    ): Call<ResponseBody>

    /**
     * Recupera todas las publicaciones disponibles, paginadas.
     *
     * @param page Página a recuperar (indexada desde 0).
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [PublicationSimple] con información mínima de cada publicación.
     */
    @GET("publicaciones")
    fun getAllPublications(@Query("page") page: Int): Call<List<PublicationSimple>>

    /**
     * Recupera las publicaciones del usuario autenticado, paginadas.
     *
     * @param page Página a recuperar (indexada desde 0).
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [PublicationSimple] correspondientes al usuario actual.
     */
    @GET("publicaciones/mias")
    fun getMyPublications(@Query("page") page: Int): Call<List<PublicationSimple>>

    /**
     * Recupera las publicaciones guardadas por el usuario autenticado, paginadas.
     *
     * @param page Página a recuperar (indexada desde 0).
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [PublicationSimple] que el usuario ha marcado como guardadas.
     */
    @GET("publicaciones/guardadas")
    fun getSavedPublications(@Query("page") page: Int): Call<List<PublicationSimple>>

    /**
     * Obtiene los detalles completos de una publicación específica.
     *
     * @param id Identificador de la publicación a recuperar.
     * @return Un objeto [Call] que, al ejecutar, retorna la publicación en forma de [Publication].
     */
    @GET("publicaciones/{id}")
    fun getPublicationById(@Path("id") id: Int): Call<Publication>

    /**
     * Crea una nueva publicación con contenido multimedia.
     *
     * @param file Parte multipart que contiene el archivo (imagen, video, etc.).
     * @param descripcion Parte opcional con la descripción de la publicación.
     * @param id_etiqueta Parte opcional con el identificador de la etiqueta a asignar.
     * @return Un objeto [Response] que al ejecutarse retorna un [ResponseBody] con la respuesta del servidor.
     */
    @Multipart
    @POST("publicaciones")
    suspend fun createPublication(
        @Part file: MultipartBody.Part,
        @Part("descripcion") descripcion: RequestBody?,
        @Part("id_etiqueta") id_etiqueta: RequestBody?
    ): Response<ResponseBody>

    /**
     * Marca o desmarca la publicación indicada como guardada para el usuario autenticado.
     *
     * @param id Identificador de la publicación a guardar o desmarcar.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @POST("publicaciones/{id}/guardar")
    fun bookmarkPublication(@Path("id") id: Int): Call<ResponseBody>

    /**
     * Marca o desmarca el "like" de la publicación indicada para el usuario autenticado.
     *
     * @param id Identificador de la publicación a la que se le aplica o remueve el "like".
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @POST("publicaciones/{id}/like")
    fun likePublication(@Path("id") id: Int): Call<ResponseBody>

    /**
     * Realiza una búsqueda de publicaciones por texto, devolviendo resultados paginados.
     *
     * @param query Texto a buscar en las publicaciones.
     * @param page Página a recuperar (indexada desde 0). Valor por defecto: 0.
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [PublicationSimple] que coinciden con la búsqueda.
     */
    @GET("publicaciones/buscar")
    fun searchPublications(
        @Query("q") query: String,
        @Query("page") page: Int = 0
    ): Call<List<PublicationSimple>>

    /**
     * Elimina una publicación específica por su identificador.
     *
     * @param id Identificador de la publicación a eliminar.
     * @return Un objeto [Call] que, al ejecutar, retorna un [ResponseBody] con la respuesta del servidor.
     */
    @DELETE("publicaciones/{id}")
    fun deletePublicationById(@Path("id") id: Int): Call<ResponseBody>

    /**
     * Recupera todas las etiquetas disponibles en el sistema.
     *
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [Tag] con todas las etiquetas.
     */
    @GET("etiquetas")
    fun getAllTags(): Call<List<Tag>>

    /**
     * Obtiene los comentarios asociados a una publicación específica.
     *
     * @param publicationId Identificador de la publicación cuyos comentarios se desean recuperar.
     * @return Un objeto [Call] que, al ejecutar, retorna una lista de [CommentSimple] de la publicación indicada.
     */
    @GET("publicaciones/{id_publicacion}/comentarios")
    fun getCommentsByPublication(@Path("id_publicacion") publicationId: Int): Call<List<CommentSimple>>

    /**
     * Crea un nuevo comentario en la publicación indicada.
     *
     * @param publicationId Identificador de la publicación donde se insertará el comentario.
     * @param commentCreateRequest Objeto [CommentCreateRequest] que contiene el texto del comentario.
     * @return Un objeto [Call] que, al ejecutar, retorna un [CommentCreateResponse] con el resultado y el ID asignado.
     */
    @POST("publicaciones/{id_publicacion}/comentarios")
    fun createComment(
        @Path("id_publicacion") publicationId: Int,
        @Body commentCreateRequest: CommentCreateRequest
    ): Call<CommentCreateResponse>

    /**
     * Elimina un comentario específico por su identificador.
     *
     * @param commentId Identificador del comentario a eliminar.
     * @return Un objeto [Call] que, al ejecutar, retorna un [CommentDeleteResponse] con el mensaje de resultado.
     */
    @DELETE("comentarios/{id_comentario}")
    fun deleteComment(@Path("id_comentario") commentId: Int): Call<CommentDeleteResponse>
}
