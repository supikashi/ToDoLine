package com.spksh.todoline.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
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
import kotlin.math.roundToInt

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
                                    tag = Tag(name = stringResource(R.string.withouttags)),
                                    selected = uiState.settings.showTasksWithoutTags,
                                    onCheckedChange = {viewModel.settingsFeatures.setShowWithoutTags(it)},
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
                                        onCheckedChange = { viewModel.tagFeatures.update(tag.copy(show = it)) },
                                        onDelete = { viewModel.tagFeatures.delete(tag) },
                                    )
                                },
                                onClick = { }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                TagItem(
                                    tag = Tag(name = stringResource(R.string.only_this_day_tasks)),
                                    selected = uiState.settings.showTodayTasks,
                                    onCheckedChange = {
                                        viewModel.settingsFeatures.setShowTodayTasks(it)
                                        if (it) {
                                            viewModel.settingsFeatures.setShowWeekTasks(false)
                                            viewModel.settingsFeatures.setShowMonthTasks(false)
                                        }
                                    },
                                    onDelete = {},
                                )
                            },
                            onClick = { }
                        )
                        DropdownMenuItem(
                            text = {
                                TagItem(
                                    tag = Tag(name = stringResource(R.string.only_this_week_tasks)),
                                    selected = uiState.settings.showWeekTasks,
                                    onCheckedChange = {
                                        viewModel.settingsFeatures.setShowWeekTasks(it)
                                        if (it) {
                                            viewModel.settingsFeatures.setShowTodayTasks(false)
                                            viewModel.settingsFeatures.setShowMonthTasks(false)
                                        }
                                    },
                                    onDelete = {},
                                )
                            },
                            onClick = { }
                        )
                        DropdownMenuItem(
                            text = {
                                TagItem(
                                    tag = Tag(name = stringResource(R.string.show_this_month_tasks)),
                                    selected = uiState.settings.showMonthTasks,
                                    onCheckedChange = {
                                        viewModel.settingsFeatures.setShowMonthTasks(it)
                                        if (it) {
                                            viewModel.settingsFeatures.setShowTodayTasks(false)
                                            viewModel.settingsFeatures.setShowWeekTasks(false)
                                        }
                                    },
                                    onDelete = {},
                                )
                            },
                            onClick = { }
                        )
                        DropdownMenuItem(
                            text = {
                                TagItem(
                                    tag = Tag(name = stringResource(R.string.show_completed_tasks)),
                                    selected = uiState.settings.showCompletedTasks,
                                    onCheckedChange = {viewModel.settingsFeatures.setShowCompletedTasks(it)},
                                    onDelete = {},
                                )
                            },
                            onClick = { }
                        )
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
                    viewModel.taskFeatures.update(task.copy(progress = if (progress) task.requiredTime else 0))
                },
                onTaskClick = {viewModel.openTaskScreen(it)},
                currentTime = uiState.currentTime,
                onTaskDragged = { task, imp, urg ->
                    viewModel.taskFeatures.update(task.copy(importance = imp, urgency = urg))
                }
            )
        }
        FloatingActionButton(
            onClick = { viewModel.taskFeatures.add() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
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
    currentTime: LocalDateTime,
    onTaskDragged: (TaskUiModel, Int, Int) -> Unit = {_,_,_->}
) {
    var firstListBounds by remember { mutableStateOf<Rect>(Rect(Offset(0f,0f), Offset(0f,0f))) }
    var secondListBounds by remember { mutableStateOf<Rect>(Rect(Offset(0f,0f), Offset(0f,0f))) }
    var thirdListBounds by remember { mutableStateOf<Rect>(Rect(Offset(0f,0f), Offset(0f,0f))) }
    var fourthListBounds by remember { mutableStateOf<Rect>(Rect(Offset(0f,0f), Offset(0f,0f))) }
    var draggedItemCenter by remember { mutableStateOf(Offset(0f,0f)) }
    var draggedItem by remember { mutableStateOf<TaskUiModel?>(null) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf<IntSize>(IntSize.Zero) }
    var boxOffset by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current
    Box(
        modifier = Modifier.onGloballyPositioned {
            boxOffset = it
        }
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
                    name = stringResource(R.string.important_urgent),
                    onCheckBox = onCheckBox,
                    onTaskClick = onTaskClick,
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            firstListBounds = coordinates.boundsInWindow()
                            size = coordinates.size
                        },
                    currentTime = currentTime,
                    color = extendedDark.quadrant1.colorContainer,
                    onDragStart = { task, taskOffset ->
                        draggedItem = task
                        offset = boxOffset?.screenToLocal(taskOffset) ?: Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    },
                    onDragEnd = {
                        draggedItem?.let {
                            if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 10, 10)
                            } else if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 10, 0)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 0, 10)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 0, 0)
                            }
                        }
                        offset = Offset.Zero
                        draggedItem = null
                    },
                    draggedItemId = draggedItem
                )
                Quadrant(
                    tasks = tasks_2,
                    name = stringResource(R.string.important_not_urgent),
                    onCheckBox = onCheckBox,
                    onTaskClick = onTaskClick,
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            secondListBounds = coordinates.boundsInWindow()
                        },
                    currentTime = currentTime,
                    color = extendedDark.quadrant2.colorContainer,
                    onDragStart = { task, taskOffset ->
                        draggedItem = task
                        offset = boxOffset?.screenToLocal(taskOffset) ?: Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    },
                    onDragEnd = {
                        draggedItem?.let {
                            if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 10, 10)
                            } else if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 10, 0)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 0, 10)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 0, 0)
                            }
                        }
                        offset = Offset.Zero
                        draggedItem = null
                    },
                    draggedItemId = draggedItem
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Quadrant(
                    tasks = tasks_3,
                    name = stringResource(R.string.unimportant_urgent),
                    onCheckBox = onCheckBox,
                    onTaskClick = onTaskClick,
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            thirdListBounds = coordinates.boundsInWindow()
                        },
                    currentTime = currentTime,
                    color = extendedDark.quadrant3.colorContainer,
                    onDragStart = { task, taskOffset ->
                        draggedItem = task
                        offset = boxOffset?.screenToLocal(taskOffset) ?: Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    },
                    onDragEnd = {
                        draggedItem?.let {
                            if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 10, 10)
                            } else if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 10, 0)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 0, 10)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 0, 0)
                            }
                        }
                        offset = Offset.Zero
                        draggedItem = null
                    },
                    draggedItemId = draggedItem
                )
                Quadrant(
                    tasks = tasks_4,
                    name = stringResource(R.string.unimportant_not_urgent),
                    onCheckBox = onCheckBox,
                    onTaskClick = onTaskClick,
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            fourthListBounds = coordinates.boundsInWindow()
                        },
                    currentTime = currentTime,
                    color = extendedDark.quadrant4.colorContainer,
                    onDragStart = { task, taskOffset ->
                        draggedItem = task
                        offset = boxOffset?.screenToLocal(taskOffset) ?: Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    },
                    onDragEnd = {
                        draggedItem?.let {
                            if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 10, 10)
                            } else if (draggedItemCenter.y < firstListBounds.bottom && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 10, 0)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x < firstListBounds.right) {
                                onTaskDragged(it, 0, 10)
                            } else if (draggedItemCenter.y > firstListBounds.top && draggedItemCenter.x > firstListBounds.left) {
                                onTaskDragged(it, 0, 0)
                            }
                        }
                        offset = Offset.Zero
                        draggedItem = null
                    },
                    draggedItemId = draggedItem
                )
            }
        }
        draggedItem?.let {
            Box(
                modifier = Modifier
                    .width(with(density) { size.width.toDp() })
                    .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                    .pointerInput(Unit) { detectDragGestures { _, dragAmount -> offset += dragAmount } }
                    .onGloballyPositioned { coordinates ->
                        val rect = coordinates.boundsInWindow()
                        draggedItemCenter = Offset((rect.left + rect.right)/2, (rect.top + rect.bottom)/2)
                        Log.i("mytag", rect.toString())
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { }
                        .padding(end = 8.dp)
                ) {
                    Checkbox(
                        checked = it.progress == it.requiredTime,
                        onCheckedChange = {},
                        enabled = true,
                        modifier = Modifier.align(Alignment.Top)
                    )
                    Column {
                        Text(
                            text = it.name,
                            //maxLines = 1
                            color = MaterialTheme.colorScheme.onSurface
                            //modifier = Modifier.
                        )
                        it.deadlineText?.let { deadlineText ->
                            val color = if (currentTime.isBefore(it.deadlineLocal)) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                extendedDark.quadrant1.colorContainer
                            }
                            Text(
                                text = deadlineText,
                                //maxLines = 1
                                style = MaterialTheme.typography.labelSmall,
                                color = color
                            )
                        }
                    }
                }
            }
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
    color: Color,
    onDragStart: (TaskUiModel, Offset) -> Unit = {_,_->},
    onDrag: (PointerInputChange, Offset) -> Unit = { _, _->},
    onDragEnd: () -> Unit = {},
    draggedItemId: TaskUiModel? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
            LazyColumn {
                items(tasks, key = {it.id}) { task ->
                    TaskItem(
                        onCheckBox = {onCheckBox(task, it)},
                        onTaskClick = {onTaskClick(task.id)},
                        currentTime = currentTime,
                        task = task,
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        draggedItem = draggedItemId
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

