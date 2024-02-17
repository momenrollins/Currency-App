package com.moamen.domain.usecases

import com.moamen.domain.BuildConfig
import com.moamen.domain.entities.CurrencyModel
import com.moamen.domain.repositories.CurrencyRepository
import javax.inject.Inject

class LatestRatesUseCase @Inject constructor(private val repository: CurrencyRepository) {
    suspend operator fun invoke(): CurrencyModel {
        return repository.getLatestRates(BuildConfig.FIXER_API_KEY)
    }
}