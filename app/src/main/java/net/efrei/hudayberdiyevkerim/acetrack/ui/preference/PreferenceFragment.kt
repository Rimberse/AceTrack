package net.efrei.hudayberdiyevkerim.acetrack.ui.preference

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import net.efrei.hudayberdiyevkerim.acetrack.R

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_layout, rootKey)
        val languagePreferences: ListPreference? = findPreference(PREF_TITLE_LANG)
        languagePreferences?.let {
            initLangPrefVal(language, it)
            it.setOnPreferenceChangeListener { _, newValue ->
                handleChangeLanguage(newValue.toString())
                true
    }
}
