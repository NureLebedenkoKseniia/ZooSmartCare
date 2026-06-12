package com.example.zoosmartcare.ui.screens.enclosures

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
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
import com.example.zoosmartcare.EnclosureDetail
import com.example.zoosmartcare.EnclosureList
import com.example.zoosmartcare.QrScanner
import com.example.zoosmartcare.data.model.EnclosureResponse
import com.example.zoosmartcare.data.repository.EnclosureRepository
import com.example.zoosmartcare.ui.components.ZooBottomNavBar
import kotlinx.coroutines.launch

class EnclosureListViewModel : ViewModel() {
    private val repository = EnclosureRepository()

    var enclosures by mutableStateOf<List<EnclosureResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadEnclosures() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            repository.getAll()
                .onSuccess {
                    enclosures = it
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Failed to load enclosures"
                    isLoading = false
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnclosureListScreen(
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { EnclosureListViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadEnclosures()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enclosures", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadEnclosures() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        bottomBar = {
            ZooBottomNavBar(
                currentKey = EnclosureList,
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
            } else if (viewModel.enclosures.isEmpty()) {
                Text(
                    text = "No enclosures found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.enclosures) { enclosure ->
                        EnclosureCard(
                            enclosure = enclosure,
                            onClick = { onNavigate(EnclosureDetail(enclosure.enclosure_id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnclosureCard(
    enclosure: EnclosureResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = enclosure.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!enclosure.geo_location.isNullOrEmpty()) {
                    Text(
                        text = "Location: ${enclosure.geo_location}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
