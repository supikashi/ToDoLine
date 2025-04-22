package com.spksh.todoline.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
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
import com.spksh.todoline.ui.theme.extendedDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    taskId: Long = 0,
    viewModel: MainViewModel = viewModel(),
) {
    Log.i("mytag", "todo screen")
    val task = viewModel.findTaskById(taskId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expandedMenu by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf((task?.progress ?: 0).toFloat()) }
    var expandedQuadrantMenu by remember { mutableStateOf(false) }
    val quadrantsNames = listOf(
        stringResource(R.string.important_urgent),
        stringResource(R.string.important_not_urgent),
        stringResource(R.string.unimportant_urgent),
        stringResource(R.string.unimportant_not_urgent))
    val quadrantsColors = listOf(
        extendedDark.quadrant1.colorContainer,
        extendedDark.quadrant2.colorContainer,
        extendedDark.quadrant3.colorContainer,
        extendedDark.quadrant4.colorContainer
    )
    val quadrantsPatams = listOf(
        Pair(10, 10),
        Pair(10, 0),
        Pair(0, 10),
        Pair(0, 0)
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            IconButton(onClick = {viewModel.popBackStack()}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_matrix_screen)
                )
            }
            IconButton(onClick = {expandedMenu = true}) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_options)
                )
                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = {expandedMenu = false},
                ) {
                    DropdownMenuItem(
                        text = {Text("Delete")},
                        onClick = {
                            expandedMenu = false
                            viewModel.popBackStack()
                            task?.let {
                                viewModel.taskFeatures.delete(it)
                            }
                        }
                    )
                }
            }
        }
        var name by remember { mutableStateOf(task?.name ?: "") }
        TextField(
            value = name,
            onValueChange = { newName ->
                task?.let {
                    Log.i("mytag", "name changed")
                    viewModel.taskFeatures.update(it.copy(name = newName))
                    name = newName
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            placeholder = {Text(stringResource(R.string.task))},
            modifier = Modifier.fillMaxWidth()
        )
        var description by remember { mutableStateOf(task?.description ?: "") }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ){
            item {
                TextField(
                    value = description,
                    onValueChange = { newDescription ->
                        task?.let {
                            Log.i("mytag", "description changed")
                            viewModel.taskFeatures.update(it.copy(description = newDescription))
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
                    placeholder = {Text(stringResource(R.string.description))},
                    //modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            task?.let {
                items(
                    count = it.subTasks.size,
                    key = {index -> it.subTasks[index].id}
                ) { index ->
                    HorizontalDivider()
                    SubTaskItem(
                        subTask = it.subTasks[index],
                        onCheckedChange = { check ->
                            viewModel.taskFeatures.update(it.copy(
                                subTasks = it.subTasks.mapIndexed { i, subTask ->
                                    if (i == index) {
                                        subTask.copy(progress = if (check) subTask.requiredTime else 0)
                                    } else {
                                        subTask
                                    }
                                }
                            ))
                        },
                        onNameChange = { name ->
                            viewModel.taskFeatures.update(it.copy(subTasks = it.subTasks.mapIndexed { i, subTask ->
                                if (i == index) {
                                    subTask.copy(name = name)
                                } else {
                                    subTask
                                }
                            }))
                        },
                        onRequiredTimeSelected = { time ->
                            viewModel.taskFeatures.update(it.copy(
                                subTasks = it.subTasks.mapIndexed { i, subTask ->
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
                            viewModel.taskFeatures.update(it.copy(
                                subTasks = it.subTasks.mapIndexed { i, subTask ->
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
                            viewModel.taskFeatures.update(it.copy(
                                subTasks = it.subTasks.mapIndexedNotNull { i, subTask ->
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
                    val id = if (it.subTasks.isNotEmpty()) it.subTasks.maxOf { it.id } + 1 else 1
                    viewModel.taskFeatures.update(it.copy(
                        subTasks = it.subTasks.plus(SubTask(id = id))
                    ))
                }
            }
        ) {
            Text(stringResource(R.string.add_subtask))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.task_parameters),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.quadrant), modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth())
                ExposedDropdownMenuBox(
                    expanded = expandedQuadrantMenu,
                    onExpandedChange = { expandedQuadrantMenu = !expandedQuadrantMenu },
                    modifier = Modifier.width(230.dp)
                ) {
                    var index = 0
                    task?.let {
                        if (it.importance > 5) {
                            if (it.urgency > 5) {
                                index = 0
                            } else {
                                index = 1
                            }
                        } else {
                            if (it.urgency > 5) {
                                index = 2
                            } else {
                                index = 3
                            }
                        }
                    }
                    TextField(
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = quadrantsNames[index],
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedQuadrantMenu) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = TextFieldDefaults.colors(
                            //unfocusedContainerColor = color,
                            //focusedContainerColor = color,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.outline,
                            unfocusedTextColor = MaterialTheme.colorScheme.outline
                        ),
                    )

                    ExposedDropdownMenu(
                        expanded = expandedQuadrantMenu,
                        onDismissRequest = { expandedQuadrantMenu = false }
                    ) {
                        repeat(4) { i ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = quadrantsNames[i],
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                },
                                onClick = {
                                    task?.let {
                                        viewModel.taskFeatures.update(it.copy(
                                            importance = quadrantsPatams[i].first,
                                            urgency = quadrantsPatams[i].second
                                        ))
                                    }
                                    expandedQuadrantMenu = false
                                }
                            )
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.deadline))
                DateTimePicker(
                    deadline = task?.deadlineText,
                    onDeadlineSelected = { newDeadline ->
                        task?.let {
                            Log.i("mytag", "deadline changed")
                            viewModel.taskFeatures.update(it.copy(deadline = viewModel.toRightZone(newDeadline)))
                        }
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.time_required))
                RequiredTimePicker(
                    requiredTime = task?.requiredTime ?: 0,
                    onTimeSelected = { newTime ->
                        task?.let {
                            viewModel.taskFeatures.update(it.copy(requiredTime = newTime))
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
                    text = stringResource(R.string.tags),
                    modifier = Modifier.width(100.dp)
                )
                TagPicker(
                    tags = uiState.tags,
                    selectedTagsIds = task?.tagsIds ?: emptyList(),
                    onDismiss = {},
                    onTagSelected = { tag, selected ->
                        task?.let {
                            if (selected) {
                                val newTask = it.copy(tagsIds = it.tagsIds.plus(tag.id))
                                viewModel.taskFeatures.update(newTask)
                            } else {
                                val newTask = it.copy(tagsIds = it.tagsIds.minus(tag.id))
                                viewModel.taskFeatures.update(newTask)
                            }
                        }
                    },
                    onTagCreated = { tag ->
                        task?.let {
                            viewModel.tagFeatures.add(tag, it)
                        }
                    },
                    onDeleted = { tag ->
                        task?.let {
                            viewModel.tagFeatures.delete(tag)
                        }
                    },
                )
            }
            task?.parentTaskId?.let { parentId ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                )  {
                    Text(text = stringResource(R.string.parent_task))
                    TextButton(
                        onClick = {
                            viewModel.openTaskScreen(parentId)
                        }
                    ) {
                        Text(stringResource(R.string.go_to_parent_task))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            )  {
                Text(text = stringResource(R.string.dependent_tasks))
                ChildTasksPicker(
                    tasks = uiState.tasks,
                    childTasksIds = task?.childTasksIds ?: emptyList(),
                    onCreateTask = {
                        task?.let {
                            viewModel.taskFeatures.addChild(it)
                        }
                    },
                    onCheckBox = { childTask, progress ->
                        viewModel.taskFeatures.update(childTask.copy(progress = if (progress) childTask.requiredTime else 0))
                    },
                    onTaskClick = { id ->
                        viewModel.openTaskScreen(id)
                    },
                )
            }
            Text(stringResource(R.string.progress))
            Slider(
                value = progress,
                onValueChange = { progress = it },
                onValueChangeFinished = {
                    task?.let {
                        Log.i("mytag", "progress changed")
                        viewModel.taskFeatures.update(it.copy(progress = progress.toInt()))
                    }
                },
                valueRange = 0f..(task?.requiredTime ?: 1).toFloat(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    TaskScreen()
}

