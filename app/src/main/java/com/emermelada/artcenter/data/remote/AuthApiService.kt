package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.data.model.auth.LoginRequest
import com.emermelada.artcenter.data.model.auth.LoginResponse
import com.emermelada.artcenter.data.model.auth.RegisterRequest
import com.emermelada.artcenter.data.model.auth.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Define los endpoints de autenticación para iniciar sesión y registrar nuevos usuarios.
 */
interface AuthApiService {

    /**
     * Envía una solicitud de inicio de sesión al servidor.
     *
     * @param loginRequest Objeto [LoginRequest] que contiene el correo electrónico y la contraseña del usuario.
     * @return Un objeto [Call] que, al ejecutarse, retorna un [LoginResponse] con el token de autenticación, el rol y el ID del usuario.
     */
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    /**
     * Envía una solicitud de registro al servidor para crear una nueva cuenta.
     *
     * @param registerRequest Objeto [RegisterRequest] que contiene el correo electrónico, la contraseña y el nombre de usuario.
     * @return Un objeto [Call] que, al ejecutarse, retorna un [RegisterResponse] con un mensaje de confirmación del registro.
     */
    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
}
