package com.spksh.todoline.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spksh.todoline.TaskUiModel


@Composable
fun ChildTasksPicker(
    tasks: List<TaskUiModel> = emptyList(),
    childTasksIds: List<Long> = emptyList(),
    onCreateTask: () -> Unit = {},
    onTaskClick: (Long) -> Unit = {},
    onCheckBox: (TaskUiModel, Boolean) -> Unit = {_,_ -> },
) {
    var showDialog by remember { mutableStateOf(false) }
    TextButton(
        onClick = {showDialog = true}
    ) {
        Text(
            if (childTasksIds.isEmpty())
                "Create Child Tasks"
            else
                "${childTasksIds.size} Child Tasks"
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Child Tasks") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(0.dp, 200.dp)) {
                    item {
                        TextButton(
                            onClick = {
                                showDialog = false
                                onCreateTask()
                            },
                        ) {
                            Text("Create New")
                        }
                    }
                    items(tasks.filter { it.task.id in childTasksIds }) { task ->
                        TaskItem(
                            task = task,
                            onCheckBox = {onCheckBox(task, it)},
                            onTaskClick = {
                                showDialog = false
                                onTaskClick(task.task.id)
                            },
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}