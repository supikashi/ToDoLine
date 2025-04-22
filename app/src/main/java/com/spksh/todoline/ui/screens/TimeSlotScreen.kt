package com.spksh.todoline.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spksh.todoline.R
import com.spksh.todoline.ui.MainViewModel
import com.spksh.todoline.ui.components.ScheduleVisualisation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotScreen(
    id: Long = 0,
    viewModel: MainViewModel = viewModel(),
) {
    Log.i("mytag", "timeslot screen")
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val timeSlot = uiState.timeSlots.find { it.id == id }
    val tag = viewModel.findTagById(timeSlot?.tagId ?: 0)
    Log.i("mytag", timeSlot.toString())
    var expandedMoreOptionsMenu by remember { mutableStateOf(false) }
    var expandedTagMenu by remember { mutableStateOf(false) }
    val daysOfWeek = remember { timeSlot?.daysOfWeek?.let {
        mutableStateListOf(it[0], it[1], it[2], it[3], it[4], it[5], it[6])
    } ?: mutableStateListOf() }

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
                    contentDescription = null
                )
            }
            IconButton(onClick = {expandedMoreOptionsMenu = true}) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_options)
                )
                DropdownMenu(
                    expanded = expandedMoreOptionsMenu,
                    onDismissRequest = {expandedMoreOptionsMenu = false},
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            viewModel.popBackStack()
                            timeSlot?.let {
                                viewModel.timeSlotFeatures.delete(it)
                            }
                        }
                    )
                }
            }
        }
        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            ScheduleVisualisation(
                timeSlots = uiState.timeSlots,
                tags = uiState.tags,
            )
            val days = listOf(stringResource(R.string.monday),
                stringResource(R.string.tuesday), stringResource(R.string.wednesday),
                stringResource(R.string.thursday), stringResource(R.string.friday),
                stringResource(R.string.saturday), stringResource(R.string.sunday)
            )
            Row(
                modifier = Modifier

            ) {
                days.forEachIndexed { index, day ->
                    InputChip(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        selected = daysOfWeek[index],
                        onClick = {
                            timeSlot?.let {
                                daysOfWeek[index] = !daysOfWeek[index]
                                //viewModel.timeSlotFeatures.update(it.copy(daysOfWeek = it.daysOfWeek.mapIndexed { i, b -> if (i == index) !b else b}))
                            }
                        },
                        label = {
                            Text(
                                text = day
                            )
                        }
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.time_slot_parameters),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.tag))
                    ExposedDropdownMenuBox(
                        expanded = expandedTagMenu,
                        onExpandedChange = { expandedTagMenu = !expandedTagMenu }

                    ) {
                        val color = Color(android.graphics.Color.parseColor(tag?.color ?: "#F5F5DC"))
                        TextField(
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            value = tag?.name ?: stringResource(R.string.any_task),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults. TrailingIcon(expanded = expandedTagMenu) },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = color,
                                focusedContainerColor = color,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.outline,
                                unfocusedTextColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(64.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedTagMenu,
                            onDismissRequest = { expandedTagMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Card(
                                    colors = CardDefaults
                                        .cardColors()
                                        .copy(containerColor = Color(android.graphics.Color.parseColor("#F5F5DC")))
                                ) {
                                    Text(
                                        text = "Any Task",
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }},
                                onClick = {
                                    timeSlot?.let {
                                        viewModel.timeSlotFeatures.update(it.copy(tagId = 0))
                                    }
                                    expandedTagMenu = false
                                }
                            )
                            uiState.tags.forEach { tag ->
                                DropdownMenuItem(
                                    text = { Card(
                                        colors = CardDefaults
                                            .cardColors()
                                            .copy(containerColor = Color(android.graphics.Color.parseColor(tag.color)))
                                    ) {
                                        Text(
                                            text = tag.name,
                                            modifier = Modifier.padding(8.dp),
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    },
                                    onClick = {
                                        timeSlot?.let {
                                            viewModel.timeSlotFeatures.update(it.copy(tagId = tag.id))
                                        }
                                        expandedTagMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                val startTimeState = rememberTimePickerState(
                    initialHour = (timeSlot?.startTime ?: 0) / 60,
                    initialMinute = (timeSlot?.startTime ?: 0) % 60,
                    is24Hour = true
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.start_time))
                    TimeInput(state = startTimeState)
                }
                val endTimeState = rememberTimePickerState(
                    initialHour = (timeSlot?.endTime ?: 0) / 60,
                    initialMinute = (timeSlot?.endTime ?: 0) % 60,
                    is24Hour = true
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.end_time))
                    TimeInput(state = endTimeState)
                }
                Button(onClick = {
                    timeSlot?.let {
                        val newTimeSlot = it.copy(
                            startTime = startTimeState.hour * 60 + startTimeState.minute,
                            endTime = endTimeState.hour * 60 + endTimeState.minute,
                            daysOfWeek = daysOfWeek
                        )
                        if (newTimeSlot.startTime <= newTimeSlot.endTime) {
                            if (viewModel.timeSlotFeatures.check(newTimeSlot)) {
                                viewModel.timeSlotFeatures.update(newTimeSlot)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.time_slots_overlap),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.bad_end_time),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}