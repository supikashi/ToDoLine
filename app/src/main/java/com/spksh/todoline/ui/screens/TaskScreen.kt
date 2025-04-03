package com.spksh.todoline.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.spksh.todoline.data.Task.SubTask
import com.spksh.todoline.ui.components.ChildTasksPicker
import com.spksh.todoline.ui.components.DateTimePicker
import com.spksh.todoline.ui.components.RequiredTimePicker
import com.spksh.todoline.ui.components.SubTaskItem
import com.spksh.todoline.ui.components.TagPicker

@Composable
fun TaskScreen(
    taskId: Long = 0,
    viewModel: MainViewModel = viewModel(),
) {
    Log.i("mytag", "todo screen")
    //var task by remember { mutableStateOf(viewModel.findTaskById(taskId)) }
    val task = viewModel.findTaskById(taskId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //LaunchedEffect(id) {  }
    var expanded by remember { mutableStateOf(false) }
    var importance by remember { mutableFloatStateOf((task?.task?.importance ?: 1).toFloat()) }
    var urgency by remember { mutableFloatStateOf((task?.task?.urgency ?: 1).toFloat()) }
    var progress by remember { mutableFloatStateOf((task?.task?.progress ?: 0).toFloat()) }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 8.dp)
        ) {
            IconButton(onClick = {viewModel.popBackStack()}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_matrix_screen)
                )
            }
            IconButton(onClick = {expanded = true}) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_options)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false},
                ) {
                    DropdownMenuItem(
                        text = {Text("Delete")},
                        onClick = {
                            expanded = false
                            viewModel.popBackStack()
                            task?.let {
                                viewModel.deleteTask(it)
                            }
                        }
                    )
                }
            }
        }
        var name by remember { mutableStateOf(task?.task?.name ?: "") }
        TextField(
            value = name,
            onValueChange = { newName ->
                task?.let {
                    Log.i("mytag", "name changed")
                    viewModel.updateTask(it.task.copy(name = newName))
                    name = newName
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            placeholder = {Text("Task")},
            modifier = Modifier.fillMaxWidth()
        )
        var description by remember { mutableStateOf(task?.task?.description ?: "") }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ){
            item {
                TextField(
                    value = description,
                    onValueChange = { newDescription ->
                        task?.let {
                            Log.i("mytag", "description changed")
                            viewModel.updateTask(it.task.copy(description = newDescription))
                            description = newDescription
                        }
                    },
                    singleLine = false,

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    placeholder = {Text("Description")},
                    //modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            task?.let {
                items(
                    count = it.task.subTasks.size,
                    key = {index -> it.task.subTasks[index].id}
                ) { index ->
                    HorizontalDivider()
                    SubTaskItem(
                        subTask = it.task.subTasks[index],
                        onCheckedChange = { check ->
                            viewModel.updateTask(it.task.copy(
                                subTasks = it.task.subTasks.mapIndexed { i, subTask ->
                                    if (i == index) {
                                        subTask.copy(progress = if (check) subTask.requiredTime else 0)
                                    } else {
                                        subTask
                                    }
                                }
                            ))
                        },
                        onNameChange = { name ->
                            viewModel.updateTask(it.task.copy(subTasks = it.task.subTasks.mapIndexed { i, subTask ->
                                if (i == index) {
                                    subTask.copy(name = name)
                                } else {
                                    subTask
                                }
                            }))
                        },
                        onRequiredTimeSelected = { time ->
                            viewModel.updateTask(it.task.copy(
                                subTasks = it.task.subTasks.mapIndexed { i, subTask ->
                                    if (i == index) {
                                        subTask.copy(
                                            requiredTime = time,
                                            progress = subTask.progress.coerceAtMost(time)
                                        )
                                    } else {
                                        subTask
                                    }
                                }
                            ))
                        },
                        onProgressSelected = { time ->
                            viewModel.updateTask(it.task.copy(
                                subTasks = it.task.subTasks.mapIndexed { i, subTask ->
                                    if (i == index) {
                                        subTask.copy(
                                            progress = time.coerceAtMost(subTask.requiredTime)
                                        )
                                    } else {
                                        subTask
                                    }
                                }
                            ))
                        },
                        onDelete = {
                            viewModel.updateTask(it.task.copy(
                                subTasks = it.task.subTasks.mapIndexedNotNull { i, subTask ->
                                    if (i == index) {
                                        null
                                    } else {
                                        subTask
                                    }
                                }
                            ))
                        }
                    )
                }
            }
        }
        TextButton(
            onClick = {
                task?.let {
                    val id = if (it.task.subTasks.isNotEmpty()) it.task.subTasks.maxOf { it.id } + 1 else 1
                    viewModel.updateTask(it.task.copy(
                        subTasks = it.task.subTasks.plus(SubTask(id = id))
                    ))
                }
            }
        ) {
            Text("Add Subtask")
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Task Parameters",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text("Importance")
            Slider(
                value = importance,
                onValueChange = { importance = it },
                onValueChangeFinished = {
                    task?.let {
                        Log.i("mytag", "importance changed")
                        viewModel.updateTask(it.task.copy(importance = importance.toInt()))
                    }
                },
                valueRange = 1f..10f,
                steps = 8,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Urgent Task")
                Switch(
                    checked = (task?.task?.urgency ?: 0) > 5,
                    onCheckedChange = { check ->
                        task?.let {
                            viewModel.updateTask(it.task.copy(urgency = if (check) 10 else 1))
                        }
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deadline")
                DateTimePicker(
                    deadline = task?.deadlineText,
                    onDeadlineSelected = { newDeadline ->
                        task?.let {
                            Log.i("mytag", "deadline changed")
                            viewModel.updateTask(it.task.copy(deadline = viewModel.toRightZone(newDeadline)))
                        }
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Time Required")
                RequiredTimePicker(
                    requiredTime = task?.task?.requiredTime ?: 0,
                    onTimeSelected = { newTime ->
                        task?.let {
                            viewModel.updateTask(it.task.copy(requiredTime = newTime))
                        }
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            )  {
                Text(
                    text = "Tags",
                    modifier = Modifier.width(100.dp)
                )
                TagPicker(
                    tags = uiState.tags,
                    selectedTagsIds = task?.task?.tagsIds ?: emptyList(),
                    onDismiss = {},
                    onTagSelected = { tag, selected ->
                        task?.let {
                            if (selected) {
                                val newTask = it.task.copy(tagsIds = it.task.tagsIds.plus(tag.id))
                                viewModel.updateTask(newTask)
                            } else {
                                val newTask = it.task.copy(tagsIds = it.task.tagsIds.minus(tag.id))
                                viewModel.updateTask(newTask)
                            }
                        }
                    },
                    onTagCreated = { tag ->
                        task?.let {
                            viewModel.addTag(tag, it)
                        }
                    },
                    onDeleted = { tag ->
                        task?.let {
                            viewModel.deleteTag(tag)
                        }
                    },
                )
            }
            task?.task?.parentTaskId?.let { parentId ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                )  {
                    Text(text = "Parent Task")
                    TextButton(
                        onClick = {
                            viewModel.openTaskScreen(parentId)
                        }
                    ) {
                        Text("Go To Parent Task")
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            )  {
                Text(text = "Child Tasks")
                ChildTasksPicker(
                    tasks = uiState.tasks,
                    childTasksIds = task?.task?.childTasksIds ?: emptyList(),
                    onCreateTask = {
                        task?.let {
                            viewModel.addChildTask(it)
                        }
                    },
                    onCheckBox = { childTask, progress ->
                        viewModel.updateTask(childTask.task.copy(progress = if (progress) childTask.task.requiredTime else 0))
                    },
                    onTaskClick = { id ->
                        viewModel.openTaskScreen(id)
                    },
                )
            }
            Text("Progress")
            Slider(
                value = progress,
                onValueChange = { progress = it },
                onValueChangeFinished = {
                    task?.let {
                        Log.i("mytag", "progress changed")
                        viewModel.updateTask(it.task.copy(progress = progress.toInt()))
                    }
                },
                valueRange = 0f..(task?.task?.requiredTime ?: 1).toFloat(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    TaskScreen()
}