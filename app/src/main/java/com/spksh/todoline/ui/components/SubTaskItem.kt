package com.spksh.todoline.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spksh.todoline.data.Task.SubTask

@Composable
fun SubTaskItem(
    subTask: SubTask,
    onCheckedChange: (Boolean) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onRequiredTimeSelected: (Int) -> Unit = {},
    onProgressSelected: (Int) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(subTask.name) }
    var showRequiredTimeDialog by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    val strRequiredTime = "${subTask.requiredTime / 60}:${
        if (subTask.requiredTime % 60 < 10)
            "0${subTask.requiredTime % 60}"
        else
            subTask.requiredTime % 60
    }"
    val strProgress = "${subTask.progress / 60}:${
        if (subTask.progress % 60 < 10)
            "0${subTask.progress % 60}"
        else
            subTask.progress % 60
    }"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(end = 8.dp)
    ) {
        Checkbox(
            checked = subTask.progress == subTask.requiredTime,
            onCheckedChange = { check ->
                onCheckedChange(check)
            },
        )
        TextField(
            value = name,
            onValueChange = { newName ->
                name = newName
                onNameChange(newName)
            },
            placeholder = {Text("Subtask Name")},
            singleLine = true,
            //suffix = {Text(" $strTime")},
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
        )
        Text(text = "$strProgress/$strRequiredTime")
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false},
            ) {
                DropdownMenuItem(
                    text = { Text("Change Progress") },
                    onClick = {
                        expanded = false
                        showProgressDialog = true
                    }
                )
                DropdownMenuItem(
                    text = { Text("Change Required Time") },
                    onClick = {
                        expanded = false
                        showRequiredTimeDialog = true
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDelete()
                    }
                )
            }
        }
    }
    if (showProgressDialog) {
        TimePickerDialog(
            initialTime = subTask.progress,
            title = "Choose Progress",
            onDismiss = {showProgressDialog = false},
            onTimeSelected = {onProgressSelected(it)}
        )
    }
    if (showRequiredTimeDialog) {
        TimePickerDialog(
            initialTime = subTask.progress,
            title = "Choose Required Time",
            onDismiss = {showRequiredTimeDialog = false},
            onTimeSelected = {onRequiredTimeSelected(it)}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: Int = 0,
    title: String = "",
    onDismiss: () -> Unit = {},
    onTimeSelected: (Int) -> Unit = {},
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime / 60,
        initialMinute = initialTime % 60,
        is24Hour = true,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            TimeInput(state = timePickerState)
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onTimeSelected(60 * timePickerState.hour + timePickerState.minute)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SubTaskItemPreview() {
    SubTaskItem(
        subTask = SubTask()
    )
}