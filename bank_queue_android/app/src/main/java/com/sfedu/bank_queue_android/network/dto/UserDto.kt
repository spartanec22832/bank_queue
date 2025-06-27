package com.sfedu.bank_queue_android.network.dto

data class UserCreateDto(
    val name: String,
    val login: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)

data class UserUpdateDto(
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
)

data class UserResponseDto(
    val id: Long,
    val name: String,
    val login: String,
    val email: String,
    val phoneNumber: String
)