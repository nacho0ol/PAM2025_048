package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.UserData
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch

sealed interface UserDetailUiState {
    object Loading : UserDetailUiState
    data class Success(val user: UserData) : UserDetailUiState
    data class Error(val message: String) : UserDetailUiState
}

class UserDetailViewModel(private val repository: ScentraRepository) : ViewModel() {
    var uiState: UserDetailUiState by mutableStateOf(UserDetailUiState.Loading)
        private set

    fun getUserById(id: Int) {
        viewModelScope.launch {
            uiState = UserDetailUiState.Loading
            try {
                val user = repository.getUserById(id)
                uiState = UserDetailUiState.Success(user)
            } catch (e: Exception) {
                uiState = UserDetailUiState.Error(e.message ?: "Error")
            }
        }
    }

    // Update User
    fun updateUser(id: Int, firstname: String, lastname: String, role: String) {
        viewModelScope.launch {
            try {
                if (uiState is UserDetailUiState.Success) {
                    val currentUser = (uiState as UserDetailUiState.Success).user
                    val updatedUser = currentUser.copy(
                        firstname = firstname,
                        lastname = lastname,
                        role = role
                    )
                    repository.updateUser(id, updatedUser)
                    getUserById(id)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteUser(id)
            } catch (e: Exception) {
            }
        }
    }
}