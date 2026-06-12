package com.example.zoosmartcare.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.zoosmartcare.Dashboard
import com.example.zoosmartcare.QrScanner
import com.example.zoosmartcare.data.repository.AlertRepository
import com.example.zoosmartcare.data.repository.AnimalRepository
import com.example.zoosmartcare.data.repository.AuthRepository
import com.example.zoosmartcare.data.repository.EnclosureRepository
import com.example.zoosmartcare.ui.components.ZooBottomNavBar
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val alertRepo = AlertRepository()
    private val enclosureRepo = EnclosureRepository()
    private val animalRepo = AnimalRepository()
    private val authRepo = AuthRepository()

    var activeAlertsCount by mutableStateOf(0)
    var enclosuresCount by mutableStateOf(0)
    var animalsCount by mutableStateOf(0)
    var userName by mutableStateOf("")
    var userRole by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadData() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val alertsResult = alertRepo.getActive()
            val enclosuresResult = enclosureRepo.getAll()
            val animalsResult = animalRepo.getAll()
            val userResult = authRepo.getCurrentUser()

            alertsResult.onSuccess { activeAlertsCount = it.filter { alert -> alert.status == "Active" }.size }
            enclosuresResult.onSuccess { enclosuresCount = it.size }
            animalsResult.onSuccess { animalsCount = it.size }
            userResult.onSuccess {
                userName = it.full_name
                userRole = it.role
            }.onFailure {
                // If token expired, we fail silently or let login screen handle it
            }

            isLoading = false
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (NavKey) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { DashboardViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ZooSmartCare", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            ZooBottomNavBar(
                currentKey = Dashboard,
                onTabSelected = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(QrScanner) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Greeting Card
            if (viewModel.userName.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Welcome back,",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = viewModel.userName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Role: ${viewModel.userRole}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = "System Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Statistics Grid (as vertical column containing rows)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    title = "Active Alerts",
                    value = viewModel.activeAlertsCount.toString(),
                    icon = Icons.Default.Notifications,
                    backgroundColor = if (viewModel.activeAlertsCount > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                    iconColor = if (viewModel.activeAlertsCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DashboardStatCard(
                            title = "Enclosures",
                            value = viewModel.enclosuresCount.toString(),
                            icon = Icons.Default.Place,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DashboardStatCard(
                            title = "Animals",
                            value = viewModel.animalsCount.toString(),
                            icon = Icons.Default.Pets,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
