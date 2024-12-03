package com.project.secureauthentication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.secureauthentication.util.EmailService
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class OTPViewModel(
    private val emailService: EmailService
): ViewModel() {

    private val _uiState = MutableLiveData<OTPUiState>()
    val uiState: LiveData<OTPUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<OTPViewModelEvent>()
    val uiEvent: LiveData<OTPViewModelEvent> = _uiEvent

    private var generatedOTP: String? = null

    fun otpSend(email: String) {
        viewModelScope.launch {
            emailService.sendOTP(
                email = email,
                onSuccessListener = { otp ->
                    generatedOTP = otp
                    _uiState.value = OTPUiState.OTPSendingSuccess
                },
                onFailureListener = { exception ->
                    _uiEvent.value = OTPViewModelEvent.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    fun verifyOTP(otpCode: String){
        _uiState.value = OTPUiState.Loading
        if (generatedOTP == otpCode) {
            _uiState.value = OTPUiState.OTPVerifySuccess
        } else {
            _uiEvent.value = OTPViewModelEvent.Error("Invalid OTP entered.")
        }
    }


}

sealed class OTPUiState {
    data object Loading : OTPUiState()
    data object OTPSendingSuccess : OTPUiState()
    data object OTPVerifySuccess: OTPUiState()
}

sealed class OTPViewModelEvent {
    data class Error(val error: String) : OTPViewModelEvent()
}