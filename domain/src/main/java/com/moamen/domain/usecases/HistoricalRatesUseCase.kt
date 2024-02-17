package com.moamen.domain.usecases

import com.moamen.domain.BuildConfig
import com.moamen.domain.entities.CurrencyModel
import com.moamen.domain.repositories.CurrencyRepository
import javax.inject.Inject

class HistoricalRatesUseCase @Inject constructor(private val repository: CurrencyRepository) {
    suspend operator fun invoke(date: String, symbols: String): CurrencyModel {
        return repository.getHistoricalRates(date, BuildConfig.FIXER_API_KEY, symbols)
    }
}