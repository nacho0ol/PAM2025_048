package com.example.scentra.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse(
    val success: Boolean,
    val message: String
)