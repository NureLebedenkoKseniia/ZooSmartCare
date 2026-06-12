package com.example.zoosmartcare.data.network

import com.example.zoosmartcare.data.model.EnclosureResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {
    @GET("api/admin/enclosures/")
    suspend fun getEnclosures(): Response<List<EnclosureResponse>>

    @GET("api/admin/enclosures/{id}")
    suspend fun getEnclosureById(
        @Path("id") id: Int
    ): Response<EnclosureResponse>

    @GET("api/admin/enclosures/by-qr")
    suspend fun getEnclosureByQr(
        @Query("qr") qr: String
    ): Response<EnclosureResponse>
}
