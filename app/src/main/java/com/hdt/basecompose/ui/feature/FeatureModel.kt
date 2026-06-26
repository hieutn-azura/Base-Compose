package com.hdt.basecompose.ui.feature

import androidx.annotation.StringRes
import com.hdt.basecompose.R

data class FeatureModel(
    @param:StringRes val nameRes: Int,
    var isSelected: Boolean = false,
)

val sampleFeatures = listOf(
    FeatureModel(R.string.genre_romance),
    FeatureModel(R.string.genre_revenge),
    FeatureModel(R.string.genre_ceo),
    FeatureModel(R.string.genre_thriller),
    FeatureModel(R.string.genre_betrayal),
    FeatureModel(R.string.genre_secret_baby),
    FeatureModel(R.string.genre_mafia),
    FeatureModel(R.string.genre_fantasy),
    FeatureModel(R.string.genre_marriage_contract),
    FeatureModel(R.string.genre_contemporary_romance),
)
