package com.spksh.todoline.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.spksh.todoline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequiredTimePicker(
    requiredTime: Int = 0,
    onTimeSelected: (Int) -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    val strTime = "${requiredTime / 60}:${
        if (requiredTime % 60 < 10) 
            "0${requiredTime % 60}" 
        else
            requiredTime % 60     
    }"
    TextButton(
        onClick = {showDialog = true}
    ) {
        Text(if (requiredTime == 0) stringResource(R.string.choose_time) else strTime)
    }
    if (showDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = requiredTime / 60,
            initialMinute = requiredTime % 60,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.choose_required_time)) },
            text = {
                TimeInput(state = timePickerState)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onTimeSelected(60 * timePickerState.hour + timePickerState.minute)
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}