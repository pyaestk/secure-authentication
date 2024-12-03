package com.project.secureauthentication.util


enum class StrengthLevel {
    WEAK, MEDIUM, STRONG, PERFECT
}

object PasswordStrengthCalculator {

    fun checkPasswordStrength(password: String): StrengthLevel {
        val length = password.length
        val hasLetter = password.any { it.isLetter() }
        val hasUpperCase = password.any { it.isUpperCase() }
        val digitCount = password.count { it.isDigit() }
        val specialCount = password.count { it in "!@#$%^&*()-_=+[]{}|;:,.<>?" }

        return when {
            length >= 16 && hasLetter && hasUpperCase && digitCount >= 2 && specialCount >= 1 -> StrengthLevel.PERFECT
            length >= 11 && hasLetter && hasUpperCase && digitCount >= 2 && specialCount >= 1 -> StrengthLevel.STRONG
            length >= 9 && hasLetter && digitCount >= 2 && specialCount >= 1 -> StrengthLevel.MEDIUM
            else -> StrengthLevel.WEAK
        }
    }
}