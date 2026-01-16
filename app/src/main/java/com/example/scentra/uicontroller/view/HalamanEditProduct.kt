package com.example.scentra.uicontroller.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scentra.modeldata.getFullImageUrl
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel
import com.example.scentra.viewmodel.EditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditProduct(
    idProduk: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // Launcher Galeri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.updateUiState(uiState.copy(imageUri = uri))
    }

    LaunchedEffect(idProduk) {
        viewModel.loadProduk(idProduk)
    }

    Scaffold(
        topBar = {
            ScentraTopAppBar(
                title = "Edit Produk",
                canNavigateBack = true,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding(), // Biar keyboard gak nutupin
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.imageUri != null) {
                        AsyncImage(
                            model = uiState.imageUri,
                            contentDescription = "New Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = getFullImageUrl(uiState.imgPath),
                            contentDescription = "Old Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ganti Foto", modifier = Modifier.size(20.dp))
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.nama,
                    onValueChange = { viewModel.updateUiState(uiState.copy(nama = it)) },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.variant,
                    onValueChange = { viewModel.updateUiState(uiState.copy(variant = it)) },
                    label = { Text("Varian (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = { viewModel.updateUiState(uiState.copy(price = it)) },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- 5. STOK ---
            item {
                OutlinedTextField(
                    value = uiState.currentStock,
                    onValueChange = { viewModel.updateUiState(uiState.copy(currentStock = it)) },
                    label = { Text("Stok") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- 6. NOTES PARFUM ---
            item {
                DynamicNotesInput(
                    label = "Top Notes",
                    currentValue = uiState.topNotes,
                    onValueChange = { viewModel.updateUiState(uiState.copy(topNotes = it)) }
                )
            }

            item {
                DynamicNotesInput(
                    label = "Middle Notes",
                    currentValue = uiState.middleNotes,
                    onValueChange = { viewModel.updateUiState(uiState.copy(middleNotes = it)) }
                )
            }

            item {
                DynamicNotesInput(
                    label = "Base Notes",
                    currentValue = uiState.baseNotes,
                    onValueChange = { viewModel.updateUiState(uiState.copy(baseNotes = it)) }
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateUiState(uiState.copy(description = it)) },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateProduk(idProduk, context) { onNavigateBack() }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Update Produk")
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}