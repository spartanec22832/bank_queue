package com.sfedu.bank_queue_android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val auth: AuthRepository
) : ViewModel() {
    var token by mutableStateOf<String?>(null); private set
    var profile by mutableStateOf<User?>(null);
    var isProcessing by mutableStateOf(false); private set
    var isLoadingProfile by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    init {
        viewModelScope.launch {
            auth.getToken()
                .distinctUntilChanged()
                .collect { newToken ->
                    token = newToken
                }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            isLoadingProfile = true
            errorMessage = null

            runCatching { userRepo.getProfile() }
                .onSuccess { user ->
                    profile = user.getOrNull()
                }
                .onFailure { ex ->
                    // на 401/403 автоматически удаляем токен
                    if ((ex as? HttpException)?.code() in listOf(401, 403)) {
                        auth.logout()
                    } else {
                        errorMessage = ex.message
                    }
                }

            isLoadingProfile = false
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        onResult: (Result<User>) -> Unit
    ) {
        viewModelScope.launch {
            isProcessing = true
            val res = userRepo.updateProfile(name, email, phone)
            onResult(res)
            isProcessing = false
        }
    }

    fun changePassword(
        oldPwd: String,
        newPwd: String,
        confPwd: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            isProcessing = true
            val res = userRepo.changePassword(oldPwd, newPwd, confPwd)
            onResult(res)
            isProcessing = false
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            auth.logout()
            profile = null
            onDone()
        }
    }
}