package com.example.zoosmartcare.ui.screens.telemetry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zoosmartcare.data.model.SensorReadingResponse
import com.example.zoosmartcare.data.repository.TelemetryRepository
import com.example.zoosmartcare.ui.screens.enclosures.TelemetryIndicator
import kotlinx.coroutines.launch

class TelemetryViewModel(private val enclosureId: Int) : ViewModel() {
    private val repository = TelemetryRepository()

    var latestReading by mutableStateOf<SensorReadingResponse?>(null)
    var history by mutableStateOf<List<SensorReadingResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadData() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val latestResult = repository.getLatest(enclosureId)
            val historyResult = repository.getHistory(enclosureId, limit = 30)

            latestResult.onSuccess {
                latestReading = it
            }

            historyResult.onSuccess { list ->
                history = list.sortedByDescending { it.timestamp }
                isLoading = false
            }
            .onFailure { error ->
                errorMessage = error.message ?: "Failed to load telemetry"
                isLoading = false
            }
        }
    }
}

class TelemetryViewModelFactory(private val enclosureId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TelemetryViewModel(enclosureId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemetryScreen(
    enclosureId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TelemetryViewModel = viewModel(
        factory = TelemetryViewModelFactory(enclosureId)
    )

    LaunchedEffect(enclosureId) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telemetry History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading && viewModel.history.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current Status Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Latest Readings",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                viewModel.latestReading?.let { reading ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        reading.temperature_val?.let { t ->
                                            TelemetryIndicator(icon = Icons.Default.Thermostat, value = "${String.format("%.1f", t)}°C", label = "Temp")
                                        }
                                        reading.humidity_val?.let { h ->
                                            TelemetryIndicator(icon = Icons.Default.WaterDrop, value = "${String.format("%.1f", h)}%", label = "Humidity")
                                        }
                                        reading.light_val?.let { l ->
                                            TelemetryIndicator(icon = Icons.Default.LightMode, value = "${String.format("%.1f", l)} lx", label = "Light")
                                        }
                                    }
                                } ?: Text("No recent telemetry", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Timeline Title
                    item {
                        Text(
                            "History Logs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (viewModel.history.isEmpty()) {
                        item {
                            Text("No telemetry history logs found", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        items(viewModel.history) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = log.timestamp,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        log.temperature_val?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Thermostat, contentDescription = null, size = 16.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(" ${String.format("%.1f", it)}°C", fontSize = 14.sp)
                                            }
                                        }
                                        log.humidity_val?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.WaterDrop, contentDescription = null, size = 16.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(" ${String.format("%.1f", it)}%", fontSize = 14.sp)
                                            }
                                        }
                                        log.light_val?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.LightMode, contentDescription = null, size = 16.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(" ${String.format("%.1f", it)} lx", fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: androidx.compose.ui.graphics.Color) {
    Icon(imageVector, contentDescription, modifier = Modifier.size(size), tint = tint)
}
