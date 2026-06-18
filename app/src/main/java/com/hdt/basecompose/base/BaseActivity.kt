package com.hdt.basecompose.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hdt.basecompose.style.AppViewTheme
import com.hdt.basecompose.ui.language.Language
import com.hdt.basecompose.base.setFullScreen
import com.hdt.basecompose.base.hideSystemBar

abstract class BaseActivity : AppCompatActivity() {

    var keyboardVisible by mutableStateOf(false)
        private set
    var keyboardHeight by mutableIntStateOf(0)
        private set

    private var permissionCallback: ((Boolean) -> Unit)? = null
    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionCallback?.invoke(granted)
            permissionCallback = null
        }

    open fun isApplyLanguage(): Boolean = true

    /** Override to true if the activity should draw into the display cutout area. */
    open fun isDisplayCutout(): Boolean = false

    override fun attachBaseContext(newBase: Context?) {
        if (!isApplyLanguage()) {
            super.attachBaseContext(newBase)
            return
        }
        newBase?.let { super.attachBaseContext(Language.createContextLocale(it)) }
            ?: super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setFullScreen()
        window.hideSystemBar()
        observeKeyboard()
        setContent {
            AppViewTheme {
                Content()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.hideSystemBar()
    }

    @Composable
    abstract fun Content()

    private fun observeKeyboard() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            keyboardVisible = imeBottom > 0
            keyboardHeight = imeBottom
            insets
        }
    }

    fun setStatusBarAppearance(lightIcons: Boolean) {
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = lightIcons
    }

    fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        permissionCallback = callback
        permissionLauncher.launch(permission)
    }

    inline fun <reified T : AppCompatActivity> startActivity(
        crossinline block: Intent.() -> Unit = {}
    ) {
        startActivity(Intent(this, T::class.java).apply(block))
    }

    inline fun <reified T : AppCompatActivity> startActivityAndFinish(
        crossinline block: Intent.() -> Unit = {}
    ) {
        startActivity<T>(block)
        finish()
    }

    inline fun <reified T : AppCompatActivity> startActivitySingleTop(
        crossinline block: Intent.() -> Unit = {}
    ) {
        startActivity(
            Intent(this, T::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .apply(block)
        )
    }
}
