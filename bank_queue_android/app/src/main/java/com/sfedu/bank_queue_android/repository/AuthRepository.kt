package com.sfedu.bank_queue_android.repository

import kotlinx.coroutines.flow.Flow

/** Интерфейс для авторизации и управления токеном */
interface AuthRepository {
    /** Авторизовать и вернуть токен */
    suspend fun login(username: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    fun getToken(): Flow<String?>
}