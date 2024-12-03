package com.project.secureauthentication.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.secureauthentication.data.repository.AuthRepository
import com.project.secureauthentication.di.authFeatureModule
import com.project.secureauthentication.util.HCaptchaService
import com.project.suitcase.ui.viewmodel.LoginFormState
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
import com.project.suitcase.ui.viewmodel.util.Validator
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
    private val validator: Validator,
    private val hCaptchaService: HCaptchaService
): ViewModel() {

    private val _emailFormState = MutableLiveData(EmailFormState())
    val emailFormState: LiveData<EmailFormState> = _emailFormState

    private val _uiState = MutableLiveData<ForgotPwUiState>()
    val uiState: LiveData<ForgotPwUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<ForgotPwViewModelEvent>()
    val uiEvent: LiveData<ForgotPwViewModelEvent> = _uiEvent

    fun onEvent(activity: Activity, emailFormEvent: EmailFormEvent){
        when(emailFormEvent){
            is EmailFormEvent.EmailChanged -> {
                _emailFormState.value = _emailFormState.value?.copy(email = emailFormEvent.email)
            }
            EmailFormEvent.Submit -> {
                _emailFormState.value?.let { state ->
                    val emailResult = validator.emailValidator(state.email)

                    if (emailResult.successful) {
                        hCaptchaService.verifyHCaptcha(
                            activity,
                            addOnFailureListener = {
                                _uiEvent.value = ForgotPwViewModelEvent.Error("Verification Failed")
                            },
                            addOnSuccessListener = {
                                resetPassword(state.email)
                            }
                        )

                    }else{
                        _emailFormState.value = _emailFormState.value?.copy(
                            emailError = emailResult.errorMessage,
                        )
                    }
                }
            }
        }
    }

    private fun resetPassword(email: String) {
        _uiState.value = ForgotPwUiState.Loading
        viewModelScope.launch {
            authRepository.resetPassword(email).fold(
                onSuccess = {
                    _uiEvent.value = ForgotPwViewModelEvent.Success
                },
                onFailure = {
                    _uiEvent.value = ForgotPwViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                }
            )
        }
    }

    fun clearEmailError() {
        _emailFormState.value = _emailFormState.value?.copy(emailError = null)
    }

}

sealed class ForgotPwUiState {
    data object Loading : ForgotPwUiState()
}

sealed class ForgotPwViewModelEvent {
    data class Error(val error: String) : ForgotPwViewModelEvent()
    data object Success : ForgotPwViewModelEvent()
}
sealed class EmailFormEvent {
    data class EmailChanged(val email: String) : EmailFormEvent()
    data object Submit : EmailFormEvent()
}

data class EmailFormState(
    val email: String = "",
    val emailError: String? = null,
)