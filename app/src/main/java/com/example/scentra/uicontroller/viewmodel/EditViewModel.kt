package com.example.scentra.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.repositori.ScentraRepository
import com.example.scentra.uicontroller.viewmodel.InsertUiState
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EditViewModel(private val repository: ScentraRepository) : ViewModel() {

    var uiState by mutableStateOf(InsertUiState())
        private set

    fun loadProduk(id: Int) {
        viewModelScope.launch {
            try {
                val produk = repository.getProductById(id)
                uiState = InsertUiState(
                    nama = produk.nama,
                    variant = produk.variant.toString(),
                    price = produk.price.toString(),
                    currentStock = produk.stok.toString(),
                    topNotes = produk.topNotes,
                    middleNotes = produk.middleNotes,
                    baseNotes = produk.baseNotes,
                    description = produk.description,
                    imgPath = produk.imgPath
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUiState(newUiState: InsertUiState) {
        uiState = newUiState
    }

    fun updateProduk(idProduk: Int, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val namaRB = uiState.nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val variantRB = uiState.variant.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceRB = uiState.price.toRequestBody("text/plain".toMediaTypeOrNull())
                val stokRB = uiState.currentStock.toRequestBody("text/plain".toMediaTypeOrNull())
                val topRB = uiState.topNotes.toRequestBody("text/plain".toMediaTypeOrNull())
                val midRB = uiState.middleNotes.toRequestBody("text/plain".toMediaTypeOrNull())
                val baseRB = uiState.baseNotes.toRequestBody("text/plain".toMediaTypeOrNull())
                val descRB = uiState.description.toRequestBody("text/plain".toMediaTypeOrNull())

                val oldPathRB = uiState.imgPath.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                uiState.imageUri?.let { uri ->
                    val file = uriToFile(uri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                repository.updateProduct(
                    idProduk, namaRB, variantRB, priceRB, stokRB, topRB, midRB, baseRB, descRB, oldPathRB, imagePart
                )
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Gagal update: ${e.message}")
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        return tempFile
    }
}