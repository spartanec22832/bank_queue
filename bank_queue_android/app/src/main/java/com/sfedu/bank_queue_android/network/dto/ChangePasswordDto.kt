package com.sfedu.bank_queue_android.network.dto

data class ChangePasswordDto(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)