package com.spksh.todoline.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.theme.ExtendedColorScheme
import com.spksh.todoline.ui.theme.extendedDark
import java.time.LocalDateTime

@Composable
fun TaskItem(
    onCheckBox: (Boolean) -> Unit = {},
    onTaskClick: () -> Unit = {},
    currentTime: LocalDateTime = LocalDateTime.MIN,
    task : TaskUiModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick() }
            .padding(end = 8.dp)
    ) {
        Checkbox(
            checked = task.task.progress == task.task.requiredTime,
            onCheckedChange = {onCheckBox(it)},
            enabled = true,
            modifier = Modifier.align(Alignment.Top)
        )
        Column {
            Text(
                text = task.task.name,
                //maxLines = 1
                color = MaterialTheme.colorScheme.onSurface
                //modifier = Modifier.
            )
            task.deadlineText?.let {
                val color = if (currentTime.isBefore(task.deadlineLocal)) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    extendedDark.quadrant1.colorContainer
                }
                Text(
                    text = it,
                    //maxLines = 1
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
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