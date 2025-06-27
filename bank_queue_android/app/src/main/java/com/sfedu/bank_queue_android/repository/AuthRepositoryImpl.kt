package com.sfedu.bank_queue_android.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sfedu.bank_queue_android.network.RemoteDataSource
import com.sfedu.bank_queue_android.model.AuthRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remote: RemoteDataSource,
    private val dataStore: DataStore<Preferences>
): AuthRepository {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    override suspend fun login(username: String, password: String): Result<String> =
        runCatching {
            val resp = remote.login(AuthRequest(username, password))
            // сохраняем токен
            dataStore.edit { prefs -> prefs[TOKEN_KEY] = resp.token }
            Log.d("AuthRepository", "Saving token = ${resp.token}")
            resp.token
        }

    override suspend fun logout(): Result<Unit> = runCatching {
        dataStore.edit { prefs -> prefs.remove(TOKEN_KEY) }
    }

    override fun getToken(): Flow<String?> =
        dataStore.data.map { it[TOKEN_KEY] }
}