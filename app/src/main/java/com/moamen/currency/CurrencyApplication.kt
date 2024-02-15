package com.moamen.currency

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CurrencyApplication : Application() {

    companion object {
        lateinit var instance: CurrencyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}