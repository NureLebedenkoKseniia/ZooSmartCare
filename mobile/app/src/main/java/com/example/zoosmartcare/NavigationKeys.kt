package com.example.zoosmartcare

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Login : NavKey

@Serializable
data object Dashboard : NavKey

@Serializable
data object EnclosureList : NavKey

@Serializable
data class EnclosureDetail(val enclosureId: Int) : NavKey

@Serializable
data object AnimalList : NavKey

@Serializable
data class AnimalDetail(val animalId: Int) : NavKey

@Serializable
data object AlertList : NavKey

@Serializable
data object QrScanner : NavKey

@Serializable
data class MaintenanceLog(val enclosureId: Int) : NavKey

@Serializable
data class CreateMaintenance(val enclosureId: Int) : NavKey

@Serializable
data class FeedingSchedules(val enclosureId: Int) : NavKey

@Serializable
data class TelemetryView(val enclosureId: Int) : NavKey
