package com.spksh.todoline.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun convertToUtc(localDateTime: LocalDateTime, zoneId: ZoneId): Instant {
    return localDateTime.atZone(zoneId).toInstant()
}

fun convertToLocalTime(deadline: Instant, zoneId: ZoneId): LocalDateTime {
    return deadline.atZone(zoneId).toLocalDateTime()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    deadline: Instant? = null,
    onDeadlineSelected: (Instant?) -> Unit = {}
) {
    val zoneId = ZoneId.systemDefault()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var localDateState by remember { mutableStateOf<LocalDateTime?>(null) }
    var text = "Choose Date"
    deadline?.let {
        text = convertToLocalTime(it, zoneId)
            .format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm"))
    }
    TextButton(
        onClick = {showDatePicker = true}
    ) {
        Text(text)
    }
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { dateState ->
                localDateState = Instant
                    .ofEpochMilli(dateState?:0)
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime()

                Log.i("mytag", "$localDateState")
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    } else if (showTimePicker) {
        TimePickerWrap(

            onConfirm = { timeState ->
                onDeadlineSelected(convertToUtc(
                    localDateState
                        ?.plusHours(timeState.hour.toLong())
                        ?.plusMinutes(timeState.minute.toLong())
                        ?: LocalDateTime.now(),
                    zoneId
                ))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWrap(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    val currentTime = LocalTime.now()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true,
    )


    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        text = { content() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}