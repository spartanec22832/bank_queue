package com.sfedu.bank_queue_android.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.sfedu.bank_queue_android.network.dto.UserCreateDto
import com.sfedu.bank_queue_android.network.dto.UserUpdateDto
import com.sfedu.bank_queue_android.network.dto.UserResponseDto
import com.sfedu.bank_queue_android.model.User

// из ResponseDto в Domain
@RequiresApi(Build.VERSION_CODES.O)
fun UserResponseDto.toDomain() = User(
    id = this.id,
    name = this.name,
    login = this.login,
    email = this.email,
    phoneNumber = this.phoneNumber
)

// из Domain в CreateDto
fun User.toCreateDto(password: String) = UserCreateDto(
    name = this.name,
    login = this.login,
    email = this.email,
    password = password,
    phoneNumber = this.phoneNumber
)

// из Domain в UpdateDto
fun User.toUpdateDto() = UserUpdateDto(
    name = this.name,
    email = this.email,
    phoneNumber = this.phoneNumber
)