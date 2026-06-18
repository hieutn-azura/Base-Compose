package com.hdt.basecompose.ui.language

data class LanguageItem(
    var code: String,
    var name: String,
    var flagId: Int,
    var isChoose: Boolean = false,
    var isDefault: Boolean = false,
)
