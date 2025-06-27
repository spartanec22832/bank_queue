package com.sfedu.bank_queue_android.util

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor, который подставляет в каждый HTTP-запрос заголовок
 * Authorization: Bearer <token>, если токен сохранён в DataStore.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStore.data
                .map { prefs -> prefs[stringPreferencesKey("auth_token")] }
                .firstOrNull()
        }
        Log.d("AuthInterceptor", "Got token = $token")
        val request = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }.build()
        return chain.proceed(request)
    }
}