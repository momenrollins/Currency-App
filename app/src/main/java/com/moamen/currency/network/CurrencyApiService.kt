package com.moamen.currency.network

import com.moamen.currency.model.CurrencyModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("latest")
    suspend fun getLatestRates(@Query("access_key") apiKey: String): CurrencyModel
}
