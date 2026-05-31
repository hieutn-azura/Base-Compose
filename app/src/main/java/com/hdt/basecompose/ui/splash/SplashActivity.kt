package com.hdt.basecompose.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.MainActivity
import com.hdt.basecompose.ui.language.LanguageActivity
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    private val prefs: PreferenceData by inject()

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            delay(2000L)
            if (prefs.firstOpenApp) {
                prefs.firstOpenApp = false
                startActivityAndFinish<LanguageActivity>()
            } else {
                startActivityAndFinish<MainActivity>()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            // Replace with your app logo / Lottie animation
            Text(
                text       = "App Name",
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
