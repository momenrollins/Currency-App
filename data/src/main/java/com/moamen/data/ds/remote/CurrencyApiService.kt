package com.moamen.data.ds.remote

import com.moamen.domain.entities.CurrencyModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("latest")
    suspend fun getLatestRates(@Query("access_key") apiKey: String): CurrencyModel

    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("access_key") apiKey: String,
        @Query("symbols") symbols: String
    ): CurrencyModel
}
