package com.spksh.todoline.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.spksh.todoline.ui.model.ActivityUiModel
import com.spksh.todoline.ui.model.EventUiModel
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.theme.extendedDark
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ActivityItem(
    activity: ActivityUiModel = ActivityUiModel(),
    heightAware: Boolean = false,
    onActivityClick: () -> Unit = {},
    onCheckBoxClick: (TaskUiModel, Int) -> Unit = {_,_->},
    findEventById: (Long) -> EventUiModel?,
    findTaskById: (Long) -> TaskUiModel?,
) {
    val minutesBetween = ChronoUnit.MINUTES.between(activity.startTimeLocal, activity.endTimeLocal).toInt()
    val modifier = if (heightAware) {
        val start = activity.startTimeLocal.run { hour * 60 + minute }
        Modifier
            .offset(y = start.dp)
            .height(minutesBetween.dp)

    } else {
        Modifier
    }
    if (activity.isTask) {
        val task = findTaskById(activity.activityId)
        TaskActivityItem(
            modifier = modifier,
            activity = activity,
            task = task,
            onTaskClick = onActivityClick,
            onCheckBoxClick = { check ->
                task?.let {
                    if (check) {
                        onCheckBoxClick(it, minutesBetween)
                    } else {
                        onCheckBoxClick(it, -minutesBetween)
                    }
                }
            },
        )
    } else {
        EventActivityItem(
            modifier = modifier,
            activity = activity,
            event = findEventById(activity.activityId),
            onEventClick = onActivityClick
        )
    }
}

@Composable
fun EventActivityItem(
    modifier: Modifier = Modifier,
    activity: ActivityUiModel = ActivityUiModel(),
    event: EventUiModel? = EventUiModel(),
    onEventClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .clickable { onEventClick() }
                .padding(8.dp)
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val dayText = if (activity.numberOfParts != 1) {
                " (Day ${activity.partIndex}/${activity.numberOfParts})"
            } else {
                ""
            }
            Text(text = event?.name + dayText)
            Text(
                text = activity.startTimeLocal.toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                        activity.endTimeLocal.toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TaskActivityItem(
    modifier: Modifier = Modifier,
    activity: ActivityUiModel = ActivityUiModel(),
    task: TaskUiModel? = TaskUiModel(),
    onTaskClick: () -> Unit = {},
    onCheckBoxClick: (Boolean) -> Unit = {},
) {
    var color: Color? = null
    task?.let {
        if (it.importance > 5) {
            if (it.urgency > 5) {
                color = extendedDark.quadrant1.colorContainer
            } else {
                color = extendedDark.quadrant2.colorContainer
            }
        } else {
            if (it.urgency > 5) {
                color = extendedDark.quadrant3.colorContainer
            } else {
                color = extendedDark.quadrant4.colorContainer
            }
        }
    }
    Card(
        modifier = modifier
            .fillMaxSize(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .clickable { onTaskClick() }
                .fillMaxSize()
        ) {
            Checkbox(
                checked = activity.isDone,
                onCheckedChange = {onCheckBoxClick(it)},
                enabled = true,
                colors = color?.let { CheckboxDefaults.colors().copy(uncheckedBorderColor = it, checkedBoxColor = it, checkedBorderColor = it) } ?: CheckboxDefaults.colors(),
                modifier = Modifier.align(Alignment.Top)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .padding(end = 16.dp)
            ) {
                val dayText = if (activity.numberOfParts != 1) {
                    stringResource(R.string.part, activity.partIndex, activity.numberOfParts)
                } else {
                    ""
                }
                val name = task?.let {
                    if (activity.subtaskId == 0L) {
                        it.name
                    } else {
                        it.subTasks.find { it.id == activity.subtaskId }?.name
                    }
                } ?: ""
                Text(text = name + dayText)
                if (activity.subtaskId != 0L) {
                    Text(
                        text = stringResource(R.string.subtask_of, task?.name ?: ""),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                task?.deadlineText?.let {
                    Text(
                        text = "${stringResource(R.string.deadline)}: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activity.isDeadlineMet) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            extendedDark.quadrant1.colorContainer
                        }
                    )
                }
                Text(
                    text = activity.startTimeLocal.toLocalTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                            activity.endTimeLocal.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
fun EventItemPreview() {
    TaskActivityItem()
}