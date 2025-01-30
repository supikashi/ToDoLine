package com.spksh.todoline.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import com.spksh.todoline.R
import com.spksh.todoline.data.Task

@Composable
fun TaskScreen(
    task: Task? = null,
    onNameChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onImportanceChanged: (Float) -> Unit = {},
    onUrgencyChanged: (Float) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Log.i("mytag", "todo screen")
        var expanded by remember { mutableStateOf(false) }
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
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
                value = task?.name ?: "",
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
                value = task?.description ?: "",
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Task Parameters",
                    style = MaterialTheme.typography.titleLarge
                )

                var importance by remember { mutableFloatStateOf((task?.importance ?: 1).toFloat()) }
                var urgency by remember { mutableFloatStateOf((task?.urgency ?: 1).toFloat()) }
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
                    steps = 8,

                    )
                repeat(5) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("parameter")
                        Text("Choose...")
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
fun TaskScreenPreview() {
    TaskScreen()
}