package com.sfedu.bank_queue_android.network

import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.network.dto.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val api: ApiService
) {

    // --- AUTH ---
    suspend fun login(request: AuthRequest) =
        api.login(request)

    // --- TICKETS ---
    suspend fun getMyTickets(): List<TicketResponseDto> =
        api.getMyTickets()

    suspend fun getTicket(id: Int): TicketResponseDto =
        api.getTicket(id.toLong())

    suspend fun createTicket(request: TicketCreateDto): TicketResponseDto =
        api.createTicket(request)

    suspend fun updateTicket(id: Long, request: TicketUpdateDto): TicketResponseDto =
        api.updateTicket(id, request)

    suspend fun deleteTicket(id: Long): Response<Unit> =
        api.deleteTicket(id)

    // --- USERS ---
    suspend fun registerUser(request: UserCreateDto): UserResponseDto =
        api.registerUser(request)

    suspend fun getMyProfile(): UserResponseDto =
        api.getMyProfile()

    suspend fun updateMyProfile(request: UserUpdateDto): UserResponseDto =
        api.updateMyProfile(request)

    suspend fun changePassword(request: ChangePasswordDto): Response<Unit> =
        api.changePassword(request)

    suspend fun deleteMyAccount(): Response<Unit> =
        api.deleteMyAccount()
}