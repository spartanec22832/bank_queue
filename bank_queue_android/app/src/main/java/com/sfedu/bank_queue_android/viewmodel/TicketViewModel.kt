package com.sfedu.bank_queue_android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketRepo: TicketRepository
) : ViewModel() {

    var tickets by mutableStateOf<List<Ticket>>(emptyList())
        private set

    var selected by mutableStateOf<Ticket?>(null)
        private set

    var isLoadingList by mutableStateOf(false)
        private set

    var isLoadingDetail by mutableStateOf(false)
        private set

    var isProcessing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Загрузка списка
    fun loadTickets() {
        viewModelScope.launch {
            isLoadingList = true
            runCatching { ticketRepo.getAll() }
                .onSuccess { tickets = it }
                .onFailure { errorMessage = it.message }
            isLoadingList = false
        }
    }

    // Загрузка деталей
    fun loadDetail(id: Int) {
        viewModelScope.launch {
            isLoadingDetail = true
            runCatching { ticketRepo.getById(id) }
                .onSuccess { selected = it }
                .onFailure { errorMessage = it.message }
            isLoadingDetail = false
        }
    }

    // Создание
    fun create(
        address: String,
        ticketType: String,
        scheduledAt: String,
        onResult: (Result<Ticket>) -> Unit
    ) {
        viewModelScope.launch {
            isProcessing = true
            val res = ticketRepo.create(address, ticketType, scheduledAt)
            onResult(res)
            isProcessing = false
        }
    }

    // Обновление
    fun update(
        id: Int,
        address: String,
        ticketType: String,
        scheduledAt: String,
        onResult: (Result<Ticket>) -> Unit
    ) {
        viewModelScope.launch {
            isProcessing = true
            val res = ticketRepo.update(id, address, ticketType, scheduledAt)
            onResult(res)
            isProcessing = false
        }
    }

    // Удаление
    fun delete(
        id: Int,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            isProcessing = true
            val res = ticketRepo.delete(id)
            onResult(res)
            isProcessing = false
        }
    }
}