package com.spksh.todoline.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.spksh.todoline.R
import com.spksh.todoline.TaskUiModel
import com.spksh.todoline.data.Tag
import com.spksh.todoline.ui.components.ChildTasksPicker
import com.spksh.todoline.ui.components.DateTimePicker
import com.spksh.todoline.ui.components.RequiredTimePicker
import com.spksh.todoline.ui.components.TagPicker

@Composable
fun TaskScreen(
    task: TaskUiModel? = null,
    tasks: List<TaskUiModel> = emptyList(),
    tags: List<Tag> = emptyList(),
    onNameChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onImportanceChanged: (Float) -> Unit = {},
    onUrgencyChanged: (Float) -> Unit = {},
    onProgressChanged: (Float) -> Unit = {},
    onDeadlineChanged: (Long?) -> Unit = {},
    onRequiredTimeChanged: (Int) -> Unit = {},
    onTagSelected: (Tag, Boolean) -> Unit = {_,_->},
    onTagCreated: (Tag) -> Unit = {},
    onTagDeleted: (Tag) -> Unit = {},
    onCreateChildTask: () -> Unit = {},
    onChildTaskCheckBox: (TaskUiModel, Boolean) -> Unit = {_,_->},
    onChildTaskClick: (Int) -> Unit = {},
    onParentTaskClick: () -> Unit = {},
) {
    Log.i("mytag", "todo screen")
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            IconButton(onClick = {onBackClick()}) {
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
                        onClick = onDeleted
                    )
                }
            }
        }
        TextField(
            value = task?.task?.name ?: "",
            onValueChange = {onNameChanged(it)},
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            placeholder = {Text("Task")},
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = task?.task?.description ?: "",
            onValueChange = {onDescriptionChanged(it)},
            singleLine = false,

            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {Text("Description")},
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Task Parameters",
                style = MaterialTheme.typography.titleLarge
            )

            var importance by remember { mutableFloatStateOf((task?.task?.importance ?: 1).toFloat()) }
            var urgency by remember { mutableFloatStateOf((task?.task?.urgency ?: 1).toFloat()) }
            var progress by remember { mutableFloatStateOf((task?.task?.progress ?: 0f)) }
            Text("Importance")
            Slider(
                value = importance,
                onValueChange = { importance = it },
                onValueChangeFinished = { onImportanceChanged(importance) },
                valueRange = 1f..10f,
                steps = 8,
            )
            Text("Urgency")
            Slider(
                value = urgency,
                onValueChange = { urgency = it },
                onValueChangeFinished = { onUrgencyChanged(urgency) },
                valueRange = 1f..10f,
                steps = 8
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deadline")
                DateTimePicker(
                    deadline = task?.deadlineText,
                    onDeadlineSelected = onDeadlineChanged
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
                    onTimeSelected = onRequiredTimeChanged
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
                    tags = tags,
                    selectedTagsIds = task?.task?.tagsIds ?: emptyList(),
                    onDismiss = {},
                    onTagSelected = onTagSelected,
                    onTagCreated = onTagCreated,
                    onDeleted = onTagDeleted,
                )
            }
            if (task?.task?.parentTaskId != null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                )  {
                    Text(text = "Parent Task")
                    TextButton(
                        onClick = onParentTaskClick
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
                    tasks = tasks,
                    childTasksIds = task?.task?.childTasksIds ?: emptyList(),
                    onCreateTask = onCreateChildTask,
                    onCheckBox = onChildTaskCheckBox,
                    onTaskClick = onChildTaskClick,
                )
            }
            Text("Progress")
            Slider(
                value = progress,
                onValueChange = { progress = it },
                onValueChangeFinished = { onProgressChanged(progress) },
                valueRange = 0f..1f,
                steps = 9,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    TaskScreen()
}