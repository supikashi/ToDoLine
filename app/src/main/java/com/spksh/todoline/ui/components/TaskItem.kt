package com.spksh.todoline.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
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
    task : TaskUiModel,
    onDragStart: (TaskUiModel, Offset) -> Unit = {_,_->},
    onDrag: (PointerInputChange, Offset) -> Unit = {_,_->},
    onDragEnd: () -> Unit = {},
    draggedItem: TaskUiModel? = null
) {
    var elementOffset by remember { mutableStateOf<Offset>(Offset(0f, 0f)) }
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart(task, elementOffset)
                    },
                    onDrag = { change, dragAmount ->
                        onDrag(change, dragAmount)
                    },
                    onDragEnd = {
                        onDragEnd()
                    }
                )
            }
            .alpha(if (draggedItem?.id == task.id) 0.0f else 1f)
            .onGloballyPositioned { coordinates ->
                elementOffset = coordinates.positionInWindow()
                elementOffset = elementOffset.copy(y = elementOffset.y - 200f)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTaskClick() }
                .padding(end = 8.dp)
        ) {
            Checkbox(
                checked = task.progress == task.requiredTime,
                onCheckedChange = {onCheckBox(it)},
                enabled = true,
                modifier = Modifier.align(Alignment.Top)
            )
            Column {
                Text(
                    text = task.name,
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
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    TaskItem(task = TaskUiModel(
        name = "Name",
        deadlineText = "Apr 14 2025 14:25"
    )
    )
}