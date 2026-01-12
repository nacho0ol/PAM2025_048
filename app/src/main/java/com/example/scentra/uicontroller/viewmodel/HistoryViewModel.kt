package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.HistoryLog
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(val logs: List<HistoryLog>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class HistoryViewModel(private val repository: ScentraRepository) : ViewModel() {
    var uiState: HistoryUiState by mutableStateOf(HistoryUiState.Loading)
        private set

    fun getHistory() {
        viewModelScope.launch {
            uiState = HistoryUiState.Loading
            try {
                val logs = repository.getHistory()
                uiState = HistoryUiState.Success(logs)
            } catch (e: IOException) {
                uiState = HistoryUiState.Error("Gagal koneksi internet")
            } catch (e: Exception) {
                uiState = HistoryUiState.Error("Error: ${e.message}")
            }
        }
    }
}