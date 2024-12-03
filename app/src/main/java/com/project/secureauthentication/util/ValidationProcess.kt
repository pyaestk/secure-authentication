package com.project.suitcase.ui.viewmodel.util

import android.content.Context
import android.util.Patterns
import com.project.secureauthentication.R

interface Validator {
    fun emailValidator(email: String): ValidationResult
    fun passwordValidator(password: String, confirmPassword: String): ValidationResult
    fun confirmPasswordValidator(password: String, confirmPassword: String): ValidationResult

    fun newPasswordValidator(password: String):ValidationResult
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)

class ValidatorImpl(private val context: Context) : Validator {

    override fun emailValidator(email: String): ValidationResult {
        return when {
            email.isBlank() -> {
                ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.the_email_can_t_be_blank)
                )
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.that_s_not_a_valid_email)
                )
            }

            else -> {
                ValidationResult(
                    successful = true
                )
            }
        }
    }

    override fun passwordValidator( password: String, confirmPassword: String): ValidationResult {
        val digitPattern = "(.*\\d.*\\d)".toRegex()
        val specialCharacters = "(?=.*[!@#%^&()_+$\\-=\\[\\]{}|;':\",./<>?~`])".toRegex()
        val uppercasePattern = "(?=.*[A-Z])".toRegex()
        val lowercasePattern = "(?=.*[a-z])".toRegex()

        when {
            //checking empty password
            password.isBlank() -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.this_password_can_t_be_blank)
                )
            }
            //checking password length
            (password.length < 9) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.nine_characters_minimum_and_remaining)
                )
            }
            //checking password digit
            (!digitPattern.containsMatchIn(password)) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_2_numbers)
                )
            }
            //checking password special character
            (!specialCharacters.containsMatchIn(password)) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_1_special_character)
                )
            }
            //checking uppercase letter
            !uppercasePattern.containsMatchIn(password) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_1_uppercase_letter)
                )
            }
            //checking lowercase letter
            !lowercasePattern.containsMatchIn(password) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_1_lowercase_letter)
                )
            }

            password != confirmPassword -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.the_passwords_doesn_t_match)
                )
            }

            else -> {
                return ValidationResult(
                    successful = true
                )
            }
        }
    }

    override fun confirmPasswordValidator(
        password: String,
        confirmPassword: String
    ): ValidationResult {
        return when {
            confirmPassword.isEmpty() -> {
                ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.confirm_password_can_t_be_empty)
                )
            }

            password != confirmPassword -> {
                ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.the_passwords_doesn_t_match)
                )
            }

            else -> {
                ValidationResult(
                    successful = true
                )
            }
        }
    }

    override fun newPasswordValidator(password: String): ValidationResult {
        val digitPattern = "(.*\\d.*\\d)".toRegex()
        val specialCharacters = "(?=.*[!@#%^&()_+$\\-=\\[\\]{}|;':\",./<>?~`])".toRegex()
        val uppercasePattern = "(?=.*[A-Z])".toRegex()
        val lowercasePattern = "(?=.*[a-z])".toRegex()

        when {
            //checking empty password
            password.isBlank() -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.this_password_can_t_be_blank)
                )
            }
            //checking password length
            (password.length < 9) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.nine_characters_minimum_and_remaining)
                )
            }
            //checking password digit
            (!digitPattern.containsMatchIn(password)) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_2_numbers)
                )
            }
            //checking password special character
            (!specialCharacters.containsMatchIn(password)) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_1_special_character)
                )
            }
            //checking uppercase letter
            !uppercasePattern.containsMatchIn(password) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_1_uppercase_letter)
                )
            }
            //checking lowercase letter
            !lowercasePattern.containsMatchIn(password) -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = context.getString(R.string.password_must_contain_at_least_1_lowercase_letter)
                )
            }
            else -> {
                return ValidationResult(
                    successful = true
                )
            }
        }
    }
}


