package com.moamen.currency.network.utils

import android.content.Context
import android.net.ConnectivityManager
import com.moamen.currency.CurrencyApplication
import javax.inject.Inject

class NetworkUtils @Inject constructor() {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            CurrencyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
