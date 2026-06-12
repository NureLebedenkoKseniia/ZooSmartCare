package com.example.zoosmartcare.data.network

import com.example.zoosmartcare.data.model.TokenResponse
import com.example.zoosmartcare.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @FormUrlEncoded
    @POST("api/admin/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<TokenResponse>

    @GET("api/admin/auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>
}
