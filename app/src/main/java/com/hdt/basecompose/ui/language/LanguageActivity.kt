package com.hdt.basecompose.ui.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hdt.basecompose.app.PreferenceData
import com.hdt.basecompose.base.BaseActivity
import com.hdt.basecompose.MainActivity
import com.hdt.basecompose.style.SpaceW
import org.koin.android.ext.android.inject

class LanguageActivity : BaseActivity() {

    private val prefs: PreferenceData by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var selectedCode by remember { mutableStateOf(prefs.languageCode) }

        Scaffold(
            topBar = { TopAppBar(title = { Text("Select Language") }) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(languages, key = { it.code }) { lang ->
                    LanguageRow(
                        language   = lang,
                        isSelected = lang.code == selectedCode,
                        onClick    = {
                            selectedCode = lang.code
                            prefs.languageCode = lang.code
                            startActivityAndFinish<MainActivity>()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(
    language: LanguageItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = language.flag, style = MaterialTheme.typography.titleLarge)
        SpaceW(12.dp)
        Text(
            text     = language.name,
            style    = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color    = if (isSelected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface,
        )
        if (isSelected) {
            Icon(
                imageVector        = Icons.Default.Check,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
