package com.hdt.basecompose.app

class PreferenceData(private val preference: SharePreference) {

    var languageCode: String by preference.string("language_code", "en")
    var isFinishFirstFlow: Boolean by preference.boolean("is_finish_first_flow", false)
    var firstOpenApp: Boolean by preference.boolean("first_open_app", true)
    var countSessionApp: Int by preference.int("count_session_app", 0)

    fun isUfo(): Boolean = countSessionApp == 1
}
