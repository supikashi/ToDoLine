package com.spksh.todoline.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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

@Composable
fun MatrixScreen(
    //tasks: List<Task> = emptyList(),
    tasks_1: List<Task> = emptyList(),
    tasks_2: List<Task> = emptyList(),
    tasks_3: List<Task> = emptyList(),
    tasks_4: List<Task> = emptyList(),
    onCheckBox: (Int, Boolean) -> Unit = {_,_->},
    onTodoClick: (Int) -> Unit = {},
    onCalendar: () -> Unit = {},
    onAddButton: () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(80.dp),
                containerColor = Color.Transparent
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onCalendar,
                    icon = {Icon(imageVector = Icons.Filled.DateRange, null)},
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {Icon(imageVector = Icons.Filled.CheckCircle, null)},
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {Icon(imageVector = Icons.Filled.Settings, null)},
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddButton,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Task")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Log.i("mytag", "matrix screen")
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
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
    }

}

@Composable
fun Quadrant(
    data: List<Task> = emptyList(),
    name: String = "",
    onCheckBox: (Int, Boolean) -> Unit = {_,_->},
    onTodoClick: (Int) -> Unit = {},
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))
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
                    data = data
                )
            }
        }
    }
}

@Composable
fun ToDoItem(
    onCheckBox: (Int, Boolean) -> Unit = {_,_->},
    onTodoClick: (Int) -> Unit = {},
    data : Task
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onTodoClick(data.id) }
    ) {
        Checkbox(data.isDone, onCheckedChange = {onCheckBox(data.id, it)})
        Text(data.name)
    }
}

@Preview
@Composable
fun MatrixScreenPreview() {
    MatrixScreen()
}

