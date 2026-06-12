package com.example.zoosmartcare.data.repository

import com.example.zoosmartcare.data.model.*
import com.example.zoosmartcare.data.network.NetworkModule

class AuthRepository {
    private val authApi = NetworkModule.authApi
    private val tokenManager = NetworkModule.tokenManager

    suspend fun login(username: String, password: String): Result<TokenResponse> = runCatching {
        val response = authApi.login(username, password)
        if (response.isSuccessful && response.body() != null) {
            val token = response.body()!!
            tokenManager.saveToken(token.access_token)
            token
        } else {
            throw Exception("Login failed: ${response.message()}")
        }
    }

    suspend fun getCurrentUser(): Result<UserResponse> = runCatching {
        val response = authApi.getCurrentUser()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to get current user: ${response.message()}")
        }
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}

class EnclosureRepository {
    private val adminApi = NetworkModule.adminApi

    suspend fun getAll(): Result<List<EnclosureResponse>> = runCatching {
        val response = adminApi.getEnclosures()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load enclosures: ${response.message()}")
        }
    }

    suspend fun getById(id: Int): Result<EnclosureResponse> = runCatching {
        val response = adminApi.getEnclosureById(id)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to get enclosure: ${response.message()}")
        }
    }

    suspend fun getByQr(qr: String): Result<EnclosureResponse> = runCatching {
        val response = adminApi.getEnclosureByQr(qr)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to find enclosure by QR: ${response.message()}")
        }
    }
}

class AnimalRepository {
    private val businessApi = NetworkModule.businessApi

    suspend fun getAll(speciesId: Int? = null, enclosureId: Int? = null): Result<List<AnimalResponse>> = runCatching {
        val response = businessApi.getAnimals(speciesId, enclosureId)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load animals: ${response.message()}")
        }
    }

    suspend fun getById(id: Int): Result<AnimalResponse> = runCatching {
        val response = businessApi.getAnimalById(id)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to get animal: ${response.message()}")
        }
    }

    suspend fun updateAnimal(id: Int, update: AnimalUpdate): Result<AnimalResponse> = runCatching {
        val response = businessApi.updateAnimal(id, update)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to update animal: ${response.message()}")
        }
    }

    suspend fun getMedicalHistory(animalId: Int): Result<List<MedicalRecordResponse>> = runCatching {
        val response = businessApi.getMedicalHistory(animalId)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load medical history: ${response.message()}")
        }
    }
}

class AlertRepository {
    private val businessApi = NetworkModule.businessApi

    suspend fun getActive(): Result<List<AlertResponse>> = runCatching {
        val response = businessApi.getAlerts()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load alerts: ${response.message()}")
        }
    }

    suspend fun resolve(id: Int): Result<Map<String, String>> = runCatching {
        val response = businessApi.resolveAlert(id)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to resolve alert: ${response.message()}")
        }
    }

    suspend fun getHistory(enclosureId: Int? = null, limit: Int? = null): Result<List<AlertResponse>> = runCatching {
        val response = businessApi.getAlertHistory(enclosureId, limit)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load alert history: ${response.message()}")
        }
    }
}

class TelemetryRepository {
    private val businessApi = NetworkModule.businessApi

    suspend fun getLatest(enclosureId: Int): Result<SensorReadingResponse?> = runCatching {
        val response = businessApi.getLatestTelemetry(enclosureId)
        if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception("Failed to load latest telemetry: ${response.message()}")
        }
    }

    suspend fun getHistory(enclosureId: Int, limit: Int? = null): Result<List<SensorReadingResponse>> = runCatching {
        val response = businessApi.getTelemetryHistory(enclosureId, limit)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load telemetry history: ${response.message()}")
        }
    }
}

class MaintenanceRepository {
    private val businessApi = NetworkModule.businessApi

    suspend fun getLogs(enclosureId: Int): Result<List<MaintenanceLogResponse>> = runCatching {
        val response = businessApi.getMaintenanceLogs(enclosureId)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load maintenance logs: ${response.message()}")
        }
    }

    suspend fun create(userId: Int, enclosureId: Int, actionType: String?, notes: String?): Result<MaintenanceLogResponse> = runCatching {
        val response = businessApi.createMaintenanceLog(MaintenanceLogCreate(userId, enclosureId, actionType, notes))
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to create maintenance log: ${response.message()}")
        }
    }
}

class FeedingRepository {
    private val businessApi = NetworkModule.businessApi

    suspend fun getSchedules(enclosureId: Int): Result<List<FeedingScheduleResponse>> = runCatching {
        val response = businessApi.getFeedingSchedules(enclosureId)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load feeding schedules: ${response.message()}")
        }
    }

    suspend fun create(enclosureId: Int, feedTime: String, portionSize: Float?, foodType: String?, daysOfWeek: String?): Result<FeedingScheduleResponse> = runCatching {
        val response = businessApi.createFeedingSchedule(FeedingScheduleCreate(enclosureId, feedTime, portionSize, foodType, daysOfWeek))
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to create feeding schedule: ${response.message()}")
        }
    }

    suspend fun delete(id: Int): Result<Map<String, String>> = runCatching {
        val response = businessApi.deleteFeedingSchedule(id)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to delete feeding schedule: ${response.message()}")
        }
    }
}

class SpeciesRepository {
    private val businessApi = NetworkModule.businessApi

    suspend fun getAll(): Result<List<SpeciesResponse>> = runCatching {
        val response = businessApi.getSpecies()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to load species: ${response.message()}")
        }
    }
}
