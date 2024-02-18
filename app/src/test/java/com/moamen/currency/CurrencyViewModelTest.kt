package com.moamen.currency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.moamen.currency.util.UiState
import com.moamen.currency.viewmodels.CurrencyViewModel
import com.moamen.data.repositories.FakeCurrencyRepositoryImpl
import com.moamen.domain.repositories.CurrencyRepository
import com.moamen.domain.usecases.HistoricalRatesUseCase
import com.moamen.domain.usecases.LatestRatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CurrencyViewModel
    private lateinit var repository: CurrencyRepository
    private lateinit var latestRatesUseCase: LatestRatesUseCase
    private lateinit var historicalRatesUseCase: HistoricalRatesUseCase
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        repository = FakeCurrencyRepositoryImpl()
        latestRatesUseCase = LatestRatesUseCase(repository)
        historicalRatesUseCase = HistoricalRatesUseCase(repository)
        viewModel = CurrencyViewModel(latestRatesUseCase, historicalRatesUseCase)
    }

    @Test
    fun `test fetchLatestRates success`() = testDispatcher.runBlockingTest {
        viewModel.fetchLatestRates()
        // Verify that latestRatesState is set to UiState.Success
        assertTrue(viewModel.latestRatesState.value is UiState.Success)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher after the test
    }
}
