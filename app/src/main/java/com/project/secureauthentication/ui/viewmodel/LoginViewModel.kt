package com.project.suitcase.ui.viewmodel

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.integrity.internal.ac
import com.google.firebase.auth.FirebaseAuth
import com.hcaptcha.sdk.HCaptcha
import com.project.secureauthentication.data.repository.AuthRepository
import com.project.secureauthentication.ui.viewmodel.AuthOptionUIEvent
import com.project.secureauthentication.ui.viewmodel.ForgotPwViewModelEvent
import com.project.secureauthentication.util.EmailService
import com.project.secureauthentication.util.HCaptchaService
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
import com.project.suitcase.ui.viewmodel.util.Validator
import kotlinx.coroutines.launch
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendTask
import java.util.Random
import kotlin.math.log

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val validator: Validator,
    private val hCaptchaService: HCaptchaService,
    private val emailService: EmailService
): ViewModel() {

    private val _loginFormState = MutableLiveData(LoginFormState())
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<LoginViewModelEvent>()
    val uiEvent: LiveData<LoginViewModelEvent> = _uiEvent

    private var loginAttempt = 0


    fun onEvent(activity: Activity, loginFormEvent: LoginFormEvent) {
        when (loginFormEvent) {
            is LoginFormEvent.EmailChanged -> {
                _loginFormState.value = _loginFormState.value?.copy(email = loginFormEvent.email)
            }

            is LoginFormEvent.PasswordChange -> {
                _loginFormState.value =
                    _loginFormState.value?.copy(password = loginFormEvent.password)
            }
            is LoginFormEvent.Submit -> {
                submitData(activity)
            }
        }
    }

    private fun submitData(activity: Activity) {

        _loginFormState.value?.let { state ->
            val emailResult = validator.emailValidator(state.email)
            val passwordResult = state.password.isNotBlank()

            if (emailResult.successful && passwordResult) {
                login(state.email, state.password, activity)
            } else if(!passwordResult){
                _loginFormState.value = _loginFormState.value?.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = "password can't be blank",
                )
            } else {
                loginAttempt++
                _loginFormState.value = _loginFormState.value?.copy(
                    emailError = emailResult.errorMessage,
                )
            }
        }
    }

    private fun login(email: String, password: String, activity: Activity) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authRepository.loginAccount(email, password).fold(
                onSuccess = {
                    viewModelScope.launch {
                        authRepository.checkEmailVerification().fold(
                            onSuccess = { isVerified ->
                                if (isVerified) {
                                    if (loginAttempt >= 3) {
                                        hCaptchaService.verifyHCaptcha(
                                            activity,
                                            addOnFailureListener = {
                                                FirebaseAuth.getInstance().signOut()
                                                _uiEvent.value = LoginViewModelEvent.Error(
                                                    "Verification Failed",
                                                    loginAttempt
                                                )
                                            },
                                            addOnSuccessListener = {
                                                _uiEvent.value = LoginViewModelEvent.LoginSuccess
                                            }
                                        )
                                    } else {
                                        _uiEvent.value = LoginViewModelEvent.LoginSuccess
                                    }
                                } else {
                                    _uiEvent.value = LoginViewModelEvent.Error(
                                        "Email is not verified. Please verify your email and try again.",
                                        loginAttempt
                                    )
                                }
                            },
                            onFailure = {
                                _uiEvent.value = LoginViewModelEvent.Error(
                                    it.message ?: "Failed to check email verification status.",
                                    loginAttempt
                                )
                            }
                        )
                    }
                },
                onFailure = {
                    loginAttempt++
                    if (loginAttempt >= 5) {
                        emailService.sendAlert(
                            email = email
                        )
                    }
                    _uiEvent.value = LoginViewModelEvent.Error(
                        it.message ?: "Invalid email or password.",
                        loginAttempt
                    )
                }
            )
        }
    }

    fun clearEmailError() {
        _loginFormState.value = _loginFormState.value?.copy(emailError = null)
    }

    fun clearPasswordError() {
        _loginFormState.value = _loginFormState.value?.copy(passwordError = null)
    }
}

sealed class LoginUiState {
    data object Loading : LoginUiState()
}

sealed class LoginViewModelEvent {
    data object LoginSuccess : LoginViewModelEvent()
    data class Error(val error: String, val loginAttempt: Int) : LoginViewModelEvent()
}
sealed class LoginFormEvent {
    data class EmailChanged(val email: String) : LoginFormEvent()
    data class PasswordChange(val password: String) : LoginFormEvent()
    data object Submit : LoginFormEvent()
}

data class LoginFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null
)