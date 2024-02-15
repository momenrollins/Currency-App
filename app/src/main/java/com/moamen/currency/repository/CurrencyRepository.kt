package com.moamen.currency.repository

import com.moamen.currency.model.CurrencyModel
import com.moamen.currency.network.CurrencyApiService
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService
) {

    suspend fun getLatestRates(): CurrencyModel {
        return apiService.getLatestRates()
    }
}
