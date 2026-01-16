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
import java.io.IOException
import retrofit2.HttpException
import org.json.JSONObject


sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: UserData) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val repository: ScentraRepository) : ViewModel() {

    var loginState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    // Data Inputan
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    var isUsernameError by mutableStateOf(false)
    var isPasswordError by mutableStateOf(false)

    fun onUsernameChange(newVal: String) {
        username = newVal
        isUsernameError = false
    }

    fun onPasswordChange(newVal: String) {
        password = newVal
        isPasswordError = false
    }

    fun onLoginClick() {
        var hasError = false

        if (username.isBlank()) {
            loginState = LoginUiState.Error("Username wajib diisi!")
            isUsernameError = true
            hasError = true
        }

        if (password.isBlank()) {
            if (!hasError) loginState = LoginUiState.Error("Password wajib diisi!")
            isPasswordError = true
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            loginState = LoginUiState.Loading

            try {
                val response = repository.login(username, password)

                if (response.success && response.data != null) {

                    CurrentUser.id = response.data.id
                    CurrentUser.username = response.data.username
                    CurrentUser.role = response.data.role

                    loginState = LoginUiState.Success(response.data)
                } else {
                    loginState = LoginUiState.Error(response.message)
                }

            } catch (e: HttpException) {
                val errorMessage = try {
                    val errorBody = e.response()?.errorBody()?.string()

                    if (!errorBody.isNullOrEmpty()) {
                        val jsonObject = JSONObject(errorBody)
                        jsonObject.getString("message")
                    } else {
                        "Login Gagal (${e.code()})"
                    }
                } catch (eParsing: Exception) {
                    eParsing.printStackTrace()
                    "Terjadi kesalahan: ${e.code()}"
                }
                loginState = LoginUiState.Error(errorMessage)
            } catch (e: IOException) {
                loginState = LoginUiState.Error("Gagal koneksi server. Cek internetmu.")
            } catch (e: Exception) {
                loginState = LoginUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun resetState() {
        loginState = LoginUiState.Idle
        username = ""
        password = ""
        isUsernameError = false
        isPasswordError = false
    }
}