package com.example.scentra.repositori

import com.example.scentra.apiservice.ScentraApiService
import com.example.scentra.modeldata.AddProdukResponse
import com.example.scentra.modeldata.BaseResponse
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.modeldata.CurrentUser
import com.example.scentra.modeldata.HistoryLog
import com.example.scentra.modeldata.LoginRequest
import com.example.scentra.modeldata.LoginResponse
import com.example.scentra.modeldata.Produk
import com.example.scentra.modeldata.ProdukResponse
import com.example.scentra.modeldata.RegisterRequest
import com.example.scentra.modeldata.StokRequest
import com.example.scentra.modeldata.UserData

interface ScentraRepository {

    suspend fun login(username: String, password: String): LoginResponse
    suspend fun register(request: RegisterRequest): BaseResponse

    suspend fun getProducts(): ProdukResponse

    suspend fun insertProduk(produk: CreateProdukRequest): AddProdukResponse

    suspend fun getProductById(id: Int): Produk

    suspend fun deleteProduct(id: Int)

    suspend fun updateProduct(id: Int, produk: CreateProdukRequest)

    suspend fun restockProduct(productId: Int, qty: Int)

    suspend fun stockOutProduct(productId: Int, qty: Int, reason: String)

    suspend fun getHistory(): List<HistoryLog>

    suspend fun getAllUsers(): List<UserData>

    suspend fun getUserById(id: Int): UserData
    suspend fun updateUser(id: Int, user: UserData)
    suspend fun deleteUser(id: Int)
}

class NetworkScentraRepository(
    private val apiService: ScentraApiService
) : ScentraRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(username, password))
    }

    override suspend fun register(request: RegisterRequest): BaseResponse {
        return apiService.register(request)
    }

    override suspend fun getProducts(): ProdukResponse {
        return apiService.getProducts()
    }
    override suspend fun insertProduk(produk: CreateProdukRequest): AddProdukResponse {
        return apiService.insertProduk(produk)
    }

    override suspend fun getProductById(id: Int): Produk {
        return apiService.getProductById(id).data
    }


    override suspend fun deleteProduct(id: Int) {
        try {
            val response = apiService.deleteProduct(id)
            if (!response.isSuccessful) {
                throw Exception("Gagal delete: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateProduct(id: Int, produk: CreateProdukRequest) {
        try {
            val response = apiService.updateProduct(id, produk)
            if (!response.isSuccessful) {
                throw Exception("Gagal update produk: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun restockProduct(productId: Int, qty: Int) {
        val userIdYangLogin = CurrentUser.id
        val finalId = if (userIdYangLogin == 0) 1 else userIdYangLogin
        val request = StokRequest(productId = productId, userId = 1, qty = qty)

        val response = apiService.restockProduct(request)
        if (!response.isSuccessful) throw Exception("Gagal Restock: ${response.code()}")
    }

    override suspend fun stockOutProduct(productId: Int, qty: Int, reason: String) {
        val userIdYangLogin = CurrentUser.id
        val finalId = if (userIdYangLogin == 0) 1 else userIdYangLogin
        val request = StokRequest(productId = productId, userId = 1, qty = qty, reason = reason)
        val response = apiService.stockOutProduct(request)
        if (!response.isSuccessful) throw Exception("Gagal Stock Out: ${response.code()}")
    }

    override suspend fun getHistory(): List<HistoryLog> {
        val response = apiService.getHistoryLog()
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun getAllUsers(): List<UserData> {
        val response = apiService.getAllUsers()
        if (response.success) {
            return response.data
        } else {
            throw Exception("Gagal ambil data user")
        }
    }

    override suspend fun getUserById(id: Int): UserData {
        val response = apiService.getUserById(id)
        return response.data ?: throw Exception("Data kosong")
    }

    override suspend fun updateUser(id: Int, user: UserData) {
        val response = apiService.updateUser(id, user)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun deleteUser(id: Int) {
        val response = apiService.deleteUser(id)
        if (!response.success) throw Exception(response.message)
    }

}