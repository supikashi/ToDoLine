package com.spksh.todoline.ui.screens


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spksh.todoline.ui.MainViewModel
import com.spksh.todoline.R
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.components.TagItem
import com.spksh.todoline.ui.components.TaskItem
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.theme.extendedDark
import java.time.LocalDateTime

@Composable
fun MatrixScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var tagsExpanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Log.i("mytag", "matrix screen")
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.todoline_matrix),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                )
                TextButton(
                    onClick = {tagsExpanded = true}
                ) {
                    Text(
                        text = stringResource(R.string.tags),
                        style = MaterialTheme.typography.titleLarge
                    )
                    DropdownMenu(
                        expanded = tagsExpanded,
                        onDismissRequest = {tagsExpanded = false},
                    ) {
                        DropdownMenuItem(
                            text = {
                                TagItem(
                                    tag = Tag(name = "WithoutTags"),
                                    selected = true,
                                    onCheckedChange = {viewModel.ChangeTasksWithoutTagsVisibility(it)},
                                    onDelete = {},
                                )
                            },
                            onClick = { }
                        )
                        uiState.tags.forEach { tag ->
                            DropdownMenuItem(
                                text = {
                                    TagItem(
                                        tag = tag,
                                        selected = tag.show,
                                        onCheckedChange = { viewModel.updateTag(tag.copy(show = it)) },
                                        onDelete = { viewModel.deleteTag(tag) },
                                    )
                                },
                                onClick = { }
                            )
                        }
                    }
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription =  stringResource(R.string.more_options),
                    )
                }
            }
            Matrix(
                tasks_1 = uiState.tasks_1,
                tasks_2 = uiState.tasks_2,
                tasks_3 = uiState.tasks_3,
                tasks_4 = uiState.tasks_4,
                onCheckBox = { task, progress ->
                    viewModel.updateTask(task.task.copy(progress = if (progress) task.task.requiredTime else 0))
                },
                onTaskClick = {viewModel.openTaskScreen(it)},
                currentTime = uiState.currentTime
            )
        }
        FloatingActionButton(
            onClick = { viewModel.addTask() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Task")
        }
    }
}

@Composable
fun Matrix(
    modifier: Modifier = Modifier,
    tasks_1: List<TaskUiModel> = emptyList(),
    tasks_2: List<TaskUiModel> = emptyList(),
    tasks_3: List<TaskUiModel> = emptyList(),
    tasks_4: List<TaskUiModel> = emptyList(),
    onCheckBox: (TaskUiModel, Boolean) -> Unit = { _, _->},
    onTaskClick: (Long) -> Unit = {},
    currentTime: LocalDateTime
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Quadrant(
                tasks = tasks_1,
                name = "Important & Urgent",
                onCheckBox = onCheckBox,
                onTaskClick = onTaskClick,
                modifier = Modifier.weight(1f),
                currentTime = currentTime,
                color = extendedDark.quadrant1.colorContainer
            )
            Quadrant(
                tasks = tasks_2,
                name = "Important & Not Urgent",
                onCheckBox = onCheckBox,
                onTaskClick = onTaskClick,
                modifier = Modifier.weight(1f),
                currentTime = currentTime,
                color = extendedDark.quadrant2.colorContainer
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Quadrant(
                tasks = tasks_3,
                name = "Unimportant & Urgent",
                onCheckBox = onCheckBox,
                onTaskClick = onTaskClick,
                modifier = Modifier.weight(1f),
                currentTime = currentTime,
                color = extendedDark.quadrant3.colorContainer
            )
            Quadrant(
                tasks = tasks_4,
                name = "Unimportant & Not Urgent",
                onCheckBox = onCheckBox,
                onTaskClick = onTaskClick,
                modifier = Modifier.weight(1f),
                currentTime = currentTime,
                color = extendedDark.quadrant4.colorContainer
            )
        }
    }
}

@Composable
fun Quadrant(
    modifier: Modifier = Modifier,
    tasks: List<TaskUiModel> = emptyList(),
    name: String = "",
    onCheckBox: (TaskUiModel, Boolean) -> Unit = { _, _->},
    onTaskClick: (Long) -> Unit = {},
    currentTime: LocalDateTime,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        onCheckBox = {onCheckBox(task, it)},
                        onTaskClick = {onTaskClick(task.task.id)},
                        currentTime = currentTime,
                        task = task
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MatrixScreenPreview() {
    MatrixScreen()
}

