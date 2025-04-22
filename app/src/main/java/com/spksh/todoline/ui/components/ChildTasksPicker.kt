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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spksh.todoline.R
import com.spksh.todoline.ui.model.TaskUiModel


@Composable
fun ChildTasksPicker(
    tasks: List<TaskUiModel> = emptyList(),
    childTasksIds: List<Long> = emptyList(),
    onCreateTask: () -> Unit = {},
    onTaskClick: (Long) -> Unit = {},
    onCheckBox: (TaskUiModel, Boolean) -> Unit = { _, _ -> },
) {
    var showDialog by remember { mutableStateOf(false) }
    TextButton(
        onClick = {showDialog = true}
    ) {
        Text(
            if (childTasksIds.isEmpty())
                stringResource(R.string.create_dependent_tasks)
            else
                "${childTasksIds.size} ${stringResource(R.string.dependent_tasks)}"
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.dependent_tasks)) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(0.dp, 200.dp)) {
                    item {
                        TextButton(
                            onClick = {
                                showDialog = false
                                onCreateTask()
                            },
                        ) {
                            Text(stringResource(R.string.create_new))
                        }
                    }
                    items(tasks.filter { it.id in childTasksIds }) { task ->
                        TaskItem(
                            task = task,
                            onCheckBox = {onCheckBox(task, it)},
                            onTaskClick = {
                                showDialog = false
                                onTaskClick(task.id)
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