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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _latestRatesState = MutableLiveData<UiState<CurrencyModel>>()
    val latestRatesState: LiveData<UiState<CurrencyModel>> get() = _latestRatesState

    fun fetchLatestRates() {

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