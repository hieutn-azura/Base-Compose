package com.hdt.basecompose.ui.feature

import com.hdt.basecompose.R

data class FeatureModel(
    val iconId: Int,
    val name: String,
    var isSelected: Boolean = false,
)

val sampleFeatures = listOf(
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 1"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 2"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 3"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 4"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 5"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 6"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 7"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 8"),
    FeatureModel(iconId = R.drawable.ic_launcher_foreground, name = "Feature 9"),
)
