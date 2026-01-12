package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.CurrentUser
import com.example.scentra.modeldata.Produk
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val produk: Produk) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(private val repository: ScentraRepository) : ViewModel() {
    var detailUiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    fun getProdukById(id: Int) {
        viewModelScope.launch {
            detailUiState = DetailUiState.Loading
            try {
                val result = repository.getProductById(id)
                detailUiState = DetailUiState.Success(result)
            } catch (e: IOException) {
                detailUiState = DetailUiState.Error("Gagal memuat data: Cek koneksi internet")
            } catch (e: Exception) {
                detailUiState = DetailUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun deleteProduk(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(id)
            } catch (e: Exception) {
                detailUiState = DetailUiState.Error("Gagal menghapus: ${e.message}")
            }
        }
    }

    fun updateStok(idProduk: Int, qty: Int, isRestock: Boolean) {
        viewModelScope.launch {
            try {
                val userIdLogin = if (CurrentUser.id != 0) CurrentUser.id else 1

                if (isRestock) {
                    repository.restockProduct(idProduk, qty)
                } else {
                    repository.stockOutProduct(idProduk, qty, reason = "Sales")
                }

                getProdukById(idProduk)

            } catch (e: Exception) {
                detailUiState = DetailUiState.Error("Gagal Update Stok: ${e.message}")
            }
        }
    }
}