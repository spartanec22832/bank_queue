package com.sfedu.bank_queue_android.network

import com.sfedu.bank_queue_android.network.dto.TicketCreateDto
import com.sfedu.bank_queue_android.network.dto.TicketResponseDto
import com.sfedu.bank_queue_android.network.dto.TicketUpdateDto
import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.model.AuthResponse
import com.sfedu.bank_queue_android.network.dto.ChangePasswordDto
import com.sfedu.bank_queue_android.network.dto.UserCreateDto
import com.sfedu.bank_queue_android.network.dto.UserResponseDto
import com.sfedu.bank_queue_android.network.dto.UserUpdateDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse


    // --- TICKETS ---
    /** 1. Получить все тикеты текущего пользователя */
    @GET("api/tickets")
    suspend fun getMyTickets(): List<TicketResponseDto>


    @GET("api/tickets/{id}")
    suspend fun getTicket(@Path("id") id: Long): TicketResponseDto

    /** 2. Создать новый тикет */
    @POST("api/tickets")
    suspend fun createTicket(
        @Body body: TicketCreateDto
    ): TicketResponseDto

    /** 3. Частичное обновление тикета */
    @PATCH("api/tickets/{id}")
    suspend fun updateTicket(
        @Path("id") id: Long,
        @Body body: TicketUpdateDto
    ): TicketResponseDto

    /** 4. Удалить тикет */
    @DELETE("api/tickets/{id}")
    suspend fun deleteTicket(
        @Path("id") id: Long
    ): Response<Unit>

    // --- USERS ---

    /** 1. Зарегистрировать нового пользователя — POST /api/users/register */
    @POST("api/users/register")
    suspend fun registerUser(
        @Body request: UserCreateDto
    ): UserResponseDto

    /** 2. Получить профиль текущего пользователя — GET /api/users/me */
    @GET("api/users/me")
    suspend fun getMyProfile(): UserResponseDto

    /** 3. Частичное обновление профиля — PATCH /api/users/me */
    @PATCH("api/users/me")
    suspend fun updateMyProfile(
        @Body request: UserUpdateDto
    ): UserResponseDto

    /** 4. Сменить пароль пользователя — POST /api/users/me/change-password */
    @POST("api/users/me/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordDto
    ): Response<Unit>

    /** 5. Удалить аккаунт — DELETE /api/users/me */
    @DELETE("api/users/me")
    suspend fun deleteMyAccount(): Response<Unit>

}