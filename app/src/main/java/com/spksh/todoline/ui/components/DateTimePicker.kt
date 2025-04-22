package com.spksh.todoline.ui.components

import android.util.Log
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.spksh.todoline.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    deadline: String? = null,
    onDeadlineSelected: (Long?) -> Unit = {}
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var localDateState by remember { mutableStateOf<Long?>(null) }
    TextButton(
        onClick = {showDatePicker = true}
    ) {
        Text(deadline ?: stringResource(R.string.choose_date))
    }
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { dateState ->
                localDateState = dateState
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
                onDeadlineSelected(localDateState?.plus(1000 * 60 * (timeState.minute + 60 * timeState.hour)))
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
        TimePicker(state = timePickerState)
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
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
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
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}