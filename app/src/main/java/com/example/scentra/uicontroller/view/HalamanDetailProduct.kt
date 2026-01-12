package com.example.scentra.ui.view.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scentra.modeldata.Produk
import com.example.scentra.uicontroller.viewmodel.DetailUiState
import com.example.scentra.uicontroller.viewmodel.DetailViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailProduct(
    idProduk: Int,
    navigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(idProduk) {
        viewModel.getProdukById(idProduk)
    }

    var showRestockDialog by remember { mutableStateOf(false) }
    var showStockOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(idProduk) }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val state = viewModel.detailUiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}")
                }
            }
            is DetailUiState.Success -> {
                DetailContent(
                    produk = state.produk,
                    onRestockClick = { showRestockDialog = true },
                    onStockOutClick = { showStockOutDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showRestockDialog) {
            StockDialog(
                title = "Restock Barang (+)",
                label = "Jumlah Masuk",
                onDismiss = { showRestockDialog = false },
                onConfirm = { jumlah ->
                    viewModel.updateStok(idProduk, jumlah, isRestock = true)
                    showRestockDialog = false
                }
            )
        }

        if (showStockOutDialog) {
            StockDialog(
                title = "Barang Keluar (-)",
                label = "Jumlah Keluar",
                onDismiss = { showStockOutDialog = false },
                onConfirm = { jumlah ->
                    viewModel.updateStok(idProduk, jumlah, isRestock = false)
                    showStockOutDialog = false
                }
            )
        }
    }
}

@Composable
fun DetailContent(
    produk: Produk,
    onRestockClick: () -> Unit,
    onStockOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = produk.imgPath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column {
            Text(
                text = produk.nama,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Rp ${produk.price}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Divider()

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stok Gudang", style = MaterialTheme.typography.titleMedium)

                    Text(
                        "${produk.stok}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onStockOutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Keluar (-)")
                    }

                    Button(
                        onClick = onRestockClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Masuk (+)")
                    }
                }
            }
        }

        Divider()

        InfoRow(label = "Variant", value = "${produk.variant} ml")
        InfoRow(label = "Top Notes", value = produk.topNotes)
        InfoRow(label = "Middle Notes", value = produk.middleNotes)
        InfoRow(label = "Base Notes", value = produk.baseNotes)

        Spacer(modifier = Modifier.height(8.dp))
        Text("Deskripsi:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(produk.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
        Text(text = value)
    }
}

@Composable
fun StockDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) textInput = it },
                label = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val jumlah = textInput.toIntOrNull() ?: 0
                    if (jumlah > 0) {
                        onConfirm(jumlah)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}