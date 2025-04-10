package com.spksh.todoline.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spksh.todoline.R
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.MainViewModel
import com.spksh.todoline.ui.components.ActivityItem
import com.spksh.todoline.ui.components.LazyColumnDragAndDrop
import com.spksh.todoline.ui.model.ActivityUiModel
import com.spksh.todoline.ui.model.EventUiModel
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.TimeSlotUiModel
import com.spksh.todoline.ui.theme.extendedDark
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isTimeLine by rememberSaveable { mutableStateOf(true) }
    var openOverdueBottomSheet by rememberSaveable { mutableStateOf(false) }
    var openCustomTaskOrderBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val overdueBottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val customTaskOrderBottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val tasksWithBadDeadline = remember { mutableStateListOf<TaskUiModel>() }

    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    Box {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp, start = 8.dp)

            ) {
                Text(
                    text = stringResource(R.string.todoline_calendar),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall
                )
                TextButton(
                    onClick = {isTimeLine = !isTimeLine}
                ) {
                    Text(
                        text = if (isTimeLine) stringResource(R.string.day) else stringResource(R.string.list),
                        style = MaterialTheme.typography.titleLarge
                    )
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
            val pageCount = 1000
            val initialDate = remember { LocalDate.now() }
            val pagerState = rememberPagerState(
                initialPage = pageCount / 2,
                pageCount = {pageCount}
            )
            if (isTimeLine) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val date = initialDate.plusDays((page - pageCount / 2).toLong())
                    Column {
                        DayHeader(date)
                        TimeLine(
                            date = date,
                            timeSlots = uiState.timeSlots,
                            activities = uiState.activities,
                            onActivityClick = { id, isTask ->
                                if (isTask)
                                    viewModel.openTaskScreen(id)
                                else
                                    viewModel.openEventScreen(id)
                            },
                            onCheckBoxClick = { task, activity, progress ->
                                viewModel.timelinedActivityFeatures.update(activity.copy(isDone = !activity.isDone))
                                if (activity.subtaskId == 0L) {
                                    val newProgress = min(max(0, task.progress + progress), task.requiredTime)
                                    viewModel.taskFeatures.update(task.copy(progress = newProgress))
                                } else {
                                    val i = task.subTasks.indexOfFirst { it.id == activity.subtaskId }
                                    val newProgress = min(max(0, task.subTasks[i].progress + progress), task.subTasks[i].requiredTime)
                                    viewModel.taskFeatures.update(task.copy(subTasks = task.subTasks.mapIndexed { index, subTask ->
                                        if (index == i) subTask.copy(progress = newProgress) else subTask
                                    }))
                                }
                            },
                            findEventById = { viewModel.findEventById(it) },
                            findTaskById = { viewModel.findTaskById(it) },
                            findTagById = { viewModel.findTagById(it) },
                            currentTime = uiState.currentTime
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.tasksAndEventsByDays.toList().sortedBy { it.first }.forEach { activity ->
                        item { Text(activity.first.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))) }
                        items(activity.second) { item ->
                            ActivityItem(
                                activity = item,
                                heightAware = false,
                                onActivityClick = {
                                    if (item.isTask)
                                        viewModel.openTaskScreen(item.activityId)
                                    else
                                        viewModel.openEventScreen(item.activityId)
                                },
                                onCheckBoxClick = { task, progress ->
                                    viewModel.timelinedActivityFeatures.update(item.copy(isDone = !item.isDone))
                                    if (item.subtaskId == 0L) {
                                        val newProgress = min(max(0, task.progress + progress), task.requiredTime)
                                        viewModel.taskFeatures.update(task.copy(progress = newProgress))
                                    } else {
                                        val i = task.subTasks.indexOfFirst { it.id == item.subtaskId }
                                        val newProgress = min(max(0, task.subTasks[i].progress + progress), task.subTasks[i].requiredTime)
                                        viewModel.taskFeatures.update(task.copy(subTasks = task.subTasks.mapIndexed { index, subTask ->
                                            if (index == i) subTask.copy(progress = newProgress) else subTask
                                        }))
                                    }
                                },
                                findEventById = { viewModel.findEventById(it) },
                                findTaskById = { viewModel.findTaskById(it) }
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(end = 16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Surface(
                shape = FloatingActionButtonDefaults.shape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(56.dp)
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onLongClick = {
                            openCustomTaskOrderBottomSheet = true
                        },
                        onClick = {
                            scope.launch {
                                val res = viewModel.timelineFeatures.getTimeline(
                                    uiState.tasks
                                ).await()
                                if (res == null) {
                                    Toast.makeText(
                                        context,
                                        "Bad Schedule",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    tasksWithBadDeadline.clear()
                                    tasksWithBadDeadline.addAll(res)
                                    if (tasksWithBadDeadline.isNotEmpty()) {
                                        openOverdueBottomSheet = true
                                    }
                                }
                            }
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            FloatingActionButton(
                onClick = { viewModel.eventFeatures.add(EventUiModel()) },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    }
    if (openOverdueBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openOverdueBottomSheet = false },
            sheetState = overdueBottomSheetState,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val res = viewModel.timelineFeatures.getTimelineByImportance(uiState.tasks).await()
                                if (res == null) {
                                    Toast.makeText(
                                        context,
                                        "Bad Schedule",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    tasksWithBadDeadline.clear()
                                    tasksWithBadDeadline.addAll(res)
                                    openOverdueBottomSheet = tasksWithBadDeadline.isNotEmpty()
                                }
                            }
                        },
                    ) {
                        Text(text = stringResource(R.string.promote_important_tasks))
                    }
                    Button(
                        onClick = {
                            scope
                                .launch { overdueBottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!overdueBottomSheetState.isVisible) {
                                        openOverdueBottomSheet = false
                                    }
                                }
                            openCustomTaskOrderBottomSheet = true
                        },
                    ) {
                        Text(text = stringResource(R.string.set_custom_order))
                    }

                }
                Text(
                    text = stringResource(R.string.potentially_overdue_tasks),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (tasksWithBadDeadline.any { it.importance > 5 }) {
                        item {
                            Text(
                                text = stringResource(R.string.important)
                            )
                        }
                    }
                    items(tasksWithBadDeadline.filter { it.importance > 5 }) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            Column(
                                modifier = Modifier
                                    .clickable { }
                                    .padding(8.dp)
                                    .padding(horizontal = 8.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = it.name,
                                    //maxLines = 1
                                    color = MaterialTheme.colorScheme.onSurface
                                    //modifier = Modifier.
                                )
                                it.deadlineText?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    if (tasksWithBadDeadline.any { it.importance < 6 }) {
                        item {
                            Text(stringResource(R.string.unimportant))
                        }
                    }
                    items(tasksWithBadDeadline.filter { it.importance < 6 }) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            Column(
                                modifier = Modifier
                                    .clickable { }
                                    .padding(8.dp)
                                    .padding(horizontal = 8.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = it.name,
                                    //maxLines = 1
                                    color = MaterialTheme.colorScheme.onSurface
                                    //modifier = Modifier.
                                )
                                it.deadlineText?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (openCustomTaskOrderBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {openCustomTaskOrderBottomSheet = false},
            sheetState = customTaskOrderBottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.order_of_tasks),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
                LazyColumnDragAndDrop(
                    tasks = uiState.settings.tasksOrder,
                    timeline = uiState.activities.filter { it.isTask },
                    onOrderChange = {
                        viewModel.taskFeatures.changeTaskOrder(it)
                        scope.launch {
                            val res = viewModel.timelineFeatures.getTimeline(
                                tasklist = it
                            ).await()
                            if (res == null) {
                                Toast.makeText(
                                    context,
                                    "Bad Schedule",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    item = { task, deadlineColor ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            Column(
                                modifier = Modifier
                                    .clickable { }
                                    .padding(8.dp)
                                    .padding(horizontal = 8.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = task.name,
                                    //maxLines = 1
                                    color = MaterialTheme.colorScheme.onSurface
                                    //modifier = Modifier.
                                )
                                task.deadlineText?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = deadlineColor
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
@Composable
private fun DayHeader(date: LocalDate) {
    Text(
        text = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun TimeLine(
    date: LocalDate = LocalDate.now(),
    timeSlots: List<TimeSlotUiModel> = emptyList(),
    activities: List<ActivityUiModel> = emptyList(),
    onActivityClick: (Long, Boolean) -> Unit = {_,_ ->},
    onCheckBoxClick: (TaskUiModel, ActivityUiModel, Int) -> Unit = { _,_,_->},
    findEventById: (Long) -> EventUiModel?,
    findTaskById: (Long) -> TaskUiModel?,
    findTagById: (Long) -> Tag?,
    currentTime: LocalDateTime
) {
    val todayActivities = activities.filter {
        it.startTimeLocal.toLocalDate() == date
    }
    val dayOfWeek = date.dayOfWeek
    val todayTimeSlots = timeSlots.filter {
        it.daysOfWeek[dayOfWeek.value - 1]
    }
    var textHeight by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((24 * 60).dp)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row {
            Box(Modifier.width(IntrinsicSize.Min)) {
                repeat(23) { hour ->
                    Text(
                        text = "%02d:00".format(hour + 1),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(end = 16.dp)
                            .onSizeChanged { textHeight = it.height }
                            .offset(y = with(LocalDensity.current) { (60 * (hour + 1)).dp - textHeight.toDp() / 2 }),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Box {
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                        .height((24 * 60).dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                )
                todayTimeSlots.forEach {
                    val tag = findTagById(it.tagId)
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                            .height((it.endTime - it.startTime).dp)
                            .offset(y = it.startTime.dp)
                            .background(
                                Color(
                                    android.graphics.Color.parseColor(
                                        tag?.color ?: "#F5F5DC"
                                    )
                                )
                            )
                    )
                }
            }
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                repeat(23) { hour ->
                    HorizontalDivider(
                        modifier = Modifier
                            .offset(y = (60 * (hour + 1)).dp)
                            .padding(horizontal = 10.dp)
                    )
                }
                todayActivities.forEach {
                    ActivityItem(
                        activity = it,
                        heightAware = true,
                        onActivityClick = {onActivityClick(it.activityId, it.isTask)},
                        onCheckBoxClick = {task, progress -> onCheckBoxClick(task, it, progress)},
                        findEventById = findEventById,
                        findTaskById = findTaskById,
                    )
                }
            }
        }
        if (currentTime.toLocalDate() == date) {
            HorizontalDivider(
                modifier = Modifier
                    .offset(y = (currentTime.hour * 60 + currentTime.minute).dp)
                    .padding(end = 10.dp),
                color = extendedDark.quadrant1.colorContainer
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0)
@Composable
fun CalendarScreenPreview() {
    //TimeLine()
}
