package com.spksh.todoline.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spksh.todoline.R
import com.spksh.todoline.data.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter



@Composable
fun MatrixScreen(
    //tasks: List<Task> = emptyList(),
    tasks_1: List<Task> = emptyList(),
    tasks_2: List<Task> = emptyList(),
    tasks_3: List<Task> = emptyList(),
    tasks_4: List<Task> = emptyList(),
    onCheckBox: (Task, Boolean) -> Unit = {_,_->},
    onTodoClick: (Int) -> Unit = {},
    onAddButton: () -> Unit = {},
) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Log.i("mytag", "matrix screen")
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.todoline_matrix),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.tags),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription =  stringResource(R.string.more_options),
                    )
                }

            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Quadrant(
                        data = tasks_1,
                        name = "Important & Urgent",
                        onCheckBox = onCheckBox,
                        onTodoClick = onTodoClick,
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        data = tasks_2,
                        name = "Important & Not Urgent",
                        onCheckBox = onCheckBox,
                        onTodoClick = onTodoClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Quadrant(
                        data = tasks_3,
                        name = "Unimportant & Urgent",
                        onCheckBox = onCheckBox,
                        onTodoClick = onTodoClick,
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        data = tasks_4,
                        name = "Unimportant & Not Urgent",
                        onCheckBox = onCheckBox,
                        onTodoClick = onTodoClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = onAddButton,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Task")
        }
    }
}

@Composable
fun Quadrant(
    data: List<Task> = emptyList(),
    name: String = "",
    onCheckBox: (Task, Boolean) -> Unit = {_,_->},
    onTodoClick: (Int) -> Unit = {},
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall
        )
        LazyColumn {
            items(data) { data ->
                ToDoItem(
                    onCheckBox = onCheckBox,
                    onTodoClick = onTodoClick,
                    task = data
                )
            }
        }
    }
}

@Composable
fun ToDoItem(
    onCheckBox: (Task, Boolean) -> Unit = {_,_->},
    onTodoClick: (Int) -> Unit = {},
    task : Task
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTodoClick(task.id) }
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = {onCheckBox(task, it)},
            modifier = Modifier.align(Alignment.Top)
        )
        Column {
            Text(
                text = task.name,
                //maxLines = 1
            )
            Text(
                text = task.deadline?.let {
                    convertToLocalTime(it, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("MMM dd HH:mm"))
                } ?: "",
                //maxLines = 1
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

fun convertToLocalTime(deadline: Instant, zoneId: ZoneId): LocalDateTime {
    return deadline.atZone(zoneId).toLocalDateTime()
}

@Preview(showBackground = true)
@Composable
fun ToDoItemPreview() {
    ToDoItem(task = Task(
        name = "Name",
        deadline = Instant.now()
    ))
}
@Preview
@Composable
fun MatrixScreenPreview() {
    MatrixScreen()
}

