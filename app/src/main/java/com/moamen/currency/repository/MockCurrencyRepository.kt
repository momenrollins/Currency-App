package com.moamen.currency.repository

import com.moamen.data.ds.remote.CurrencyApiService
import com.moamen.domain.entities.CurrencyModel

object MockCurrencyApiService : CurrencyApiService {
    override suspend fun getLatestRates(apiKey: String): CurrencyModel {
        return CurrencyModel(
            success = true,
            timestamp = 1708197963,
            base = "EUR",
            date = "2024-02-17",
            rates = mapOf(
                "BTC" to 2.095273e-5,
                "DOP" to 63.16092,
                "DZD" to 145.04414,
                "EGP" to 33.172306,
                "ERN" to 16.166399,
                "ETB" to 61.033967,
                "EUR" to 1.0,
                "UGX" to 4176.162177,
                "USD" to 1.07776,
                "ZWL" to 347.038257
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
