package com.moamen.currency.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moamen.currency.BuildConfig
import com.moamen.domain.entities.ConvertedHistoryModel
import com.moamen.domain.entities.CurrencyModel
import com.moamen.currency.network.utils.NetworkUtils
import com.moamen.currency.repository.MockCurrencyApiService
import com.moamen.currency.util.UiState
import com.moamen.domain.usecases.HistoricalRatesUseCase
import com.moamen.domain.usecases.LatestRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val latestRatesUseCase: LatestRatesUseCase,
    private val historicalRatesUseCase: HistoricalRatesUseCase
) : ViewModel() {

    private val _latestRatesState = MutableLiveData<UiState<CurrencyModel>>()
    val latestRatesState: LiveData<UiState<CurrencyModel>> get() = _latestRatesState

    private val _historyState = MutableLiveData<UiState<List<ConvertedHistoryModel>>>()
    val historyState: LiveData<UiState<List<ConvertedHistoryModel>>> get() = _historyState

    var latestRates: CurrencyModel? = null
    fun fetchLatestRates() {
        viewModelScope.launch {
            _latestRatesState.value = UiState.Loading
            try {
                val response = latestRatesUseCase.invoke()
                if (response.success) {
                    _latestRatesState.value = UiState.Success(response)
                    latestRates = response
                } else {
                    _latestRatesState.value = UiState.Error("Failed to fetch latest rates")
                }
            } catch (e: Exception) {
                _latestRatesState.value = UiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun getConvertList(
        symbols: String,
        currencyModels: List<CurrencyModel>
    ): List<ConvertedHistoryModel> {
        return currencyModels.map { model ->
            val fromCurrency = symbols.split(",").first()
            val toCurrency = symbols.split(",").last()
            ConvertedHistoryModel(
                model.date,
                1.0,
                fromCurrency,
                calculateValue(
                    model.rates[fromCurrency] ?: 1.0,
                    model.rates[toCurrency] ?: 1.0
                ),
                toCurrency
            )
        }
    }

    fun fetchHistoricalData(symbols: String) {
        if (!NetworkUtils.isNetworkAvailable())
            _historyState.value = UiState.Error("Check your network and try again!")
        else {
            _historyState.value = UiState.Loading
            var convertedList: List<ConvertedHistoryModel>
            viewModelScope.launch {
                try {
                    val currentDate = LocalDate.now()
                    val dateFormats = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val deferredResults = mutableListOf<Deferred<CurrencyModel>>()

                    for (i in 1..3) {
                        val date = currentDate.minusDays(i.toLong()).format(dateFormats)
                        val deferred = async { historicalRatesUseCase.invoke(date, symbols) }
                        deferredResults.add(deferred)
                    }

                    val resultList = deferredResults.awaitAll()

                    if (resultList.all { it.success }) {
                        convertedList = getConvertList(symbols, resultList)
                        _historyState.value = UiState.Success(convertedList)
                    } else {
                        _historyState.value = UiState.Error("Failed to fetch historical data")
                    }
                } catch (e: Exception) {
                    _historyState.value = UiState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }

    private fun calculateValue(
        fromRate: Double,
        toRate: Double
    ): Double {
        val conversionRate = toRate.div(fromRate)
        return String.format("%.2f", conversionRate).toDouble()
    }

}