package com.hdt.basecompose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hdt.basecompose.base.BaseActivity

class MainActivity : BaseActivity() {

    @Composable
    override fun Content() {
        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                // Replace with your NavHost or main feature composable
                Text(text = "MainActivity — ready to build")
            }
        }
    }
}
