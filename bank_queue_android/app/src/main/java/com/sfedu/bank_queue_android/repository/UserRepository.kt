package com.sfedu.bank_queue_android.repository


import com.sfedu.bank_queue_android.model.User

/** Интерфейс для работы с профилем пользователя */
interface UserRepository {
    suspend fun register(
        name: String,
        login: String,
        email: String,
        password: String,
        phoneNumber: String
    ): Result<Unit>

    suspend fun getProfile(): Result<User>

    suspend fun updateProfile(name: String, email: String, phoneNumber: String): Result<User>

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit>

    suspend fun deleteAccount(): Result<Unit>
}