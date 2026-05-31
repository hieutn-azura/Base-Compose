package com.hdt.basecompose.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.hdt.basecompose.style.AppViewTheme

abstract class BaseActivity : ComponentActivity() {

    var keyboardVisible by mutableStateOf(false)
        private set
    var keyboardHeight  by mutableIntStateOf(0)
        private set

    private var permissionCallback: ((Boolean) -> Unit)? = null
    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionCallback?.invoke(granted)
            permissionCallback = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeKeyboard()
        setContent {
            AppViewTheme {
                Content()
            }
        }
    }

    /** Override this to provide the screen's composable content. */
    @Composable
    abstract fun Content()

    private fun observeKeyboard() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            keyboardVisible = imeBottom > 0
            keyboardHeight  = imeBottom
            insets
        }
    }

    fun setStatusBarAppearance(lightIcons: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = lightIcons
    }

    fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        permissionCallback = callback
        permissionLauncher.launch(permission)
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    inline fun <reified T : ComponentActivity> startActivity(
        crossinline block: Intent.() -> Unit = {}
    ) {
        startActivity(Intent(this, T::class.java).apply(block))
    }

    inline fun <reified T : ComponentActivity> startActivityAndFinish(
        crossinline block: Intent.() -> Unit = {}
    ) {
        startActivity<T>(block)
        finish()
    }

    inline fun <reified T : ComponentActivity> startActivitySingleTop(
        crossinline block: Intent.() -> Unit = {}
    ) {
        startActivity(
            Intent(this, T::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .apply(block)
        )
    }
}
