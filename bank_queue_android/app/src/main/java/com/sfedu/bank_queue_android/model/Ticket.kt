package com.sfedu.bank_queue_android.model

import java.time.OffsetDateTime

data class Ticket(
    val id: Long?,
    val userId: Long,
    val address: String,
    val ticketType: String,
    val ticket: String,
    val scheduledAt: OffsetDateTime
    //TODO в backend: val status: String по типу open/closed
)