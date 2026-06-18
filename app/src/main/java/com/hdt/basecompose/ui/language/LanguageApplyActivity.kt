package com.hdt.basecompose.ui.language

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hdt.basecompose.R
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import com.hdt.basecompose.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LanguageApplyActivity : BaseActivity() {

    companion object {
        const val EXTRA_LANGUAGE_CODE = "language_code"
    }

    private val prefs: PreferenceData by inject()
    private var job: Job? = null
    private val showCheckmark = mutableStateOf(false)
    private val applyText = mutableStateOf<String?>(null)

    override fun isApplyLanguage() = false

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val languageCode = remember { intent.getStringExtra(EXTRA_LANGUAGE_CODE) ?: "en" }
        val languageItem = remember {
            Language.listLanguage.find { it.code == languageCode }
                ?: LanguageItem(code = "en", name = "English", flagId = R.drawable.ic_flag_uk)
        }
        val showCheck by showCheckmark
        val label by applyText
        val primary = MaterialTheme.colorScheme.primary

        val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_basic_light))
        val lottieProgress by animateLottieCompositionAsState(lottieComposition, iterations = Int.MAX_VALUE)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.language)) },
                    actions = {
                        IconButton(onClick = { navigateToOnboarding() }) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(8.dp))

                // Selected language card — same style as LanguageRow
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, primary, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = languageItem.flagId,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp).clip(CircleShape),
                        )
                        Text(
                            text = languageItem.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f).padding(start = 12.dp),
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                // Center animation area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                            Column {
                                AnimatedVisibility(visible = !showCheck, enter = fadeIn()) {
                                    LottieAnimation(
                                        composition = lottieComposition,
                                        progress = { lottieProgress },
                                        modifier = Modifier.size(80.dp),
                                    )
                                }
                                AnimatedVisibility(visible = showCheck, enter = fadeIn() + scaleIn()) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = primary,
                                        modifier = Modifier.size(56.dp),
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = label ?: stringResource(R.string.applying_language),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                            color = if (showCheck) primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            if (prefs.isUfo()) Analytics.track("ufo_apply_language")
            startCountdown()
        }
    }

    private fun startCountdown() {
        job?.cancel()
        job = lifecycleScope.launch {
            delay(1_500L)
            showCheckmark.value = true
            applyText.value = getString(R.string.changes_applied)
            delay(500L)
            if (isActive) navigateToOnboarding()
        }
    }

    private fun navigateToOnboarding() {
        job?.cancel()
        startActivityAndFinish<OnboardingActivity>()
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}
