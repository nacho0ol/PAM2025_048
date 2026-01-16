package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.uicontroller.view.widget.ProdukCard
import com.example.scentra.uicontroller.view.widget.ScentraBottomAppBar
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel
import com.example.scentra.viewmodel.DashboardUiState
import com.example.scentra.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDashboard(
    role: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onAddProductClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState

    Scaffold(
        containerColor = Color(0xFFFFFCF5),
        topBar = {
            ScentraTopAppBar(
                title = "Scentra",
                canNavigateBack = false,
            )
        },
        bottomBar = {
            ScentraBottomAppBar(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "profile" -> onNavigateToProfile()
                        "history" -> onNavigateToHistory()
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == "Admin") {
                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = Color(0xFF1D1B20),
                    contentColor = Color.White,
                    modifier = Modifier
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
                }
            }
        }
    ) { innerPadding ->


        Box(
            modifier = modifier.fillMaxSize()
        ) {

            when (uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DashboardUiState.Success -> {
                    if (uiState.produk.isEmpty()) {
                        Text(
                            text = "Belum ada produk. Klik + untuk tambah.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(top = innerPadding.calculateTopPadding() + 16.dp,

                                // 2. Kiri Kanan standar
                                start = 16.dp,
                                end = 16.dp,

                                // 3. Bawah standar aja (karena FAB udah diamankan sama Spacer raksasa)
                                bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.produk) { produk ->
                                ProdukCard(produk = produk, onClick = { onProductClick(produk.id) })
                            }
                            item {
                                Spacer(modifier = Modifier.height(150.dp))
                            }
                        }
                    }
                }

                is DashboardUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Gagal memuat data :(")
                        Button(onClick = { viewModel.getProducts() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}