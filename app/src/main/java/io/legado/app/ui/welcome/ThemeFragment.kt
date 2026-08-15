package io.legado.app.ui.welcome

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.legado.app.R

class ThemeFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_strat_theme, rootKey)
    }
}
