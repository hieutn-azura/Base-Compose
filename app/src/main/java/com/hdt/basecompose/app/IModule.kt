package com.hdt.basecompose.app

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { SharePreference(androidContext()) }
    single { PreferenceData(get()) }
}

val viewModelModule = module {
    // Register ViewModels here:
    // viewModel { MyFeatureViewModel(get()) }
}
