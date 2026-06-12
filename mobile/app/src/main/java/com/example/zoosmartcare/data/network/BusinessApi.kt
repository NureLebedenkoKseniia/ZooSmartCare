package com.example.zoosmartcare.data.network

import com.example.zoosmartcare.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface BusinessApi {
    @GET("api/business/animals/")
    suspend fun getAnimals(
        @Query("species_id") speciesId: Int? = null,
        @Query("enclosure_id") enclosureId: Int? = null
    ): Response<List<AnimalResponse>>

    @GET("api/business/animals/{id}")
    suspend fun getAnimalById(
        @Path("id") id: Int
    ): Response<AnimalResponse>

    @PUT("api/business/animals/{id}")
    suspend fun updateAnimal(
        @Path("id") id: Int,
        @Body update: AnimalUpdate
    ): Response<AnimalResponse>

    @GET("api/business/species/")
    suspend fun getSpecies(): Response<List<SpeciesResponse>>

    @GET("api/business/alerts/")
    suspend fun getAlerts(): Response<List<AlertResponse>>

    @GET("api/business/alerts/history")
    suspend fun getAlertHistory(
        @Query("enclosure_id") enclosureId: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<AlertResponse>>

    @PUT("api/business/alerts/{id}/resolve")
    suspend fun resolveAlert(
        @Path("id") id: Int
    ): Response<Map<String, String>>

    @GET("api/business/telemetry/enclosure/{id}/latest")
    suspend fun getLatestTelemetry(
        @Path("id") enclosureId: Int
    ): Response<SensorReadingResponse?>

    @GET("api/business/telemetry/history/{id}")
    suspend fun getTelemetryHistory(
        @Path("id") enclosureId: Int,
        @Query("limit") limit: Int? = null
    ): Response<List<SensorReadingResponse>>

    @GET("api/business/enclosures/{id}/schedules")
    suspend fun getFeedingSchedules(
        @Path("id") enclosureId: Int
    ): Response<List<FeedingScheduleResponse>>

    @POST("api/business/schedules/")
    suspend fun createFeedingSchedule(
        @Body schedule: FeedingScheduleCreate
    ): Response<FeedingScheduleResponse>

    @DELETE("api/business/schedules/{id}")
    suspend fun deleteFeedingSchedule(
        @Path("id") id: Int
    ): Response<Map<String, String>>

    @GET("api/business/animals/{id}/medical-history")
    suspend fun getMedicalHistory(
        @Path("id") animalId: Int
    ): Response<List<MedicalRecordResponse>>

    @POST("api/business/maintenance-logs/")
    suspend fun createMaintenanceLog(
        @Body log: MaintenanceLogCreate
    ): Response<MaintenanceLogResponse>

    @GET("api/business/enclosures/{id}/logs")
    suspend fun getMaintenanceLogs(
        @Path("id") enclosureId: Int
    ): Response<List<MaintenanceLogResponse>>
}
