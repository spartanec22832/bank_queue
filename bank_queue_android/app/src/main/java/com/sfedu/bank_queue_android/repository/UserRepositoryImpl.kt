package com.sfedu.bank_queue_android.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.sfedu.bank_queue_android.mapper.toDomain
import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.network.RemoteDataSource
import com.sfedu.bank_queue_android.network.dto.ChangePasswordDto
import com.sfedu.bank_queue_android.network.dto.UserCreateDto
import com.sfedu.bank_queue_android.network.dto.UserUpdateDto
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remote: RemoteDataSource
) : UserRepository {

    override suspend fun register(
        name: String,
        login: String,
        email: String,
        password: String,
        phoneNumber: String
    ): Result<Unit> = runCatching {
        val dto = UserCreateDto(name, login, email, password, phoneNumber)
        remote.registerUser(dto)
        Unit
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getProfile(): Result<User> = runCatching {
        remote.getMyProfile().toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateProfile(
        name: String,
        email: String,
        phoneNumber: String
    ): Result<User> = runCatching {
        val dto = UserUpdateDto(name, email, phoneNumber)
        remote.updateMyProfile(dto).toDomain()
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> = runCatching {
        val resp = remote.changePassword(
            ChangePasswordDto(oldPassword, newPassword, confirmPassword)
        )
        if (!resp.isSuccessful) {
            throw retrofit2.HttpException(resp)
        }
        Unit
    }

    override suspend fun deleteAccount(): Result<Unit> =
        runCatching {
            remote.deleteMyAccount()
            Unit
        }
}