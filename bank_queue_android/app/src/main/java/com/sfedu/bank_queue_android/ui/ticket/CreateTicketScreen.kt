package com.sfedu.bank_queue_android.ui.ticket

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Build
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.sfedu.bank_queue_android.viewmodel.TicketViewModel
import com.sfedu.bank_queue_android.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTicketScreen(
    ticketVm: TicketViewModel = hiltViewModel(),
    userVm: UserViewModel = hiltViewModel(),
    onCreated: () -> Unit
) {
    val token = userVm.token
    if (token.isNullOrBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Вы не авторизованы", textAlign = TextAlign.Center)
        }
        return
    }

    // Списки опций
    val addresses = listOf(
        "пр. М.Нагибина, 32А",
        "пр. Соколова, 62",
        "пр.Буденновский, 97"
    )
    val types = listOf("Вклад", "Кредит", "Карты", "Инвестиции", "Счета")

    // Состояния
    var address by rememberSaveable { mutableStateOf(addresses.first()) }
    var type    by rememberSaveable { mutableStateOf(types.first()) }

    val zoneMoscow = ZoneId.of("Europe/Moscow")
    var date by rememberSaveable {
        mutableStateOf(LocalDate.now(zoneMoscow))
    }
    var time by rememberSaveable {
        mutableStateOf(
            LocalTime.now(zoneMoscow)
                .truncatedTo(ChronoUnit.MINUTES)
                .coerceIn(LocalTime.of(8, 0), LocalTime.of(17, 0))
        )
    }

    var expandedAddress by remember { mutableStateOf(false) }
    var expandedType    by remember { mutableStateOf(false) }
    var showDatePicker  by remember { mutableStateOf(false) }
    var showTimePicker  by remember { mutableStateOf(false) }
    var error           by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(error) {
        if (error != null) {
            // ждём 5 секунд
            delay(5_000L)
            // сбрасываем ошибку
            error = null
        }
    }

    val isProcessing = ticketVm.isProcessing

    val scheduledAtIso = remember(date, time) {
        ZonedDateTime
            .of(date, time, zoneMoscow)
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
        Text("Создать тикет", style = MaterialTheme.typography.titleLarge)

        // 1) Address — выпадающий список
        ExposedDropdownMenuBox(
            expanded = expandedAddress,
            onExpandedChange = { expandedAddress = !expandedAddress }
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { },
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
                addresses.forEach { option ->
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

        // 2) Ticket Type — тоже выпадающий список
        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = !expandedType }
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { },
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
                types.forEach { option ->
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

        // 3) Scheduled Date
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = date.format(dateFormatter),
                onValueChange = { },
                label = { Text("Дата приема") },
                readOnly = true,
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                Modifier
                    .matchParentSize()
                    // чтобы он точно лежал над TextField
                    .zIndex(1f)
                    // и пропускал фоновый ripple, если нужно
                    .background(Color.Transparent)
                    .clickable { showDatePicker = true }
            )
        }

        val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

        // 4) Scheduled Time
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value    = time.format(timeFormatter),
                onValueChange = {},
                label    = { Text("Время приёма") },
                readOnly = true,
                enabled  = true,
                modifier = Modifier.fillMaxWidth()
            )
            // Этот Spacer лежит поверх TextField и перехватит нажатие
            Spacer(
                Modifier
                    .matchParentSize()
                    // чтобы он точно лежал над TextField
                    .zIndex(1f)
                    // и пропускал фоновый ripple, если нужно
                    .background(Color.Transparent)
                    .clickable { showTimePicker = true }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Кнопка Create
        Button(
            onClick = {
                error = null
                ticketVm.create(address, type, scheduledAtIso) { result ->
                    result.fold(
                        onSuccess = { onCreated() },
                        onFailure = { error = "Ошибка создания тикета"}
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = address.isNotBlank() && type.isNotBlank() && !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
                Text("Создание…")
            } else {
                Text("Создать")
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }

    // Диалоги выбора
    if (showDatePicker) {
        DatePickerDialog(
            initial = date,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                date = it
                showDatePicker = false
            }
        )
    }
    if (showTimePicker) {
        TimeSpinnerPickerDialog(
            initial = time,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { selected ->
                // если вышло за рамки — игнорируем или показываем ошибку
                if (selected in LocalTime.of(8,0)..LocalTime.of(17,0)) {
                    time = selected
                    showTimePicker = false
                } else {
                    error = "Запись только между 08:00 и 17:00"
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    initial: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val dlg = android.app.DatePickerDialog(
            context,
            { _, y, m, d -> onDateSelected(LocalDate.of(y, m + 1, d)) },
            initial.year, initial.monthValue - 1, initial.dayOfMonth
        )
        dlg.setOnDismissListener { onDismiss() }
        dlg.show()
        onDispose { dlg.dismiss() }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSpinnerPickerDialog(
    initial: LocalTime,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        // 1) создаём диалог без слушателя
        val dlg = android.app.TimePickerDialog(
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            null,
            initial.hour,
            initial.minute / 15,
            true
        )

        // 2) подменяем спиннер минут и перехватываем OK
        dlg.setOnShowListener {
            // a) находим внутренний TimePicker
            @Suppress("DiscouragedPrivateApi")
            val timePickerId = Resources.getSystem()
                .getIdentifier("timePicker", "id", "android")
            val tp = dlg.findViewById<TimePicker>(timePickerId)!!

            // b) находим NumberPicker минут
            @Suppress("DiscouragedPrivateApi")
            val minutePickerId = Resources.getSystem()
                .getIdentifier("minute", "id", "android")
            val minutePicker = tp.findViewById<NumberPicker>(minutePickerId)!!

            // c) настраиваем шаг 15 минут
            minutePicker.minValue = 0
            minutePicker.maxValue = 3
            minutePicker.displayedValues = arrayOf("00","15","30","45")
            minutePicker.wrapSelectorWheel = true

            // d) перехватываем нажатие кнопки OK
            //    здесь используем android.app.AlertDialog.BUTTON_POSITIVE
            dlg.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val minute = minutePicker.value * 15
                    onTimeSelected(LocalTime.of(tp.hour, minute))
                    dlg.dismiss()
                }
        }

        dlg.setOnDismissListener { onDismiss() }
        dlg.show()
        onDispose { dlg.dismiss() }
    }
}