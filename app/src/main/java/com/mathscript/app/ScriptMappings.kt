package com.mathscript.app

object ScriptMappings {

    // Matches both "alpha" and "\alpha"
    private val wordShortcuts = mapOf(
        // Calculus & Math Symbols
        "partial" to "∂", "del" to "∂",
        "nabla" to "∇",
        "iiint" to "∭", "iint" to "∬", "oint" to "∮", "int" to "∫",
        "sum" to "∑", "prod" to "∏",
        "infty" to "∞", "inf" to "∞",
        "sqrt" to "√", "cbrt" to "∛",
        "pm" to "±", "mp" to "∓",
        "times" to "×", "cdot" to "·",
        "approx" to "≈", "neq" to "≠", "leq" to "≤", "geq" to "≥",
        "implies" to "⇒", "to" to "→",
        "forall" to "∀", "exists" to "∃", "in" to "∈", "notin" to "∉",

        // Capital Greek
        "Alpha" to "Α", "Beta" to "Β", "Gamma" to "Γ", "Delta" to "Δ",
        "Epsilon" to "Ε", "Zeta" to "Ζ", "Eta" to "Η", "Theta" to "Θ",
        "Iota" to "Ι", "Kappa" to "Κ", "Lambda" to "Λ", "Mu" to "Μ",
        "Nu" to "Ν", "Xi" to "Ξ", "Omicron" to "Ο", "Pi" to "Π",
        "Rho" to "Ρ", "Sigma" to "Σ", "Tau" to "Τ", "Upsilon" to "Υ",
        "Phi" to "Φ", "Chi" to "Χ", "Psi" to "Ψ", "Omega" to "Ω",

        // Lowercase Greek
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ",
        "varepsilon" to "ϵ", "epsilon" to "ε", "zeta" to "ζ", "eta" to "η",
        "vartheta" to "ϑ", "theta" to "θ", "iota" to "ι", "kappa" to "κ",
        "lambda" to "λ", "mu" to "μ", "nu" to "ν", "xi" to "ξ",
        "omicron" to "ο", "pi" to "π", "rho" to "ρ", "varsigma" to "ς",
        "sigma" to "σ", "tau" to "τ", "upsilon" to "υ", "varphi" to "ϕ",
        "phi" to "φ", "chi" to "χ", "psi" to "ψ", "omega" to "ω"
    )

    private val operatorShortcuts = mapOf(
        "!=" to "≠", "<=" to "≤", ">=" to "≥", "=>" to "⇒", "->" to "→"
    )

    // Unicode superscripts
    val superMap = mapOf(
        '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴",
        '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹",
        '+' to "⁺", '-' to "⁻", '=' to "⁼", '(' to "⁽", ')' to "⁾",
        '*' to "∗", '/' to "ᐟ", '.' to "˙", ',' to "ʼ",
        '∞' to "ꝏ", '!' to "ᵎ", ' ' to " ",
        'a' to "ᵃ", 'b' to "ᵇ", 'c' to "ᶜ", 'd' to "ᵈ", 'e' to "ᵉ",
        'f' to "ᶠ", 'g' to "ᵍ", 'h' to "ʰ", 'i' to "ⁱ", 'j' to "ʲ",
        'k' to "ᵏ", 'l' to "ˡ", 'm' to "ᵐ", 'n' to "ⁿ", 'o' to "ᵒ",
        'p' to "ᵖ", 'r' to "ʳ", 's' to "ˢ", 't' to "ᵗ", 'u' to "ᵘ",
        'v' to "ᵛ", 'w' to "ʷ", 'x' to "ˣ", 'y' to "ʸ", 'z' to "ᶻ",
        'A' to "ᴬ", 'B' to "ᴮ", 'D' to "ᴰ", 'E' to "ᴱ", 'G' to "ᴳ",
        'H' to "ᴴ", 'I' to "ᴵ", 'J' to "ᴶ", 'K' to "ᴷ", 'L' to "ᴸ",
        'M' to "ᴹ", 'N' to "ᴺ", 'O' to "ᴼ", 'P' to "ᴾ", 'R' to "ᴿ",
        'T' to "ᵀ", 'U' to "ᵁ", 'V' to "ⱽ", 'W' to "ᵂ",
        'α' to "ᵅ", 'β' to "ᵝ", 'γ' to "ᵞ", 'δ' to "ᵟ", 'ε' to "ᵋ",
        'θ' to "ᶿ", 'ι' to "ᶥ", 'φ' to "ᵠ", 'χ' to "ᵡ"
    )

    // Unicode subscripts
    val subMap = mapOf(
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄",
        '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉",
        '+' to "₊", '-' to "₋", '=' to "₌", '(' to "₍", ')' to "₎",
        '*' to "⁎", '/' to "ˏ", '.' to "․", ' ' to " ",
        'a' to "ₐ", 'e' to "ₑ", 'h' to "ₕ", 'i' to "ᵢ", 'j' to "ⱼ",
        'k' to "ₖ", 'l' to "ₗ", 'm' to "ₘ", 'n' to "ₙ", 'o' to "ₒ",
        'p' to "ₚ", 'r' to "ᵣ", 's' to "ₛ", 't' to "ₜ", 'u' to "ᵤ",
        'v' to "ᵥ", 'x' to "ₓ", 'ə' to "ₔ",
        'β' to "ᵦ", 'γ' to "ᵧ", 'ρ' to "ᵨ", 'φ' to "ᵩ", 'χ' to "ᵪ"
    )

    private val pattern = Regex("""(\^|_)(?:\(([^)]+)\)|([+\-]?[0-9]+|[+\-]?[a-zA-Zα-ωΑ-Ωə]+|[a-zA-Zα-ωΑ-Ωə+\-=*/.,!∞]))""")
    private val sortedWords = wordShortcuts.entries.sortedByDescending { it.key.length }

    fun convertMathScript(input: String): String {
        var working = input

        for ((word, symbol) in sortedWords) {
            working = Regex("""\\?\b${Regex.escape(word)}\b""").replace(working, symbol)
        }

        for ((op, symbol) in operatorShortcuts) {
            working = working.replace(op, symbol)
        }

        return pattern.replace(working) { match ->
            val type = match.groupValues[1]
            val content = match.groupValues[2].ifEmpty { match.groupValues[3] }
            val map = if (type == "^") superMap else subMap

            if (content.any { ch -> map[ch] == null }) {
                match.value
            } else {
                content.map { ch -> map[ch]!! }.joinToString("")
            }
        }
    }
}
