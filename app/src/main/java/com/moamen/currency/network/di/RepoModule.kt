package com.moamen.currency.network.di

import com.moamen.data.ds.remote.CurrencyApiService
import com.moamen.data.repositories.CurrencyRepositoryImpl
import com.moamen.domain.repositories.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    fun provideJobsRepoModule(apiService: CurrencyApiService): CurrencyRepository {
        return CurrencyRepositoryImpl(apiService)
    }
}