package io.legado.app.help.readaloud.resolve

import com.google.gson.Gson
import com.google.gson.JsonParser

data class SpeakerMatchRule(
    val character: String = "",
    val pattern: String = "",
    val afterDialogue: Boolean = false,
    val enabled: Boolean = true,
)

object SpeakerMatchRules {
    private val gson = Gson()

    fun parse(json: String): List<SpeakerMatchRule> {
        require(json.length <= 1_000_000) { "规则文件过大" }
        val root = JsonParser.parseString(json)
        val array = if (root.isJsonArray) root.asJsonArray else {
            require(root.isJsonObject && root.asJsonObject.has("matchRules")) { "规则需为 JSON 数组或包含 matchRules 的对象" }
            root.asJsonObject.getAsJsonArray("matchRules")
        }
        require(array.size() <= 200) { "规则最多 200 条" }
        return array.map { element ->
            require(element.isJsonObject) { "规则必须是对象" }
            val rule = gson.fromJson(element, SpeakerMatchRule::class.java)
            require(!rule.character.isNullOrBlank()) { "请填写目标角色 character" }
            require(!rule.pattern.isNullOrBlank() && rule.pattern.length <= 512) { "正则 pattern 需为 1–512 字符" }
            Regex(rule.pattern)
            rule
        }
    }

    fun parseOrEmpty(json: String): List<SpeakerMatchRule> = runCatching { parse(json) }.getOrDefault(emptyList())
    fun serialize(rules: List<SpeakerMatchRule>): String = gson.toJson(rules)
}
