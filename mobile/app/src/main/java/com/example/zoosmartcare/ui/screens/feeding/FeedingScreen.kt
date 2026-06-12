package com.example.zoosmartcare.ui.screens.feeding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
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
import com.example.zoosmartcare.data.model.FeedingScheduleResponse
import com.example.zoosmartcare.data.repository.FeedingRepository
import kotlinx.coroutines.launch

class FeedingViewModel(private val enclosureId: Int) : ViewModel() {
    private val repository = FeedingRepository()

    var schedules by mutableStateOf<List<FeedingScheduleResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadSchedules() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            repository.getSchedules(enclosureId)
                .onSuccess { list ->
                    schedules = list
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Failed to load schedules"
                    isLoading = false
                }
        }
    }
}

class FeedingViewModelFactory(private val enclosureId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FeedingViewModel(enclosureId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingScreen(
    enclosureId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FeedingViewModel = viewModel(
        factory = FeedingViewModelFactory(enclosureId)
    )

    LaunchedEffect(enclosureId) {
        viewModel.loadSchedules()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feeding Schedules") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadSchedules() }) {
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
            if (viewModel.isLoading && viewModel.schedules.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (viewModel.schedules.isEmpty()) {
                Text(
                    text = "No feeding schedules configured for this enclosure",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.schedules) { schedule ->
                        FeedingCard(schedule = schedule)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedingCard(schedule: FeedingScheduleResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = schedule.food_type ?: "Food Type Not Specified",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Time: ${schedule.feed_time}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                schedule.portion_size?.let { size ->
                    Text(text = "Portion Size: $size kg", fontSize = 14.sp)
                }
                schedule.days_of_week?.let { days ->
                    Text(
                        text = "Days: $days",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
