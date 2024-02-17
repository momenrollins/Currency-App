package com.moamen.currency.model

data class ConvertedHistoryModel(
    val date: String,
    val fromValue: Double,
    val fromCurrency: String,
    val toValue: Double,
    val toCurrency: String
)
