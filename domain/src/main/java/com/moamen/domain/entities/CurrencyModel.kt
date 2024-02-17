package com.moamen.domain.entities

data class CurrencyModel(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)