package com.example.zoosmartcare

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.zoosmartcare.data.network.NetworkModule
import com.example.zoosmartcare.ui.screens.alerts.AlertListScreen
import com.example.zoosmartcare.ui.screens.animals.AnimalDetailScreen
import com.example.zoosmartcare.ui.screens.animals.AnimalListScreen
import com.example.zoosmartcare.ui.screens.dashboard.DashboardScreen
import com.example.zoosmartcare.ui.screens.enclosures.EnclosureDetailScreen
import com.example.zoosmartcare.ui.screens.enclosures.EnclosureListScreen
import com.example.zoosmartcare.ui.screens.feeding.FeedingScreen
import com.example.zoosmartcare.ui.screens.login.LoginScreen
import com.example.zoosmartcare.ui.screens.maintenance.CreateMaintenanceScreen
import com.example.zoosmartcare.ui.screens.maintenance.MaintenanceLogScreen
import com.example.zoosmartcare.ui.screens.qrscanner.QrScannerScreen
import com.example.zoosmartcare.ui.screens.telemetry.TelemetryScreen

@Composable
fun MainNavigation() {
    val startDest = if (!NetworkModule.tokenManager.getToken().isNullOrEmpty()) Dashboard else Login
    val backStack = rememberNavBackStack(startDest)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Login> {
                LoginScreen(
                    onLoginSuccess = {
                        backStack.add(Dashboard)
                    },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<Dashboard> {
                DashboardScreen(
                    onNavigate = { key -> backStack.add(key) },
                    onLogout = { backStack.add(Login) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<EnclosureList> {
                EnclosureListScreen(
                    onNavigate = { key -> backStack.add(key) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<EnclosureDetail> { key ->
                EnclosureDetailScreen(
                    enclosureId = key.enclosureId,
                    onNavigate = { k -> backStack.add(k) },
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<AnimalList> {
                AnimalListScreen(
                    onNavigate = { key -> backStack.add(key) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<AnimalDetail> { key ->
                AnimalDetailScreen(
                    animalId = key.animalId,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<AlertList> {
                AlertListScreen(
                    onNavigate = { key -> backStack.add(key) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<QrScanner> {
                QrScannerScreen(
                    onNavigate = { key -> backStack.add(key) },
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<MaintenanceLog> { key ->
                MaintenanceLogScreen(
                    enclosureId = key.enclosureId,
                    onNavigate = { k -> backStack.add(k) },
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<CreateMaintenance> { key ->
                CreateMaintenanceScreen(
                    enclosureId = key.enclosureId,
                    onSuccess = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<FeedingSchedules> { key ->
                FeedingScreen(
                    enclosureId = key.enclosureId,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<TelemetryView> { key ->
                TelemetryScreen(
                    enclosureId = key.enclosureId,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        }
    )
}
