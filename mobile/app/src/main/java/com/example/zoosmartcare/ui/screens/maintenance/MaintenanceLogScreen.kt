package com.example.zoosmartcare.ui.screens.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
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
import com.example.zoosmartcare.CreateMaintenance
import com.example.zoosmartcare.data.model.MaintenanceLogResponse
import com.example.zoosmartcare.data.repository.MaintenanceRepository
import kotlinx.coroutines.launch

class MaintenanceLogViewModel(private val enclosureId: Int) : ViewModel() {
    private val repository = MaintenanceRepository()

    var logs by mutableStateOf<List<MaintenanceLogResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadLogs() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            repository.getLogs(enclosureId)
                .onSuccess { list ->
                    logs = list.sortedByDescending { it.timestamp }
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Failed to load logs"
                    isLoading = false
                }
        }
    }
}

class MaintenanceLogViewModelFactory(private val enclosureId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MaintenanceLogViewModel(enclosureId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceLogScreen(
    enclosureId: Int,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MaintenanceLogViewModel = viewModel(
        factory = MaintenanceLogViewModelFactory(enclosureId)
    )

    LaunchedEffect(enclosureId) {
        viewModel.loadLogs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Logs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadLogs() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(CreateMaintenance(enclosureId)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading && viewModel.logs.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (viewModel.logs.isEmpty()) {
                Text(
                    text = "No maintenance logs for this enclosure",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.logs) { log ->
                        MaintenanceCard(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceCard(log: MaintenanceLogResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = log.action_type ?: "Routine Maintenance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Logged by User #${log.user_id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                log.notes?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.timestamp,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
