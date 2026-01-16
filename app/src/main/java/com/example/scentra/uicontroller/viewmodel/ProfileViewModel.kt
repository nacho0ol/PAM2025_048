package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.CurrentUser
import com.example.scentra.modeldata.UserData
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val users: List<UserData>) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(private val repository: ScentraRepository) : ViewModel() {

    var profileUiState: ProfileUiState by mutableStateOf(ProfileUiState.Loading)
        private set

    val currentUser = CurrentUser

    fun loadUsers() {
        viewModelScope.launch {
            profileUiState = ProfileUiState.Loading
            try {
                val listUsers = repository.getAllUsers()
                profileUiState = ProfileUiState.Success(listUsers)
            } catch (e: Exception) {
                profileUiState = ProfileUiState.Error("Gagal memuat user: ${e.message}")
            }
        }
    }

    fun logout() {
        CurrentUser.id = 0
        CurrentUser.username = ""
        CurrentUser.role = ""
    }
}