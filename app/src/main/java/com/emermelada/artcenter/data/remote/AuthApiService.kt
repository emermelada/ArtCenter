package com.emermelada.artcenter.data.remote

import com.emermelada.artcenter.data.model.auth.LoginRequest
import com.emermelada.artcenter.data.model.auth.LoginResponse
import com.emermelada.artcenter.data.model.auth.RegisterRequest
import com.emermelada.artcenter.data.model.auth.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
}