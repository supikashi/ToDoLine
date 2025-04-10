package com.spksh.todoline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spksh.todoline.R
import com.spksh.todoline.ui.MainViewModel
import com.spksh.todoline.ui.components.ScheduleVisualisation
import com.spksh.todoline.ui.components.TimeSlotItem
import com.spksh.todoline.ui.model.TimeSlotUiModel

@Composable
fun ScheduleSettingsScreen(
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.schedule_settings),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall
                )

                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                    )
                }
            }
            ScheduleVisualisation(
                timeSlots = uiState.timeSlots,
                tags = uiState.tags,
            )
            val days = listOf(stringResource(R.string.m),
                stringResource(R.string.t), stringResource(R.string.w),
                stringResource(R.string.thursday), stringResource(R.string.f),
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
                        selected = false,
                        onClick = { },
                        label = {
                            Text(
                                text = day
                            )
                        }
                    )
                }
            }
            val week = listOf(stringResource(R.string.monday_full),
                stringResource(R.string.tuesday_full),
                stringResource(R.string.wednesday_full),
                stringResource(R.string.thursday_full),
                stringResource(R.string.friday_full),
                stringResource(R.string.saturday_full), stringResource(R.string.sunday_full)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEachIndexed { index, day ->
                    item {
                        Text(day)
                    }
                    items(uiState.timeSlots.filter { it.daysOfWeek[index] }) { item ->
                        TimeSlotItem(
                            timeSlot = item,
                            tag = viewModel.findTagById(item.tagId),
                            onClick = {viewModel.openTimeSlotScreen(item.id)}
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { viewModel.timeSlotFeatures.add(TimeSlotUiModel(startTime = 540, endTime = 600)) },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}