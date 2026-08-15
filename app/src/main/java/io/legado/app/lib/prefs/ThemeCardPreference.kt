package io.legado.app.lib.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import io.legado.app.R
import io.legado.app.constant.PreferKey
import io.legado.app.domain.gateway.AppShellSettingsGateway
import io.legado.app.domain.gateway.ThemeSettingsGateway
import io.legado.app.ui.theme.ThemeEngine
import io.legado.app.ui.theme.ThemeResolver
import io.legado.app.utils.activity
import io.legado.app.utils.isNightMode
import io.legado.app.utils.toastOnUi
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class ThemeCardPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    private val themeSettingsGateway by lazy {
        GlobalContext.get().get<ThemeSettingsGateway>()
    }
    private val appShellSettingsGateway by lazy {
        GlobalContext.get().get<AppShellSettingsGateway>()
    }

    private var entries: Array<CharSequence> = context.resources.getTextArray(R.array.themes_item)
    private var entryValues: Array<CharSequence> = context.resources.getTextArray(R.array.themes_value).takeIf { it.isNotEmpty() }
        ?: arrayOf("0")
    private var currentValue = "0"

    init {
        layoutResource = R.layout.preference_theme_card
        widgetLayoutResource = R.layout.item_theme_card
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        currentValue = themeSettingsGateway.currentSettings.appTheme
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        currentValue = themeSettingsGateway.currentSettings.appTheme
        val recyclerView = holder.findViewById(R.id.recyclerView) as RecyclerView
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            ?: LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false).also {
                recyclerView.layoutManager = it
            }
        val adapter = recyclerView.adapter as? ThemeAdapter
            ?: ThemeAdapter().also { recyclerView.adapter = it }

        recyclerView.clearOnScrollListeners()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                rememberScrollPosition(recyclerView)
            }
        })
        if (retainedFirstVisiblePosition != RecyclerView.NO_POSITION) {
            layoutManager.scrollToPositionWithOffset(
                retainedFirstVisiblePosition,
                retainedFirstVisibleOffset,
            )
        }
        adapter.notifyDataSetChanged()
    }

    private fun rememberScrollPosition(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val position = layoutManager.findFirstVisibleItemPosition()
        if (position == RecyclerView.NO_POSITION) return
        retainedFirstVisiblePosition = position
        retainedFirstVisibleOffset = layoutManager.findViewByPosition(position)?.let {
            layoutManager.getDecoratedLeft(it) - recyclerView.paddingStart
        } ?: 0
    }

    private inner class ThemeAdapter : RecyclerView.Adapter<ThemeViewHolder>() {
        private val colorCache = mutableMapOf<String, List<Int>>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_theme_card, parent, false)
            return ThemeViewHolder(view)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
            val label = entries.getOrNull(position)?.toString() ?: return
            val value = entryValues.getOrNull(position)?.toString() ?: return

            holder.label.text = label
            holder.card.isChecked = (value == currentValue)
            holder.card.contentDescription = label

            val colors = colorCache.getOrPut(value) { getThemeColors(value) }
            val currentThemeColors = colorCache.getOrPut(currentValue) {
                getThemeColors(currentValue)
            }
            holder.colorTop.setCardBackgroundColor(colors[0])
            holder.colorBook.setCardBackgroundColor(colors[1])
            holder.colorPin.setCardBackgroundColor(colors[2])
            holder.colorBottom.setCardBackgroundColor(colors[3])
            holder.colorBottomLeft.setCardBackgroundColor(colors[4])
            holder.colorBottomRight.setCardBackgroundColor(colors[5])
            holder.background.setCardBackgroundColor(colors[6])
            val isSelected = (value == currentValue)
            holder.background.strokeColor = if (isSelected) colors[4] else colors[7]
            holder.card.checkedIconTint = ColorStateList.valueOf(colors[2])
            holder.label.setTextColor(
                if (isSelected) currentThemeColors[4] else currentThemeColors[0]
            )
            ViewCompat.setStateDescription(
                holder.card,
                context.getString(if (isSelected) R.string.a11y_selected else R.string.a11y_not_selected)
            )


            holder.card.setOnClickListener {
                if (value == currentValue) return@setOnClickListener
                if (value == "13") {
                    val settings = themeSettingsGateway.currentSettings
                    val hasLightBg = !settings.backgroundImageLight.isNullOrEmpty()
                    val hasDarkBg = !settings.backgroundImageDark.isNullOrEmpty()
                    if (!hasLightBg || !hasDarkBg) {
                        context.toastOnUi(R.string.transparent_theme_alarm)
                        return@setOnClickListener
                    }
                }
                if (!callChangeListener(value)) return@setOnClickListener

                val activity = holder.itemView.activity ?: return@setOnClickListener
                (holder.itemView.parent as? RecyclerView)?.let(::rememberScrollPosition)
                currentValue = value
                activity.lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
                    themeSettingsGateway.update {
                        it.copy(
                            appTheme = value,
                            containerOpacity = if (value == "13") {
                                0
                            } else {
                                it.containerOpacity
                            },
                        )
                    }
                }
                notifyDataSetChanged()
            }
        }


        override fun getItemCount(): Int = entries.size
    }

    private fun getThemeColors(value: String): List<Int> {
        val settings = themeSettingsGateway.currentSettings
        val isDark = when (appShellSettingsGateway.currentSettings.themeMode) {
            "1" -> false
            "2" -> true
            else -> context.resources.configuration.isNightMode
        }
        val colorScheme = ThemeEngine.getColorScheme(
            context = context,
            mode = ThemeResolver.resolveThemeMode(value),
            darkTheme = isDark,
            isAmoled = settings.isPureBlack,
            paletteStyle = settings.paletteStyle,
            materialVersion = settings.materialVersion,
            customSeedColor = if (isDark) settings.customNightPrimary else settings.customPrimary,
            customContrast = settings.customContrast,
        )
        return listOf(
            colorScheme.onSurface.toArgb(),
            colorScheme.secondaryContainer.toArgb(),
            colorScheme.secondary.toArgb(),
            colorScheme.surfaceContainer.toArgb(),
            colorScheme.primary.toArgb(),
            colorScheme.onSurfaceVariant.toArgb(),
            colorScheme.surface.toArgb(),
            colorScheme.secondaryContainer.toArgb(),
        )
    }



    class ThemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardView)
        val label: TextView = view.findViewById(R.id.themeLabel)
        val colorTop: MaterialCardView = view.findViewById(R.id.cv_title)
        val colorBook: MaterialCardView = view.findViewById(R.id.cv_book)
        val colorPin: MaterialCardView = view.findViewById(R.id.cv_pin)
        val colorBottom: MaterialCardView = view.findViewById(R.id.cv_bottom)
        val colorBottomRight: MaterialCardView = view.findViewById(R.id.right_rect)
        val colorBottomLeft : MaterialCardView = view.findViewById(R.id.left_circle)
        val background : MaterialCardView = view.findViewById(R.id.cardView)
    }

    private companion object {
        var retainedFirstVisiblePosition = RecyclerView.NO_POSITION
        var retainedFirstVisibleOffset = 0
    }
}
