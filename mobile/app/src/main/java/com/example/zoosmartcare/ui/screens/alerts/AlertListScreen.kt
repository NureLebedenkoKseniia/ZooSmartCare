package com.example.zoosmartcare.ui.screens.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.zoosmartcare.AlertList
import com.example.zoosmartcare.QrScanner
import com.example.zoosmartcare.data.model.AlertResponse
import com.example.zoosmartcare.data.repository.AlertRepository
import com.example.zoosmartcare.ui.components.ZooBottomNavBar
import kotlinx.coroutines.launch

class AlertListViewModel : ViewModel() {
    private val alertRepo = AlertRepository()

    var alerts by mutableStateOf<List<AlertResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadAlerts() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            alertRepo.getActive()
                .onSuccess { list ->
                    // Show only Active alerts and sort by timestamp descending
                    alerts = list.filter { it.status == "Active" }.sortedByDescending { it.timestamp }
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Failed to load alerts"
                    isLoading = false
                }
        }
    }

    fun resolveAlert(id: Int) {
        viewModelScope.launch {
            alertRepo.resolve(id).onSuccess {
                loadAlerts()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertListScreen(
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { AlertListViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Alerts", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadAlerts() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        bottomBar = {
            ZooBottomNavBar(
                currentKey = AlertList,
                onTabSelected = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(QrScanner) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (viewModel.alerts.isEmpty()) {
                Text(
                    text = "No active alerts in the zoo!",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.alerts) { alert ->
                        AlertCard(
                            alert = alert,
                            onResolve = { viewModel.resolveAlert(alert.alert_id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    alert: AlertResponse,
    onResolve: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = alert.alert_type ?: "Alert",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = alert.message ?: "",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = alert.timestamp,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                    )
                }
            }

            Button(
                onClick = onResolve,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Resolve", fontSize = 12.sp)
            }
        }
    }
}
