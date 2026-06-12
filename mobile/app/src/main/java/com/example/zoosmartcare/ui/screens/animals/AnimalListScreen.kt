package com.example.zoosmartcare.ui.screens.animals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
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
import com.example.zoosmartcare.AnimalDetail
import com.example.zoosmartcare.AnimalList
import com.example.zoosmartcare.QrScanner
import com.example.zoosmartcare.data.model.AnimalResponse
import com.example.zoosmartcare.data.repository.AnimalRepository
import com.example.zoosmartcare.data.repository.SpeciesRepository
import com.example.zoosmartcare.ui.components.ZooBottomNavBar
import kotlinx.coroutines.launch

class AnimalListViewModel : ViewModel() {
    private val animalRepo = AnimalRepository()
    private val speciesRepo = SpeciesRepository()

    var animals by mutableStateOf<List<AnimalResponse>>(emptyList())
    var speciesMap by mutableStateOf<Map<Int, String>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadData() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            speciesRepo.getAll().onSuccess { list ->
                speciesMap = list.associate { it.species_id to (it.common_name ?: it.scientific_name) }
            }

            animalRepo.getAll()
                .onSuccess {
                    animals = it
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Failed to load animals"
                    isLoading = false
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { AnimalListViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Animals", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        bottomBar = {
            ZooBottomNavBar(
                currentKey = AnimalList,
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
            } else if (viewModel.animals.isEmpty()) {
                Text(
                    text = "No animals found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.animals) { animal ->
                        val speciesName = viewModel.speciesMap[animal.species_id] ?: "Species #${animal.species_id}"
                        AnimalCard(
                            animal = animal,
                            speciesName = speciesName,
                            onClick = { onNavigate(AnimalDetail(animal.animal_id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimalCard(
    animal: AnimalResponse,
    speciesName: String,
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
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = animal.nickname,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = speciesName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
