package com.hdt.basecompose.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppProjectApplication : Application() {

    var isAppForeground = false

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppProjectApplication)
            modules(dataModule, viewModelModule)
        }
    }
}
