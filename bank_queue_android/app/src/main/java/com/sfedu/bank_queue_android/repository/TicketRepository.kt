package com.sfedu.bank_queue_android.repository

import com.sfedu.bank_queue_android.model.Ticket

/** Интерфейс для CRUD-операций с тикетами */
interface TicketRepository {
    suspend fun getAll(): List<Ticket>
    suspend fun getById(id: Int): Ticket
    suspend fun create(address: String, ticketType: String, scheduledAt: String): Result<Ticket>
    suspend fun update(id: Int, address: String, ticketType: String, scheduledAt: String): Result<Ticket>
    suspend fun delete(id: Int): Result<Unit>
}