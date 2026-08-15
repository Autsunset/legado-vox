package io.legado.app.ui.book.read.sheet

import io.legado.app.domain.model.settings.ReadStyleItem
import io.legado.app.help.config.ReadStyleResolver

internal object ReadBackgroundPreset {
    private const val ASSET_PREFIX = "file:///android_asset/bg/"

    val items = listOf(
        "护眼漫绿.jpg",
        "清新时光.jpg",
        "明媚倾城.jpg",
        "午后沙滩.jpg",
        "宁静夜色.jpg",
        "山水墨影.jpg",
        "山水画.jpg",
        "深宫魅影.jpg",
        "边彩画布.jpg",
        "新羊皮纸.jpg",
        "羊皮纸1.jpg",
        "羊皮纸2.jpg",
        "羊皮纸3.jpg",
        "羊皮纸4.jpg",
    ).map { fileName ->
        ReadStyleItem(
            name = fileName.substringBeforeLast('.'),
            bgType = 1,
            bgValue = fileName,
            bgTypeNight = 1,
            bgValueNight = fileName,
            bgTypeEInk = 1,
            bgValueEInk = fileName,
            textColor = 0xFFFFFFFF.toInt(),
            textColorNight = 0xFFFFFFFF.toInt(),
            textColorEInk = 0xFFFFFFFF.toInt(),
        )
    }

    fun previewModel(bgType: Int, bgValue: String): String? {
        return when (bgType) {
            1 -> "$ASSET_PREFIX$bgValue"
            2 -> ReadStyleResolver.backgroundPath(bgType, bgValue)
            else -> null
        }
    }
}
