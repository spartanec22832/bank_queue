package com.sfedu.bank_queue_android.ui.ticket

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sfedu.bank_queue_android.viewmodel.TicketViewModel
import com.sfedu.bank_queue_android.viewmodel.UserViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TicketListScreen(
    ticketVm: TicketViewModel = hiltViewModel(),
    userVm: UserViewModel = hiltViewModel(),
    onClick: (Int) -> Unit
) {
    val token = userVm.token
    if (token.isNullOrBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Вы не авторизованы", textAlign = TextAlign.Center)
        }
        return
    }

    val tickets = ticketVm.tickets
    LaunchedEffect(Unit) { ticketVm.loadTickets() }

    if (tickets.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("У вас нет тикетов", textAlign = TextAlign.Center)
        }
        return
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val zoneMoscow   = ZoneId.of("Europe/Moscow")

    LazyColumn(Modifier.fillMaxSize()) {
        items(tickets) { t ->
            val odt = runCatching { OffsetDateTime.parse(t.scheduledAt.toString()) }
                .getOrNull()
                ?: OffsetDateTime.now(ZoneOffset.UTC)
            val mskZdt = odt.atZoneSameInstant(zoneMoscow)

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onClick(t.id!!.toInt()) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("№ ${t.ticket}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("Тип операции: ${t.ticketType}", style = MaterialTheme.typography.bodyMedium)
                    Text("Адрес отделения: ${t.address}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Дата: ${mskZdt.toLocalDate().format(dateFormatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Время: ${mskZdt.toLocalTime().format(timeFormatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}