package com.sfedu.bank_queue_android.network.dto

data class TicketCreateDto(
    val address: String,
    val ticketType: String,
    val scheduledAt: String
)

data class TicketUpdateDto(
    val address: String? = null,
    val ticketType: String? = null,
    val scheduledAt: String? = null
)

data class TicketResponseDto(
    val id: Long?,
    val userId: Long,
    val address: String,
    val ticketType: String,
    val ticket: String,
    val scheduledAt: String
)