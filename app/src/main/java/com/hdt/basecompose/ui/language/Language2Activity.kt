package com.hdt.basecompose.ui.language

import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.facebook.shimmer.ShimmerFrameLayout
import com.hdt.basecompose.R
import com.hdt.basecompose.ads.`native`.NativeAdsWrapper
import com.hdt.basecompose.ads.`native`.NativePlacement
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.widget.NativeAdSlot
import com.hdt.basecompose.remoteconfig.analytics.Analytics
import com.hdt.basecompose.utils.extensions.moveItemToPosition
import org.koin.android.ext.android.inject

class Language2Activity : BaseActivity() {

    companion object {
        const val EXTRA_SELECTED_CODE = "selected_code"
        const val EXTRA_SCROLL_OFFSET = "scroll_offset"
    }

    private val prefs: PreferenceData by inject()
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdsWrapper: NativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.LANGUAGE_2,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    override fun isApplyLanguage() = false

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val preSelectedCode = remember { intent.getStringExtra(EXTRA_SELECTED_CODE) ?: "" }
        val languages = remember { buildLanguageList() }
        var selectedCode by remember { mutableStateOf(preSelectedCode) }
        val selectedItem = remember(selectedCode) { languages.find { it.code == selectedCode } }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.language)) },
                    actions = {
                        if (selectedItem != null) {
                            IconButton(onClick = { navigateToApply(selectedItem) }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.done),
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize()) {
                LanguageListContent(
                    languages = languages,
                    selectedCode = selectedCode,
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding()),
                    onItemClick = { item -> selectedCode = item.code },
                )

                NativeAdSlot(modifier = Modifier.fillMaxWidth()) { container, shimmer ->
                    adContainerRef = container
                    shimmerRef = shimmer
                }
            }
        }

        LaunchedEffect(Unit) {
            if (prefs.isUfo()) Analytics.track("ufo_language_dup")
            with(nativeAdsWrapper) {
                setupNativeAd("native_language_2")
                requestAds()
            }
        }
    }

    private fun buildLanguageList(): List<LanguageItem> {
        val deviceLanguage = Language.cachedDeviceLanguage
        val list = Language.listLanguage.toMutableList()
        list.forEach { it.isDefault = false }
        return if (list.indexOfFirst { it.code == deviceLanguage } != -1) {
            list.moveItemToPosition(3) { it.code == deviceLanguage }
        } else list
    }

    private fun navigateToApply(item: LanguageItem) {
        Language.changeLanguage(this, item.code)
        startActivityAndFinish<LanguageApplyActivity> {
            putExtra(LanguageApplyActivity.EXTRA_LANGUAGE_CODE, item.code)
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finishAffinity() }
        })
    }
}
