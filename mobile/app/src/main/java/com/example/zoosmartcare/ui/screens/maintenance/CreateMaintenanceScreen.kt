package com.example.zoosmartcare.ui.screens.maintenance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
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
import com.example.zoosmartcare.data.repository.AuthRepository
import com.example.zoosmartcare.data.repository.MaintenanceRepository
import kotlinx.coroutines.launch

class CreateMaintenanceViewModel(private val enclosureId: Int) : ViewModel() {
    private val repository = MaintenanceRepository()
    private val authRepo = AuthRepository()

    var actionType by mutableStateOf("Routine Maintenance")
    var notes by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    val actionTypes = listOf(
        "Routine Maintenance",
        "Cleaning",
        "Equipment Repair",
        "Equipment Failure",
        "Feed Leftover",
        "Health Observation"
    )

    fun createLog(onSuccess: () -> Unit) {
        if (notes.isBlank()) {
            errorMessage = "Please enter some notes about the maintenance action"
            return
        }

        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepo.getCurrentUser()
                .onSuccess { user ->
                    repository.create(user.user_id, enclosureId, actionType, notes)
                        .onSuccess {
                            isLoading = false
                            onSuccess()
                        }
                        .onFailure { error ->
                            isLoading = false
                            errorMessage = error.message ?: "Failed to log maintenance"
                        }
                }
                .onFailure {
                    isLoading = false
                    errorMessage = "Failed to determine current logged-in user"
                }
        }
    }
}

class CreateMaintenanceViewModelFactory(private val enclosureId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateMaintenanceViewModel(enclosureId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMaintenanceScreen(
    enclosureId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CreateMaintenanceViewModel = viewModel(
        factory = CreateMaintenanceViewModelFactory(enclosureId)
    )

    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Maintenance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Maintenance Log",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )

            // Custom Dropdown for Action Type
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = viewModel.actionType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Action Type") },
                    trailingIcon = {
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    viewModel.actionTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.actionType = type
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Notes field
            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = { viewModel.notes = it },
                label = { Text("Notes / Observations") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createLog(onSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Log", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
