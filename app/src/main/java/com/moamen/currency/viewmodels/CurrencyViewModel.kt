package com.moamen.currency.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moamen.currency.BuildConfig
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

    private val _historyState = MutableLiveData<UiState<List<CurrencyModel>>>()
    val historyState: LiveData<UiState<List<CurrencyModel>>> get() = _historyState

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
                    } else {
                        _latestRatesState.value = UiState.Error("Failed to fetch latest rates")
                    }
                } catch (e: Exception) {
                    _latestRatesState.value = UiState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }

    fun fetchHistoricalData(symbols: String) {
        if (!NetworkUtils.isNetworkAvailable())
            _historyState.value = UiState.Error("Check your network and try again!")
        else {
            _historyState.value = UiState.Loading
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
                        _historyState.value = UiState.Success(resultList)
                    } else {
                        _historyState.value = UiState.Error("Failed to fetch historical data")
                    }
                } catch (e: Exception) {
                    _historyState.value = UiState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }

}