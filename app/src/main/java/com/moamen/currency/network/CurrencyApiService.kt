package com.moamen.currency.network

import com.moamen.currency.model.CurrencyModel
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("latest")
    suspend fun getLatestRates(): CurrencyModel
}
