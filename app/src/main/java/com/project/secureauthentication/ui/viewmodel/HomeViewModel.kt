package com.project.secureauthentication.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.secureauthentication.data.repository.AuthRepository
import com.project.secureauthentication.domain.model.UserDetailModel
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
import com.project.suitcase.ui.viewmodel.util.ValidationResult
import com.project.suitcase.ui.viewmodel.util.Validator
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val validator: Validator,
):ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<HomeUIEvent>()
    val uiEvent: LiveData<HomeUIEvent> = _uiEvent

    private val _formState = MutableLiveData(NewPasswordFormState())
    val formState: LiveData<NewPasswordFormState> = _formState

    init {
        viewModelScope.launch {
            _uiState.value = HomeUiState.LOADING
            authRepository.getUserInfo().fold(
                onSuccess = {
                    _uiState.value = HomeUiState.SUCCESS(it)
                },
                onFailure = {
                    _uiEvent.value = HomeUIEvent.Error(it.message?:"Something went wrong")
                }
            )
        }
    }

    fun onEvent(event: NewPasswordFormEvent){
        when(event) {
            is NewPasswordFormEvent.newPasswordChange -> {
                _formState.value = _formState.value?.copy(
                    newPassword = event.newPassword,
                )
            }

            is NewPasswordFormEvent.passwordChange -> {
                _formState.value = _formState.value?.copy(
                    password = event.password,
                )
            }

            NewPasswordFormEvent.Submit -> {
                submitData()
            }

        }
    }

    fun clearPasswordError() {
        _formState.value = _formState.value?.copy(newPasswordError = null)
    }

    private fun submitData() {
        val currentState = _formState.value ?: return
        val passwordResult = if (currentState.password.isBlank()) {
            ValidationResult(successful = false, errorMessage = "Current password can't be empty")
        } else {
            ValidationResult(successful = true)
        }
        val newPasswordResult = validator.newPasswordValidator(
            currentState.newPassword,
        )

        val hasError = listOf(newPasswordResult, passwordResult).any {
            !it.successful
        }

        if (hasError){
            _formState.value = currentState.copy(
                passwordError = passwordResult.errorMessage,
                newPasswordError = newPasswordResult.errorMessage
            )
        } else {
            reAuthenticateAccount(password = currentState.password)
        }
    }

    fun reAuthenticateAccount(password: String) {
        viewModelScope.launch {
            authRepository.reAuthenticateAccount(password).fold(
                onSuccess = {
                    _uiState.value = HomeUiState.ReAuthenticateSuccess
                },
                onFailure = {
                    _uiState.value = HomeUiState.ReAuthenticateError
                }
            )
        }
    }
    fun updatePassword(password: String) {
        viewModelScope.launch {
            authRepository.updatePassword(password = password).fold(
                onSuccess = {
                    _uiState.value = HomeUiState.PwResetSuccess
                },
                onFailure = {
                    _uiEvent.value = HomeUIEvent.Error(it.message?:"Something went wrong")
                }
            )
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logoutAccount().fold(
                onFailure = {
                    _uiEvent.value = HomeUIEvent.Error(it.message?:"Something went wrong")
                },
                onSuccess = {
                    _uiEvent.value = HomeUIEvent.NavigateToAuthOptionScreen
                }
            )
        }
    }

}
sealed class HomeUiState {
    data object LOADING : HomeUiState()
    data class SUCCESS(val userDetailModel: UserDetailModel): HomeUiState()
    data object ReAuthenticateSuccess: HomeUiState()
    data object PwResetSuccess: HomeUiState()
    data object ReAuthenticateError: HomeUiState()
}
sealed class HomeUIEvent {
    data class Error(var e: String): HomeUIEvent()
    data object NavigateToAuthOptionScreen: HomeUIEvent()
}
data class NewPasswordFormState(
    val password: String = "",
    val passwordError: String? = null,
    val newPassword: String = "",
    val newPasswordError: String? = null,
)
sealed class NewPasswordFormEvent {
    data class passwordChange(val password: String): NewPasswordFormEvent()
    data class newPasswordChange(val newPassword: String) :NewPasswordFormEvent()
    data object Submit : NewPasswordFormEvent()
}