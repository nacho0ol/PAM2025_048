package com.example.scentra.uicontroller.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.R
import com.example.scentra.modeldata.UserData
import com.example.scentra.uicontroller.viewmodel.UserDetailUiState
import com.example.scentra.uicontroller.viewmodel.UserDetailViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailUser(
    idUser: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(idUser) {
        viewModel.getUserById(idUser)
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail User") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEditDialog = true },
                containerColor = Color(0xFF1D1B20)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
            }
        }
    ) { innerPadding ->

        when (val state = viewModel.uiState) {
            is UserDetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is UserDetailUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UserDetailUiState.Success -> {
                val user = state.user

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.group_47897), // Ganti sesuai gambar
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "${user.firstname} ${user.lastname ?: ""}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "@${user.username}", fontSize = 16.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(32.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF2))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            InfoRow(label = "Role", value = user.role)
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRow(label = "User ID", value = "#${user.id}")
                        }
                    }
                }

                if (showEditDialog) {
                    EditUserDialog(
                        user = user,
                        onDismiss = { showEditDialog = false },
                        onConfirm = { fname, lname, role ->
                            viewModel.updateUser(user.id, fname, lname, role)
                            showEditDialog = false
                        }
                    )
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Hapus User?") },
                        text = { Text("Yakin ingin menghapus ${user.firstname}? Tindakan ini tidak bisa dibatalkan.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.deleteUser(user.id)
                                    showDeleteDialog = false
                                    navigateBack() // Balik ke list setelah hapus
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) { Text("Hapus") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}

@Composable
fun EditUserDialog(
    user: UserData,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var fname by remember { mutableStateOf(user.firstname) }
    var lname by remember { mutableStateOf(user.lastname ?: "") }
    var role by remember { mutableStateOf(user.role) } // Bisa ganti jadi Dropdown kalau mau

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = fname, onValueChange = { fname = it }, label = { Text("Nama Depan") })
                OutlinedTextField(value = lname, onValueChange = { lname = it }, label = { Text("Nama Belakang") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Role: ")
                    RadioButton(selected = role == "Admin", onClick = { role = "Admin" })
                    Text("Admin")
                    Spacer(Modifier.width(8.dp))
                    RadioButton(selected = role == "Staff", onClick = { role = "Staff" })
                    Text("Staff")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(fname, lname, role) }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}