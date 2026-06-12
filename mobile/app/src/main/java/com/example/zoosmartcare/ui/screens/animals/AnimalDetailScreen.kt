package com.example.zoosmartcare.ui.screens.animals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
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
import com.example.zoosmartcare.data.model.AnimalResponse
import com.example.zoosmartcare.data.model.MedicalRecordResponse
import com.example.zoosmartcare.data.repository.AnimalRepository
import com.example.zoosmartcare.data.repository.EnclosureRepository
import com.example.zoosmartcare.data.repository.SpeciesRepository
import kotlinx.coroutines.launch

class AnimalDetailViewModel(private val animalId: Int) : ViewModel() {
    private val animalRepo = AnimalRepository()
    private val speciesRepo = SpeciesRepository()
    private val enclosureRepo = EnclosureRepository()

    var animal by mutableStateOf<AnimalResponse?>(null)
    var speciesName by mutableStateOf("")
    var enclosureName by mutableStateOf("")
    var medicalRecords by mutableStateOf<List<MedicalRecordResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadData() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            animalRepo.getById(animalId)
                .onSuccess { anim ->
                    animal = anim

                    // Get species details
                    speciesRepo.getAll().onSuccess { speciesList ->
                        val spec = speciesList.find { it.species_id == anim.species_id }
                        speciesName = spec?.common_name ?: spec?.scientific_name ?: "Species #${anim.species_id}"
                    }

                    // Get enclosure details
                    enclosureRepo.getById(anim.enclosure_id).onSuccess { enc ->
                        enclosureName = enc.name
                    }
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Failed to load animal"
                }

            animalRepo.getMedicalHistory(animalId).onSuccess { history ->
                medicalRecords = history
            }

            isLoading = false
        }
    }
}

class AnimalDetailViewModelFactory(private val animalId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnimalDetailViewModel(animalId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    animalId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AnimalDetailViewModel = viewModel(
        factory = AnimalDetailViewModelFactory(animalId)
    )

    LaunchedEffect(animalId) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.animal?.nickname ?: "Animal Details") },
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
            if (viewModel.isLoading && viewModel.animal == null) {
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
                viewModel.animal?.let { animal ->
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                    Column {
                                        Text(
                                            text = animal.nickname,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(text = "Species: ${viewModel.speciesName}", fontSize = 16.sp)
                                        Text(text = "Enclosure: ${viewModel.enclosureName}", fontSize = 16.sp)
                                        animal.birth_date?.let {
                                            Text(text = "Birth Date: $it", fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Medical History
                        item {
                            Text(
                                text = "Medical History",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (viewModel.medicalRecords.isEmpty()) {
                            item {
                                Text("No medical records registered for this animal", style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            items(viewModel.medicalRecords) { record ->
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
                                            imageVector = Icons.Default.Healing,
                                            contentDescription = null,
                                            tint = if (record.severity == "High" || record.severity == "Critical") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Column {
                                            Text(text = record.diagnosis ?: "Routine Checkup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(text = "Date: ${record.event_date}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            record.treatment_notes?.let {
                                                Text(text = "Treatment: $it", fontSize = 14.sp)
                                            }
                                            Text(
                                                text = "Severity: ${record.severity ?: "Low"}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (record.severity == "High" || record.severity == "Critical") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                            )
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
