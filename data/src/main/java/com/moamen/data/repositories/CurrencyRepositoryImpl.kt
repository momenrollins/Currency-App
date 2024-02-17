package com.moamen.data.repositories

import com.moamen.data.ds.remote.CurrencyApiService
import com.moamen.domain.entities.CurrencyModel
import com.moamen.domain.repositories.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService
) : CurrencyRepository {

    override suspend fun getLatestRates(apiKey: String): CurrencyModel {
        return apiService.getLatestRates(apiKey)
    }

    override suspend fun getHistoricalRates(
        date: String,
        symbols: String,
        apiKey: String
    ): CurrencyModel {
        return apiService.getHistoricalRates(date, symbols, apiKey)
    }
}