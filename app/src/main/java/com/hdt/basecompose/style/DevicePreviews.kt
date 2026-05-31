package com.hdt.basecompose.style

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Phone",           showBackground = true, widthDp = 360,  heightDp = 800)
@Preview(name = "Phone Landscape", showBackground = true, widthDp = 800,  heightDp = 360)
@Preview(name = "Tablet",          showBackground = true, widthDp = 840,  heightDp = 1280)
@Preview(name = "Dark Phone",      showBackground = true, widthDp = 360,  heightDp = 800,  uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class DevicePreviews
