package com.moamen.currency.repository

import com.moamen.currency.model.CurrencyModel
import com.moamen.currency.network.CurrencyApiService

object MockCurrencyApiService : CurrencyApiService {
    override suspend fun getLatestRates(apiKey: String): CurrencyModel {
        return CurrencyModel(
            success = true,
            timestamp = 1708100103,
            base = "EUR",
            date = "2024-02-16",
            rates = mapOf(
                "AED" to 3.956629,
                "AFN" to 78.635587,
                "ALL" to 103.761542,
                "AMD" to 437.240252,
                "ANG" to 1.942235,
                "AOA" to 892.215867,
                "ARS" to 899.518215,
                "AUD" to 1.649672,
                "AWG" to 1.938965,
                "ZWL" to 346.858863
            )
        )
    }

    override suspend fun getHistoricalRates(
        date: String,
        apiKey: String,
        symbols: String
    ): CurrencyModel {
        TODO("Not yet implemented")
    }
}
