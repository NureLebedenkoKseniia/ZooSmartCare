package com.example.zoosmartcare.ui.screens.qrscanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.zoosmartcare.EnclosureDetail
import com.example.zoosmartcare.data.repository.EnclosureRepository
import kotlinx.coroutines.launch

class QrScannerViewModel : ViewModel() {
    private val enclosureRepo = EnclosureRepository()

    var qrCodeInput by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun findEnclosure(onSuccess: (Int) -> Unit) {
        if (qrCodeInput.isBlank()) {
            errorMessage = "Please enter or scan a QR code"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            enclosureRepo.getByQr(qrCodeInput)
                .onSuccess { enclosure ->
                    isLoading = false
                    onSuccess(enclosure.enclosure_id)
                }
                .onFailure { error ->
                    isLoading = false
                    errorMessage = error.message ?: "Enclosure not found for this QR code"
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { QrScannerViewModel() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Code Scanner", fontWeight = FontWeight.Bold) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Scan QR Code on Enclosure",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Scanner Area Mockup
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        text = "[ Camera Viewfinder ]",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Text(
                text = "Or enter the QR code value manually:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = viewModel.qrCodeInput,
                onValueChange = { viewModel.qrCodeInput = it },
                label = { Text("QR Code String") },
                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    viewModel.findEnclosure { enclosureId ->
                        onNavigate(EnclosureDetail(enclosureId))
                    }
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
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Find Enclosure", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
