package com.hdt.basecompose.app

import com.ads.control.admob.Admob
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AzAds
import com.ads.control.application.AdsMultiDexApplication
import com.ads.control.config.AdjustConfig
import com.ads.control.config.AppsflyerConfig
import com.ads.control.config.AzAdConfig
import com.ads.control.config.TaichiConfig
import com.hdt.basecompose.BuildConfig
import com.hdt.basecompose.remoteconfig.RemoteInitializer
import com.hdt.basecompose.ui.language.Language
import com.hdt.basecompose.ui.splash.NativeSplashActivity
import com.hdt.basecompose.ui.splash.SplashActivity
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppProjectApplication : AdsMultiDexApplication() {

    lateinit var azAdConfig: AzAdConfig
    var isAppForeground = false

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppProjectApplication)
            modules(dataModule, viewModelModule)
        }
        Language.initDeviceLanguage()
        FirebaseApp.initializeApp(this)
        RemoteInitializer.init(this)
        initAds()
    }

    private fun initAds() {
        val environment = if (BuildConfig.build_debug) AzAdConfig.ENVIRONMENT_DEVELOP
        else AzAdConfig.ENVIRONMENT_PRODUCTION
        azAdConfig = AzAdConfig(this, environment)
        azAdConfig.adjustConfig = AdjustConfig(false, "")
        azAdConfig.appsflyerConfig = AppsflyerConfig(false, "")
        azAdConfig.taichiConfig = TaichiConfig(true)
        azAdConfig.listDeviceTest = listOf()
        AzAds.getInstance().init(this, azAdConfig)
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(NativeSplashActivity::class.java)
        Admob.getInstance().setOpenActivityAfterShowInterAds(true)
    }
}
