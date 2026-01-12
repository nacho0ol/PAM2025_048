package com.example.scentra.ui.view.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.modeldata.HistoryLog
import com.example.scentra.uicontroller.viewmodel.HistoryUiState
import com.example.scentra.uicontroller.viewmodel.HistoryViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHistory(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.getHistory()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Riwayat Stok") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val state = viewModel.uiState) {
            is HistoryUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HistoryUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}")
                }
            }
            is HistoryUiState.Success -> {
                HistoryList(
                    logs = state.logs,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun HistoryList(logs: List<HistoryLog>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            HistoryCard(log)
        }
    }
}

@Composable
fun HistoryCard(log: HistoryLog) {
    val isMasuk = log.type == "Masuk"
    val warnaStatus = if (isMasuk) Color(0xFF4CAF50) else Color(0xFFE57373) // Green vs Red
    val iconTeks = if (isMasuk) "(+)" else "(-)"

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.product_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Oleh: ${log.username ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = log.date.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = warnaStatus)
            ) {
                Text(
                    text = "$iconTeks ${log.qty}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}