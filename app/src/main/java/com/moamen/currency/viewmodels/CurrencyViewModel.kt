package com.moamen.currency.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moamen.currency.BuildConfig
import com.moamen.currency.model.ConvertedHistoryModel
import com.moamen.currency.model.CurrencyModel
import com.moamen.currency.network.utils.NetworkUtils
import com.moamen.currency.repository.CurrencyRepository
import com.moamen.currency.repository.MockCurrencyApiService
import com.moamen.currency.util.UiState
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
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _latestRatesState = MutableLiveData<UiState<CurrencyModel>>()
    val latestRatesState: LiveData<UiState<CurrencyModel>> get() = _latestRatesState

    private val _historyState = MutableLiveData<UiState<List<ConvertedHistoryModel>>>()
    val historyState: LiveData<UiState<List<ConvertedHistoryModel>>> get() = _historyState

    var latestRates: CurrencyModel? = null
    fun fetchLatestRates() {
        if (!NetworkUtils.isNetworkAvailable())
            _latestRatesState.value = UiState.Error("Check your network and try again!")
        else {
            _latestRatesState.value = UiState.Loading
            viewModelScope.launch {
                try {
                    val response = if (BuildConfig.DEBUG)
                        MockCurrencyApiService.getLatestRates("")
                    else repository.getLatestRates()
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
            if (BuildConfig.DEBUG) {
                convertedList = getConvertList(symbols, currencyModels)
                _historyState.value = UiState.Success(convertedList)
            } else
                viewModelScope.launch {
                    try {
                        val currentDate = LocalDate.now()
                        val dateFormats = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val deferredResults = mutableListOf<Deferred<CurrencyModel>>()

                        for (i in 1..3) {
                            val date = currentDate.minusDays(i.toLong()).format(dateFormats)
                            val deferred = async { repository.getHistoricalRates(date, symbols) }
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

    private val currencyModels = listOf(
        CurrencyModel(
            success = true,
            timestamp = 1708127999,
            base = "EUR",
            date = "2024-02-16",
            rates = mapOf("AED" to 3.958657, "AFN" to 78.676872)
        ),
        CurrencyModel(
            success = true,
            timestamp = 1708041599,
            base = "EUR",
            date = "2024-02-15",
            rates = mapOf("AED" to 3.956947, "AFN" to 78.104203)
        ),
        CurrencyModel(
            success = true,
            timestamp = 1707955199,
            base = "EUR",
            date = "2024-02-14",
            rates = mapOf("AED" to 3.941668, "AFN" to 78.342024)
        )
    )

}