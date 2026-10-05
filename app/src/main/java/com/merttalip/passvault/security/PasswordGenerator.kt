package com.merttalip.passvault.security

import androidx.compose.ui.graphics.Color
import java.security.SecureRandom
import kotlin.math.log2

enum class PasswordStrength(val title: String, val color: Color, val progress: Float) {
    VERY_WEAK("Çok Zayıf", Color(0xFFE53935), 0.2f),
    WEAK("Zayıf", Color(0xFFFF9800), 0.4f),
    MEDIUM("Orta", Color(0xFFFFEB3B), 0.6f),
    STRONG("Güçlü", Color(0xFF4CAF50), 0.8f),
    VERY_STRONG("Çok Güçlü", Color(0xFF00BFA5), 1.0f)
}

data class PasswordGeneratorConfig(
    val length: Int = 18,
    val useUppercase: Boolean = true,
    val useLowercase: Boolean = true,
    val useNumbers: Boolean = true,
    val useSymbols: Boolean = true,
    val avoidAmbiguous: Boolean = true
)

object PasswordGenerator {
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val NUMBERS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    private val AMBIGUOUS = setOf('l', '1', 'I', 'o', 'O', '0', '|')

    private val MEMORABLE_WORDS = listOf(
        "atlas", "akdeniz", "kartal", "samanyolu", "bulut", "kristal", "parola", "kaplan",
        "safir", "yildiz", "firtina", "gokkusagi", "volkan", "orman", "deniz", "ruzgar",
        "kale", "pusula", "sahil", "yagmur", "safak", "nehir", "vadisi", "gezegen",
        "orbit", "zenith", "quantum", "solaris", "cyber", "falcon", "phoenix", "canyon"
    )

    fun generate(config: PasswordGeneratorConfig): String {
        var pool = ""
        val guaranteed = mutableListOf<Char>()
        val random = SecureRandom()

        fun filter(chars: String): String {
            return if (config.avoidAmbiguous) chars.filter { it !in AMBIGUOUS } else chars
        }

        val upper = filter(UPPERCASE)
        val lower = filter(LOWERCASE)
        val nums = filter(NUMBERS)
        val syms = filter(SYMBOLS)

        if (config.useUppercase && upper.isNotEmpty()) {
            pool += upper
            guaranteed.add(upper[random.nextInt(upper.length)])
        }
        if (config.useLowercase && lower.isNotEmpty()) {
            pool += lower
            guaranteed.add(lower[random.nextInt(lower.length)])
        }
        if (config.useNumbers && nums.isNotEmpty()) {
            pool += nums
            guaranteed.add(nums[random.nextInt(nums.length)])
        }
        if (config.useSymbols && syms.isNotEmpty()) {
            pool += syms
            guaranteed.add(syms[random.nextInt(syms.length)])
        }

        if (pool.isEmpty()) return ""

        val result = guaranteed.toMutableList()
        val remaining = maxOf(0, config.length - guaranteed.size)

        for (i in 0 until remaining) {
            result.add(pool[random.nextInt(pool.length)])
        }

        result.shuffle(random)
        return result.joinToString("")
    }

    fun generatePassphrase(wordCount: Int = 4, separator: String = "-", includeNumber: Boolean = true): String {
        val random = SecureRandom()
        val words = (1..wordCount).map {
            MEMORABLE_WORDS[random.nextInt(MEMORABLE_WORDS.size)]
        }
        var res = words.joinToString(separator)
        if (includeNumber) {
            val num = random.nextInt(90) + 10
            res += "$separator$num"
        }
        return res
    }

    fun evaluateStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.VERY_WEAK

        var poolSize = 0
        if (password.any { it.isUpperCase() }) poolSize += 26
        if (password.any { it.isLowerCase() }) poolSize += 26
        if (password.any { it.isDigit() }) poolSize += 10
        if (password.any { !it.isLetterOrDigit() }) poolSize += 32

        if (poolSize == 0) return PasswordStrength.VERY_WEAK
        val entropy = password.length * log2(poolSize.toDouble())

        return when {
            password.length < 8 || entropy < 28 -> PasswordStrength.VERY_WEAK
            password.length < 10 || entropy < 40 -> PasswordStrength.WEAK
            password.length < 12 || entropy < 55 -> PasswordStrength.MEDIUM
            entropy < 75 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
}
