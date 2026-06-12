package com.example.zoosmartcare.ui.screens.enclosures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation3.runtime.NavKey
import com.example.zoosmartcare.AnimalDetail
import com.example.zoosmartcare.*
import com.example.zoosmartcare.data.model.AnimalResponse
import com.example.zoosmartcare.data.model.EnclosureResponse
import com.example.zoosmartcare.data.model.SensorReadingResponse
import com.example.zoosmartcare.data.repository.AnimalRepository
import com.example.zoosmartcare.data.repository.EnclosureRepository
import com.example.zoosmartcare.data.repository.TelemetryRepository
import kotlinx.coroutines.launch

class EnclosureDetailViewModel(private val enclosureId: Int) : ViewModel() {
    private val enclosureRepo = EnclosureRepository()
    private val telemetryRepo = TelemetryRepository()
    private val animalRepo = AnimalRepository()

    var enclosure by mutableStateOf<EnclosureResponse?>(null)
    var telemetry by mutableStateOf<SensorReadingResponse?>(null)
    var animals by mutableStateOf<List<AnimalResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadData() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val encResult = enclosureRepo.getById(enclosureId)
            val telemetryResult = telemetryRepo.getLatest(enclosureId)
            val animalsResult = animalRepo.getAll(enclosureId = enclosureId)

            encResult.onSuccess {
                enclosure = it
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to load enclosure"
            }

            telemetryResult.onSuccess {
                telemetry = it
            }

            animalsResult.onSuccess {
                animals = it
            }

            isLoading = false
        }
    }
}

class EnclosureDetailViewModelFactory(private val enclosureId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EnclosureDetailViewModel(enclosureId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnclosureDetailScreen(
    enclosureId: Int,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EnclosureDetailViewModel = viewModel(
        factory = EnclosureDetailViewModelFactory(enclosureId)
    )

    LaunchedEffect(enclosureId) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.enclosure?.name ?: "Enclosure Details") },
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
            if (viewModel.isLoading && viewModel.enclosure == null) {
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
                viewModel.enclosure?.let { enclosure ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Enclosure Info", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Name: ${enclosure.name}", fontSize = 16.sp)
                                    enclosure.geo_location?.let {
                                        Text("Coordinates: $it", fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        // Telemetry Summary Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Current Telemetry", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        IconButton(onClick = { onNavigate(TelemetryView(enclosureId)) }) {
                                            Icon(Icons.Default.Timeline, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    viewModel.telemetry?.let { t ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            t.temperature_val?.let { temp ->
                                                TelemetryIndicator(icon = Icons.Default.Thermostat, value = "${String.format("%.1f", temp)}°C", label = "Temp")
                                            }
                                            t.humidity_val?.let { hum ->
                                                TelemetryIndicator(icon = Icons.Default.WaterDrop, value = "${String.format("%.1f", hum)}%", label = "Humidity")
                                            }
                                            t.light_val?.let { light ->
                                                TelemetryIndicator(icon = Icons.Default.LightMode, value = "${String.format("%.1f", light)} lx", label = "Light")
                                            }
                                        }
                                    } ?: Text("No telemetry data available", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        // Quick Navigation Grid
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { onNavigate(FeedingSchedules(enclosureId)) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Feeding", fontSize = 14.sp)
                                    }
                                    Button(
                                        onClick = { onNavigate(MaintenanceLog(enclosureId)) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Maintenance", fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        // Animals List
                        item {
                            Text("Resident Animals", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        }

                        if (viewModel.animals.isEmpty()) {
                            item {
                                Text("No animals in this enclosure", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        } else {
                            items(viewModel.animals) { animal ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigate(AnimalDetail(animal.animal_id)) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(animal.nickname, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                animal.birth_date?.let {
                                                    Text("Born: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = null)
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
fun TelemetryIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
    }
}
