package com.spksh.todoline.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.spksh.todoline.ui.components.DateTimePicker
import java.time.format.DateTimeFormatter

@Composable
fun EventScreen(
    eventId: Long = 0,
    viewModel: MainViewModel = viewModel(),
) {
    Log.i("mytag", "event screen")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = uiState.events.find { it.id == eventId }
    Log.i("mytag", event.toString())
    var expanded by remember { mutableStateOf(false) }
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
                        text = {Text(stringResource(R.string.delete))},
                        onClick = {
                            viewModel.popBackStack()
                            event?.let {
                                viewModel.eventFeatures.delete(it)
                            }
                        }
                    )
                }
            }
        }
        var name by remember { mutableStateOf(event?.name ?: "") }
        TextField(
            value = name,
            onValueChange = { newName ->
                event?.let {
                    Log.i("mytag", "name changed")
                    viewModel.eventFeatures.update(it.copy(name = newName))
                    name = newName
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            placeholder = {Text(stringResource(R.string.event))},
            modifier = Modifier.fillMaxWidth()
        )
        var description by remember { mutableStateOf(event?.description ?: "") }
        TextField(
            value = description,
            onValueChange = { newDescription ->
                event?.let {
                    Log.i("mytag", "description changed")
                    viewModel.eventFeatures.update(it.copy(description = newDescription))
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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
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
                text = stringResource(R.string.event_parameters),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.start_time))
                DateTimePicker(
                    deadline = event?.startTimeLocal
                        ?.format(DateTimeFormatter.ofPattern("MMM d yyyy H:mm")),
                    onDeadlineSelected = { newStartTime ->
                        viewModel.toRightZone(newStartTime)?.let { time ->
                            event?.let {
                                Log.i("mytag", "start time changed")
                                viewModel.eventFeatures.update(it.copy(startTime = time))
                            }
                        }
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.end_time))
                DateTimePicker(
                    deadline = event?.endTimeLocal
                        ?.format(DateTimeFormatter.ofPattern("MMM d yyyy H:mm")),
                    onDeadlineSelected = { newEndTime ->
                        viewModel.toRightZone(newEndTime)?.let { time ->
                            event?.let {
                                Log.i("mytag", "end time changed")
                                viewModel.eventFeatures.update(it.copy(endTime = time))
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
    EventScreen()
}