package com.sfedu.bank_queue_android.ui.ticket

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sfedu.bank_queue_android.viewmodel.TicketViewModel
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TicketDetailScreen(
    id: Int,
    nav: NavController,
    vm: TicketViewModel = hiltViewModel()
) {
    // 1) загружаем детали при старте
    LaunchedEffect(id) {
        vm.loadDetail(id)
    }
    // ждём пока детали грузятся
    if (vm.isLoadingDetail) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val ticket = vm.selected ?: return

    // состояния формы
    var address by rememberSaveable { mutableStateOf(ticket.address) }
    var type    by rememberSaveable { mutableStateOf(ticket.ticketType) }

    val zoneMoscow = ZoneId.of("Europe/Moscow")
    // распарсим inbound ISO и переведём в мск-зону
    val odt0 = OffsetDateTime.parse(ticket.scheduledAt.toString())
        .atZoneSameInstant(zoneMoscow)
    var date by rememberSaveable { mutableStateOf(odt0.toLocalDate()) }
    var time by rememberSaveable {
        mutableStateOf(odt0.toLocalTime().truncatedTo(ChronoUnit.MINUTES))
    }

    var expandedAddress by remember { mutableStateOf(false) }
    var expandedType    by remember { mutableStateOf(false) }
    var showDatePicker  by remember { mutableStateOf(false) }
    var showTimePicker  by remember { mutableStateOf(false) }
    var error           by remember { mutableStateOf<String?>(null) }
    var isProcessing    by remember { mutableStateOf(false) }

    // автоскрытие ошибки
    LaunchedEffect(error) {
        if (error != null) {
            delay(5_000)
            error = null
        }
    }

    // пересобираем ISO-строку из date/time
    val scheduledAtIso = remember(date, time) {
        ZonedDateTime.of(date, time, zoneMoscow)
            .toOffsetDateTime()
            .truncatedTo(ChronoUnit.MINUTES)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // заголовок + кнопка назад
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(8.dp))
            Text("Тикет № ${ticket.ticket}", style = MaterialTheme.typography.titleLarge)
        }

        // 1) Address — ровно как в Create
        ExposedDropdownMenuBox(
            expanded = expandedAddress,
            onExpandedChange = { expandedAddress = !expandedAddress }
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { /* readOnly */ },
                label = { Text("Адрес отделения") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAddress) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedAddress,
                onDismissRequest = { expandedAddress = false }
            ) {
                listOf("пр. М.Нагибина, 32А", "пр. Соколова, 62", "пр.Буденновский, 97")
                    .forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                address = option
                                expandedAddress = false
                            }
                        )
                    }
            }
        }

        // 2) Type — тоже как в Create
        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = !expandedType }
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { /* readOnly */ },
                label = { Text("Тип тикета") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedType,
                onDismissRequest = { expandedType = false }
            ) {
                listOf("Вклад","Кредит","Карты","Инвестиции","Счета")
                    .forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                type = option
                                expandedType = false
                            }
                        )
                    }
            }
        }

        val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
        // 3) Date — Box + Spacer
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date.format(dateFormatter),
                onValueChange = { },
                label = { Text("Дата приёма") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                Modifier
                    .matchParentSize()
                    .zIndex(1f)
                    .background(Color.Transparent)
                    .clickable { showDatePicker = true }
            )
        }

        val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
        // 4) Time — аналогично
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = time.format(timeFormatter),
                onValueChange = { },
                label = { Text("Время приёма") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                Modifier
                    .matchParentSize()
                    .zIndex(1f)
                    .background(Color.Transparent)
                    .clickable { showTimePicker = true }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Update
        Button(
            onClick = {
                error = null
                isProcessing = true
                vm.update(
                    id          = ticket.id!!.toInt(),
                    address     = address,
                    ticketType  = type,
                    scheduledAt = scheduledAtIso
                ) { result ->
                    result.fold(
                        onSuccess = { nav.popBackStack() },
                        onFailure = { error = "Ошибка обновления тикета" }
                    )
                    isProcessing = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled  = !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Редактировать")
            }
        }

        // Delete
        Button(
            onClick = {
                vm.delete(ticket.id!!.toInt()) { res ->
                    res.fold(
                        onSuccess = { nav.popBackStack() },
                        onFailure = { error = "Ошибка удаления тикета" }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Удалить", color = MaterialTheme.colorScheme.onError)
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }

    // DatePickerDialog / TimePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            initial        = date,
            onDismiss      = { showDatePicker = false },
            onDateSelected = { date = it; showDatePicker = false }
        )
    }
    if (showTimePicker) {
        TimeSpinnerPickerDialog(
            initial        = time,
            onDismiss      = { showTimePicker = false },
            onTimeSelected = { sel ->
                if (sel in LocalTime.of(8,0)..LocalTime.of(17,0)) {
                    time = sel; showTimePicker = false
                } else {
                    error = "Запись только между 08:00 и 17:00"
                }
            }
        )
    }
}
