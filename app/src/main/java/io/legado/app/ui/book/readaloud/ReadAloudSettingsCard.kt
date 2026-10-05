package io.legado.app.ui.book.readaloud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.widget.components.card.NormalCard
import io.legado.app.ui.widget.components.icon.AppIcon
import io.legado.app.ui.widget.components.text.AppText

@Composable
fun ReadAloudSettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    detail: String? = null,
    detailColor: Color = LegadoTheme.colorScheme.primary,
    imageVector: ImageVector? = null,
    selected: Boolean? = null,
    enabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val contentAlpha = if (enabled) 1f else 0.5f
    NormalCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .heightIn(min = 56.dp)
            .semantics(mergeDescendants = true) {
                selected?.let {
                    this.selected = it
                    role = Role.RadioButton
                }
                if (!enabled) disabled()
            },
        cornerRadius = 12.dp,
        containerColor = if (selected == true) {
            LegadoTheme.colorScheme.secondaryContainer
        } else {
            LegadoTheme.colorScheme.surfaceContainerLow
        },
        onClick = onClick?.takeIf { enabled },
        onLongClick = onLongClick?.takeIf { enabled },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            imageVector?.let {
                AppIcon(
                    imageVector = it,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AppText(
                    text = title,
                    style = LegadoTheme.typography.titleSmallEmphasized,
                    color = LegadoTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                description?.takeIf(String::isNotBlank)?.let {
                    AppText(
                        text = it,
                        style = LegadoTheme.typography.labelSmall,
                        color = LegadoTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                detail?.takeIf(String::isNotBlank)?.let {
                    AppText(
                        text = it,
                        style = LegadoTheme.typography.labelSmall,
                        color = detailColor.copy(alpha = contentAlpha),
                    )
                }
            }
            when {
                trailingContent != null -> trailingContent()
                selected == true -> AppIcon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                selected == null && onClick != null -> AppIcon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
