package com.emermelada.artcenter.data.repositories

import com.emermelada.artcenter.data.model.auth.LoginRequest
import com.emermelada.artcenter.data.model.auth.LoginResponse
import com.emermelada.artcenter.data.model.auth.RegisterRequest
import com.emermelada.artcenter.data.model.auth.RegisterResponse
import com.emermelada.artcenter.data.remote.RetroFitInstance
import com.emermelada.artcenter.data.model.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Singleton

@Singleton
class AuthRepository {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            val response = RetroFitInstance.authApi.login(loginRequest).execute()
            if(response.code() == 200){
                Result<LoginResponse>(
                    data = response.body(),
                    msg = "",
                    code = response.code()
                )
            } else{
                Result<LoginResponse>(
                    data = null,
                    msg = JSONObject(response.errorBody()?.string() ?: "{}").optString("msg"),
                    code = response.code()
                )
            }
        }
    }

    suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            val response = RetroFitInstance.authApi.register(registerRequest).execute()
            if (response.code() in listOf(200, 201)) {
                Result(
                    data = response.body(),
                    msg = null,
                    code = response.code()
                )
            } else {
                Result(
                    data = null,
                    msg = response.errorBody()?.string(),
                    code = response.code()
                )
            }
        }
    }

}