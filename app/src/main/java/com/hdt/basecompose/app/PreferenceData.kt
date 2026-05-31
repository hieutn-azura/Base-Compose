package com.hdt.basecompose.app

class PreferenceData(private val preference: SharePreference) {

    var languageCode: String by preference.string("language_code", "en")
    var isFinishFirstFlow: Boolean by preference.boolean("is_finish_first_flow", false)
    var firstOpenApp: Boolean by preference.boolean("first_open_app", true)

    // Add project-specific keys below:
    // var myFlag: Boolean by preference.boolean("my_flag_key", false)
    // var userId: String  by preference.string("user_id", "")
}
