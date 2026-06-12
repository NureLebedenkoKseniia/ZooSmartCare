package com.example.zoosmartcare.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String
)

@Serializable
data class UserResponse(
    val user_id: Int,
    val full_name: String,
    val role: String,
    val contact_info: String? = null
)

@Serializable
data class EnclosureResponse(
    val enclosure_id: Int,
    val name: String,
    val qr_code_string: String? = null,
    val geo_location: String? = null
)

@Serializable
data class AnimalResponse(
    val animal_id: Int,
    val nickname: String,
    val species_id: Int,
    val enclosure_id: Int,
    val birth_date: String? = null
)

@Serializable
data class AnimalUpdate(
    val nickname: String? = null,
    val species_id: Int? = null,
    val enclosure_id: Int? = null,
    val birth_date: String? = null
)

@Serializable
data class SpeciesResponse(
    val species_id: Int,
    val scientific_name: String,
    val common_name: String? = null,
    val general_diet_info: String? = null
)

@Serializable
data class AlertResponse(
    val alert_id: Int,
    val enclosure_id: Int,
    val alert_type: String? = null,
    val message: String? = null,
    val status: String? = null,
    val timestamp: String
)

@Serializable
data class SensorReadingResponse(
    val reading_id: Int,
    val device_id: Int,
    val temperature_val: Float? = null,
    val humidity_val: Float? = null,
    val light_val: Float? = null,
    val timestamp: String
)

@Serializable
data class FeedingScheduleResponse(
    val schedule_id: Int,
    val enclosure_id: Int,
    val feed_time: String,
    val portion_size: Float? = null,
    val food_type: String? = null,
    val days_of_week: String? = null
)

@Serializable
data class FeedingScheduleCreate(
    val enclosure_id: Int,
    val feed_time: String,
    val portion_size: Float? = null,
    val food_type: String? = null,
    val days_of_week: String? = null
)

@Serializable
data class MedicalRecordResponse(
    val record_id: Int,
    val animal_id: Int,
    val user_id: Int,
    val event_date: String,
    val diagnosis: String? = null,
    val severity: String? = null,
    val treatment_notes: String? = null
)

@Serializable
data class MaintenanceLogResponse(
    val log_id: Int,
    val user_id: Int,
    val enclosure_id: Int,
    val action_type: String? = null,
    val notes: String? = null,
    val timestamp: String
)

@Serializable
data class MaintenanceLogCreate(
    val user_id: Int,
    val enclosure_id: Int,
    val action_type: String? = null,
    val notes: String? = null
)
