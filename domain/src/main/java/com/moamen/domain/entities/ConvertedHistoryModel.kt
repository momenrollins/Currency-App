package com.moamen.domain.entities

data class ConvertedHistoryModel(
    val date: String,
    val fromValue: Double,
    val fromCurrency: String,
    val toValue: Double,
    val toCurrency: String
)
