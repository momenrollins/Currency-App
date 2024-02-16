package com.moamen.currency.repository

import com.moamen.currency.BuildConfig
import com.moamen.currency.model.CurrencyModel
import com.moamen.currency.network.CurrencyApiService
import javax.inject.Inject

open class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService
) {

    suspend fun getLatestRates(): CurrencyModel {
        return apiService.getLatestRates(BuildConfig.FIXER_API_KEY)
    }
}
