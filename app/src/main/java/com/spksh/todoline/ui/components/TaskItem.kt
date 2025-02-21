package com.spksh.todoline.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.spksh.todoline.TaskUiModel
import com.spksh.todoline.data.Task

@Composable
fun TaskItem(
    onCheckBox: (Boolean) -> Unit = {},
    onTodoClick: () -> Unit = {},
    task : TaskUiModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTodoClick() }
    ) {
        Checkbox(
            checked = task.task.progress == 1f,
            onCheckedChange = {onCheckBox(it)},
            modifier = Modifier.align(Alignment.Top)
        )
        Column {
            Text(
                text = task.task.name,
                //maxLines = 1
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = task.deadlineText ?: "",
                //maxLines = 1
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    TaskItem(task = TaskUiModel(
        task = Task(
            name = "Name"
        ),
        deadlineText = "Apr 14 2025 14:25"
    )
    )
}