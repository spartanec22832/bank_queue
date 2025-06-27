package com.sfedu.bank_queue_android.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.network.dto.TicketCreateDto
import com.sfedu.bank_queue_android.network.dto.TicketResponseDto
import com.sfedu.bank_queue_android.network.dto.TicketUpdateDto
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// из ResponseDto в Domain
@RequiresApi(Build.VERSION_CODES.O)
fun TicketResponseDto.toDomain() = Ticket(
     id = this.id,
     userId = this.userId,
     address = this.address,
     ticketType = this.ticketType,
     ticket = this.ticket,
     scheduledAt = OffsetDateTime.parse(this.scheduledAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
)

// из Domain в CreateDto
@RequiresApi(Build.VERSION_CODES.O)
fun Ticket.toCreateDto() = TicketCreateDto(
     address = this.address,
     ticketType = this.ticketType,
     scheduledAt = this.scheduledAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
)

// из Domain в UpdateDto
@RequiresApi(Build.VERSION_CODES.O)
fun Ticket.toUpdateDto() = TicketUpdateDto(
     address = this.address,
     ticketType = this.ticketType,
     scheduledAt = this.scheduledAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
)