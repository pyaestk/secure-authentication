package com.project.secureauthentication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.secureauthentication.data.repository.AuthRepository
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class AuthOptionViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableLiveData<AuthOptionUiState>()
    val uiState: LiveData<AuthOptionUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<AuthOptionUIEvent>()
    val uiEvent: LiveData<AuthOptionUIEvent> = _uiEvent

    fun checkUserRegistration(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            _uiEvent.value = AuthOptionUIEvent.NavigateToMainScreen
        }
    }

}

sealed class AuthOptionUiState {
    data object Loading : AuthOptionUiState()

}
sealed class AuthOptionUIEvent() {
    data class Error(var e: String): AuthOptionUIEvent()
    data object NavigateToMainScreen: AuthOptionUIEvent()
}