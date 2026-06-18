package com.hdt.basecompose.ui.language

import android.content.Context
import android.content.res.Configuration
import com.hdt.basecompose.R
import com.hdt.basecompose.app.PreferenceData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

object Language : KoinComponent {
    private val prefs: PreferenceData by inject()

    var cachedDeviceLanguage: String = ""
        private set

    fun initDeviceLanguage() {
        if (cachedDeviceLanguage.isEmpty()) {
            cachedDeviceLanguage = android.content.res.Resources.getSystem().configuration.locales[0].language
        }
    }

    val listLanguage = listOf(
        LanguageItem(code = "en",    name = "English",                        flagId = R.drawable.ic_flag_uk),
        LanguageItem(code = "hi",    name = "हिंदी",                          flagId = R.drawable.ic_flag_india),
        LanguageItem(code = "fr",    name = "Français",                       flagId = R.drawable.ic_flag_france),
        LanguageItem(code = "es",    name = "Español",                        flagId = R.drawable.ic_flag_spain),
        LanguageItem(code = "pt",    name = "Português",                      flagId = R.drawable.ic_flag_portugal),
        LanguageItem(code = "ar",    name = "العربية",                        flagId = R.drawable.ic_flag_arab),
        LanguageItem(code = "bg",    name = "Български",                      flagId = R.drawable.ic_flag_bulgary),
        LanguageItem(code = "cs",    name = "Čeština",                        flagId = R.drawable.ic_flag_czech),
        LanguageItem(code = "da",    name = "Dansk",                          flagId = R.drawable.ic_flag_denmark),
        LanguageItem(code = "de",    name = "Deutsch",                        flagId = R.drawable.ic_flag_germany),
        LanguageItem(code = "el",    name = "Ελληνικά",                       flagId = R.drawable.ic_flag_greek),
        LanguageItem(code = "fa_rIR",name = "فارسی",                          flagId = R.drawable.ic_flag_iran),
        LanguageItem(code = "fi",    name = "Suomi",                          flagId = R.drawable.ic_flag_finland),
        LanguageItem(code = "fil",   name = "Filipino",                       flagId = R.drawable.ic_flag_filipino),
        LanguageItem(code = "hu",    name = "Magyar",                         flagId = R.drawable.ic_flag_hungary),
        LanguageItem(code = "in",    name = "Bahasa Indonesia",               flagId = R.drawable.ic_flag_indonesia),
        LanguageItem(code = "it",    name = "Italiano",                       flagId = R.drawable.ic_flag_italy),
        LanguageItem(code = "iw",    name = "עברית",                          flagId = R.drawable.ic_flag_israel),
        LanguageItem(code = "ja",    name = "日本語",                          flagId = R.drawable.ic_flag_japan),
        LanguageItem(code = "ko",    name = "한국어",                          flagId = R.drawable.ic_flag_south_korea),
        LanguageItem(code = "ms",    name = "Melayu",                         flagId = R.drawable.ic_flag_malaysia),
        LanguageItem(code = "nl",    name = "Nederlands",                     flagId = R.drawable.ic_flag_netherlands),
        LanguageItem(code = "pl",    name = "Polski",                         flagId = R.drawable.ic_flag_poland),
        LanguageItem(code = "ro",    name = "Română",                         flagId = R.drawable.ic_flag_romania),
        LanguageItem(code = "ru",    name = "Pусский язык",                   flagId = R.drawable.ic_flag_russia),
        LanguageItem(code = "sr",    name = "Српски",                         flagId = R.drawable.ic_flag_serbia),
        LanguageItem(code = "sv",    name = "Svenska",                        flagId = R.drawable.ic_flag_sweden),
        LanguageItem(code = "th",    name = "ไทย",                            flagId = R.drawable.ic_flag_thailand),
        LanguageItem(code = "tr",    name = "Türkçe",                         flagId = R.drawable.ic_flag_turkey),
        LanguageItem(code = "uk",    name = "Українська",                     flagId = R.drawable.ic_flag_ukraine),
        LanguageItem(code = "zh",    name = "简体中文",                        flagId = R.drawable.ic_flag_china),
        LanguageItem(code = "zh_TW", name = "繁體中文",                        flagId = R.drawable.ic_flag_taiwan),
        LanguageItem(code = "vi",    name = "Tiếng Việt",                     flagId = R.drawable.ic_flag_vietnam),
        LanguageItem(code = "pt_BR", name = "Brasil",                         flagId = R.drawable.ic_flag_brazil),
        LanguageItem(code = "es_MX", name = "México",                         flagId = R.drawable.ic_flag_mexico),
        LanguageItem(code = "ar_AE", name = "الإمارات العربية المتحدة",       flagId = R.drawable.ic_flag_uae),
        LanguageItem(code = "ar_SA", name = "المملكة العربية السعودية",       flagId = R.drawable.ic_flag_arab),
    )

    fun changeLanguage(context: Context, language: String): Context {
        if (language.isBlank()) return context
        var myLocale = Locale(language)
        if (language.contains("_")) {
            val parts = language.split("_")
            myLocale = Locale(parts[0], parts[1])
        }
        prefs.languageCode = language
        Locale.setDefault(myLocale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(myLocale)
        configuration.setLayoutDirection(myLocale)
        return context.createConfigurationContext(configuration)
    }

    fun createContextLocale(context: Context): Context {
        val selectedLanguage = prefs.languageCode
        if (selectedLanguage.isBlank()) return context
        val locale = if (selectedLanguage.contains("_")) {
            val parts = selectedLanguage.split("_")
            Locale(parts[0], parts[1])
        } else {
            Locale(selectedLanguage)
        }
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
