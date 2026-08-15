package io.legado.app.lib.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceViewHolder
import com.google.android.material.button.MaterialButtonToggleGroup
import io.legado.app.R
import io.legado.app.domain.gateway.AppShellSettingsGateway
import io.legado.app.help.config.ThemeConfigStore
import io.legado.app.utils.activity
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class ThemeModePreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    private val appShellSettingsGateway by lazy {
        GlobalContext.get().get<AppShellSettingsGateway>()
    }

    private var currentValue: String = "0"

    init {
        layoutResource = R.layout.view_pref
        widgetLayoutResource = R.layout.view_theme_mode
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val toggleGroup =
            holder.itemView.findViewById<MaterialButtonToggleGroup>(R.id.theme_toggle_group)
                ?: return

        currentValue = appShellSettingsGateway.currentSettings.themeMode
        toggleGroup.clearOnButtonCheckedListeners()
        selectCurrentValue(toggleGroup)
        setupToggleGroup(toggleGroup)
        updateButtonStateDescriptions(toggleGroup)
    }

    private fun setupToggleGroup(toggleGroup: MaterialButtonToggleGroup) {
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val newValue = when (checkedId) {
                R.id.btn_system -> "0"
                R.id.btn_light -> "1"
                R.id.btn_dark -> "2"
                else -> return@addOnButtonCheckedListener
            }
            if (newValue == currentValue || !callChangeListener(newValue)) return@addOnButtonCheckedListener

            val activity = group.activity ?: return@addOnButtonCheckedListener
            currentValue = newValue
            updateButtonStateDescriptions(group)
            activity.lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
                appShellSettingsGateway.update { it.copy(themeMode = newValue) }
                ThemeConfigStore.initNightMode()
            }
        }
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        currentValue = appShellSettingsGateway.currentSettings.themeMode
    }

    private fun selectCurrentValue(toggleGroup: MaterialButtonToggleGroup) {
        when (currentValue) {
            "0" -> toggleGroup.check(R.id.btn_system)
            "1" -> toggleGroup.check(R.id.btn_light)
            "2" -> toggleGroup.check(R.id.btn_dark)
        }
    }

    private fun updateButtonStateDescriptions(group: MaterialButtonToggleGroup) {
        for (index in 0 until group.childCount) {
            val child = group.getChildAt(index)
            val selected = child.id == group.checkedButtonId
            ViewCompat.setStateDescription(
                child,
                context.getString(if (selected) R.string.a11y_selected else R.string.a11y_not_selected)
            )
            child.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
    }

}
