package com.mathscript.app

object ScriptMappings {

    val superMap = mapOf(
        '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴",
        '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹",
        '+' to "⁺", '-' to "⁻", '=' to "⁼", '(' to "⁽", ')' to "⁾",
        '*' to "∗", '×' to "ˣ", '/' to "ᐟ", '.' to "˙", ',' to "ʼ",'∞' to "ꝏ",
        '!' to "ᵎ",' ' to " ",
        'a' to "ᵃ", 'b' to "ᵇ", 'c' to "ᶜ", 'd' to "ᵈ", 'e' to "ᵉ",
        'f' to "ᶠ", 'g' to "ᵍ", 'h' to "ʰ", 'i' to "ⁱ", 'j' to "ʲ",
        'k' to "ᵏ", 'l' to "ˡ", 'm' to "ᵐ", 'n' to "ⁿ", 'o' to "ᵒ",
        'p' to "ᵖ", 'r' to "ʳ", 's' to "ˢ", 't' to "ᵗ", 'u' to "ᵘ",
        'v' to "ᵛ", 'w' to "ʷ", 'x' to "ˣ", 'y' to "ʸ", 'z' to "ᶻ",
        'A' to "ᴬ", 'B' to "ᴮ", 'D' to "ᴰ", 'E' to "ᴱ", 'G' to "ᴳ",
        'H' to "ᴴ", 'I' to "ᴵ", 'J' to "ᴶ", 'K' to "ᴷ", 'L' to "ᴸ",
        'M' to "ᴹ", 'N' to "ᴺ", 'O' to "ᴼ", 'P' to "ᴾ", 'R' to "ᴿ",
        'S' to "ˢ", 'T' to "ᵀ", 'U' to "ᵁ", 'V' to "ⱽ", 'W' to "ᵂ",
        'X' to "ˣ", 'Y' to "ʸ", 'Z' to "ᶻ",
        'α' to "ᵅ", 'β' to "ᵝ", 'γ' to "ᵞ", 'δ' to "ᵟ", 'ε' to "ᵋ",
        'θ' to "ᶿ", 'ι' to "ᶥ", 'φ' to "ᵠ", 'χ' to "ᵡ"
    )

    val subMap = mapOf(
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄",
        '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉",
        '+' to "₊", '-' to "₋", '=' to "₌", '(' to "₍", ')' to "₎",
        '*' to "⁎", '×' to "ₓ", '/' to "ˏ", '.' to "․", ',' to "ˏ",
        ' ' to " ",
        'a' to "ₐ", 'b' to "ʙ", 'c' to "ᴄ", 'd' to "ᴅ", 'e' to "ₑ",
        'f' to "ꜰ", 'g' to "ɢ", 'h' to "ₕ", 'i' to "ᵢ", 'j' to "ⱼ",
        'k' to "ₖ", 'l' to "ₗ", 'm' to "ₘ", 'n' to "ₙ", 'o' to "ₒ",
        'p' to "ₚ", 'q' to "ǫ", 'r' to "ᵣ", 's' to "ₛ", 't' to "ₜ",
        'u' to "ᵤ", 'v' to "ᵥ", 'w' to "ᴡ", 'x' to "ₓ", 'y' to "ʏ", 'z' to "ᴢ", 'ə' to "ₔ",

        'β' to "ᵦ", 'γ' to "ᵧ", 'ρ' to "ᵨ", 'φ' to "ᵩ", 'χ' to "ᵪ"
    ) 

    private val pattern = Regex("""(\^|_)(?:\(([^)]+)\)|([0-9]+|[a-zA-Zα-ωΑ-Ωə+\-=*/×.,!∞]))""")

    fun convertMathScript(input: String): String {
        return pattern.replace(input) { match ->
            val type = match.groupValues[1]
            val content = match.groupValues[2].ifEmpty { match.groupValues[3] }
            val map = if (type == "^") superMap else subMap
            val hasUnsupported = content.any { ch -> map[ch] == null && map[ch.lowercaseChar()] == null }
            if (hasUnsupported) {
                match.value
            } else {
                content.map { ch -> map[ch] ?: map[ch.lowercaseChar()]!! }.joinToString("")
            }
        }
    }
}
