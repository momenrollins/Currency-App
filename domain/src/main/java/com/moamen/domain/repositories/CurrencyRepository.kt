package com.moamen.domain.repositories

import com.moamen.domain.entities.CurrencyModel

interface CurrencyRepository {
    suspend fun getLatestRates(apiKey: String): CurrencyModel
    suspend fun getHistoricalRates(date: String, symbols: String, apiKey: String): CurrencyModel
}