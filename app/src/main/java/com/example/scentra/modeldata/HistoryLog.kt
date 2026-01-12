package com.example.scentra.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class HistoryLog(
    val type: String,
    val product_name: String,
    val username: String?,
    val qty: Int,
    val date: String
)

@Serializable
data class HistoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<HistoryLog>
)