package com.sfedu.bank_queue_android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.network.RemoteDataSource
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Состояние экрана авторизации/регистрации */
sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val remote: RemoteDataSource
) : ViewModel() {

    /** Текущее состояние UI */
    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            runCatching {
                val resp = remote.login(AuthRequest(username, password))
                authRepo.login(username, password)
                resp.token
            }.onSuccess { token ->
                uiState = AuthUiState.Success
            }.onFailure {
                uiState = AuthUiState.Error("Ошибка авторизации! Проверьте правильность заполнения формы")
            }
        }
    }

    /** Регистрация нового пользователя */
    fun register(
        name: String,
        login: String,
        email: String,
        password: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            userRepo.register(name, login, email, password, phoneNumber)
                .fold(
                    onSuccess = { uiState = AuthUiState.Success },
                    onFailure = { uiState = AuthUiState.Error("Ошибка регистрации! Проверьте правильность заполнения формы") }
                )
        }
    }

    /** Логаут (очистка токена) */
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            uiState = AuthUiState.Idle
        }
    }
}