package com.sfedu.bank_queue_android.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.sfedu.bank_queue_android.mapper.toDomain
import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.network.RemoteDataSource
import com.sfedu.bank_queue_android.network.dto.TicketCreateDto
import com.sfedu.bank_queue_android.network.dto.TicketUpdateDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepositoryImpl @Inject constructor(
    private val remote: RemoteDataSource
) : TicketRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAll(): List<Ticket> =
        remote.getMyTickets().map { it.toDomain() }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getById(id: Int): Ticket =
        remote.getTicket(id).toDomain()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun create(address: String, ticketType:String, scheduledAt: String): Result<Ticket> = runCatching {
        val dto = TicketCreateDto(address, ticketType, scheduledAt)
        remote.createTicket(dto).toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun update(id: Int, address: String, ticketType: String, scheduledAt: String): Result<Ticket> = runCatching {
        val dto = TicketUpdateDto(address, ticketType, scheduledAt)
        remote.updateTicket(id.toLong(), dto).toDomain()
    }


    override suspend fun delete(id: Int): Result<Unit> =
        runCatching { remote.deleteTicket(id.toLong()) }
}