package com.example.scentra.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)


@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData? = null
)

@Serializable
data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val username: String,
    val password: String,
    val role: String // "Admin" atau "Staff"
)

@Serializable
data class BaseResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class UserData(
    @SerialName("user_id") val id: Int,
    @SerialName("username") val username: String,
    val firstname: String,
    val lastname: String? = null,
    val role: String
)
@Serializable
data class UserResponse(
    val success: Boolean,
    val message: String,
    val data: List<UserData>
)