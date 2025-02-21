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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spksh.todoline.R
import com.spksh.todoline.TaskUiModel
import com.spksh.todoline.data.Tag
import com.spksh.todoline.data.Task
import com.spksh.todoline.ui.components.TagItem
import com.spksh.todoline.ui.components.TaskItem


@Composable
fun MatrixScreen(
    //tasks: List<Task> = emptyList(),
    tasks_1: List<TaskUiModel> = emptyList(),
    tasks_2: List<TaskUiModel> = emptyList(),
    tasks_3: List<TaskUiModel> = emptyList(),
    tasks_4: List<TaskUiModel> = emptyList(),
    tags: List<Tag> = emptyList(),
    showTasksWithoutTags: Boolean = true,
    onChangeTasksWithoutTagsVisibility: (Boolean) -> Unit = {},
    onTaskCheckedChanged: (TaskUiModel, Boolean) -> Unit = { _, _->},
    onTodoClick: (Int) -> Unit = {},
    onAddButton: () -> Unit = {},
    onTagCheckedChanged: (Tag, Boolean) -> Unit = { _, _->},
    onTagDeleted: (Tag) -> Unit = {},
) {
    var tagsExpanded by remember { mutableStateOf(false) }
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
                                    selected = showTasksWithoutTags,
                                    onCheckedChange = {onChangeTasksWithoutTagsVisibility(it)},
                                    onDelete = {},
                                )
                            },
                            onClick = { }
                        )
                        tags.forEach { tag ->
                            DropdownMenuItem(
                                text = {
                                    TagItem(
                                        tag = tag,
                                        selected = tag.show,
                                        onCheckedChange = {onTagCheckedChanged(tag, it)},
                                        onDelete = {onTagDeleted(tag)},
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Quadrant(
                        data = tasks_1,
                        name = "Important & Urgent",
                        onCheckBox = onTaskCheckedChanged,
                        onTodoClick = onTodoClick,
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        data = tasks_2,
                        name = "Important & Not Urgent",
                        onCheckBox = onTaskCheckedChanged,
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
                        onCheckBox = onTaskCheckedChanged,
                        onTodoClick = onTodoClick,
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        data = tasks_4,
                        name = "Unimportant & Not Urgent",
                        onCheckBox = onTaskCheckedChanged,
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
    data: List<TaskUiModel> = emptyList(),
    name: String = "",
    onCheckBox: (TaskUiModel, Boolean) -> Unit = {_,_->},
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
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        LazyColumn {
            items(data) { data ->
                TaskItem(
                    onCheckBox = {onCheckBox(data, it)},
                    onTodoClick = {onTodoClick(data.task.id)},
                    task = data
                )
            }
        }
    }
}

@Preview
@Composable
fun MatrixScreenPreview() {
    MatrixScreen()
}

