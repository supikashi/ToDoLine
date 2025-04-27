package com.spksh.todoline.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spksh.todoline.R
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.MainViewModel
import com.spksh.todoline.ui.components.AnimatedCircle
import com.spksh.todoline.ui.components.DateRangePicker
import com.spksh.todoline.ui.model.Statistics
import com.spksh.todoline.ui.theme.extendedDark
import kotlinx.coroutines.launch


@Composable
fun StatisticsScreen(
    viewModel: MainViewModel = viewModel(),
) {
    var statistics by remember { mutableStateOf<Statistics?>(null) }
    val scope = rememberCoroutineScope()
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            IconButton(onClick = {viewModel.popBackStack()}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_matrix_screen)
                )
            }
            Text(
                text = stringResource(R.string.todoline_statistics),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_options),
                )
            }
        }
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            DateRangePicker(
                onDatesSelected = { start, end ->
                    if (start != null && end == null) {
                        scope.launch {
                            statistics = viewModel.statisticsFeatures.getStatistics(start, start).await()
                        }
                    } else if (start == null && end != null) {
                        scope.launch {
                            statistics = viewModel.statisticsFeatures.getStatistics(end, end).await()
                        }
                    } else if (start != null && end != null) {
                        scope.launch {
                            statistics = viewModel.statisticsFeatures.getStatistics(start, end).await()
                        }
                    }
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            statistics?.let {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedCircle(
                            proportions = listOf(
                                it.totalQuadrant1TasksDone / it.totalTasksDone.toFloat(),
                                it.totalQuadrant2TasksDone / it.totalTasksDone.toFloat(),
                                it.totalQuadrant3TasksDone / it.totalTasksDone.toFloat(),
                                it.totalQuadrant4TasksDone / it.totalTasksDone.toFloat(),
                            ),
                            colors = listOf(
                                extendedDark.quadrant1.colorContainer,
                                extendedDark.quadrant2.colorContainer,
                                extendedDark.quadrant3.colorContainer,
                                extendedDark.quadrant4.colorContainer
                            ),
                            modifier = Modifier
                                .size(128.dp)
                        )
                        Column(
                            modifier = Modifier.height(128.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.total_tasks_done, it.totalTasksDone),
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_1,
                                    it.totalQuadrant1TasksDone
                                ),
                                color = extendedDark.quadrant1.colorContainer
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_2,
                                    it.totalQuadrant2TasksDone
                                ),
                                color = extendedDark.quadrant2.colorContainer
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_3,
                                    it.totalQuadrant3TasksDone
                                ),
                                color = extendedDark.quadrant3.colorContainer
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_4,
                                    it.totalQuadrant4TasksDone
                                ),
                                color = extendedDark.quadrant4.colorContainer
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedCircle(
                            proportions = listOf(
                                it.totalQuadrant1ProgressMinutes / it.totalProgressMinutes.toFloat(),
                                it.totalQuadrant2ProgressMinutes / it.totalProgressMinutes.toFloat(),
                                it.totalQuadrant3ProgressMinutes / it.totalProgressMinutes.toFloat(),
                                it.totalQuadrant4ProgressMinutes / it.totalProgressMinutes.toFloat(),
                            ),
                            colors = listOf(
                                extendedDark.quadrant1.colorContainer,
                                extendedDark.quadrant2.colorContainer,
                                extendedDark.quadrant3.colorContainer,
                                extendedDark.quadrant4.colorContainer
                            ),
                            modifier = Modifier
                                .size(128.dp)
                        )
                        Column(
                            modifier = Modifier.height(128.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.total_time_spent,
                                    getStringTime(it.totalProgressMinutes)
                                ),
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_1,
                                    getStringTime(it.totalQuadrant1ProgressMinutes)
                                ),
                                color = extendedDark.quadrant1.colorContainer
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_2,
                                    getStringTime(it.totalQuadrant2ProgressMinutes)
                                ),
                                color = extendedDark.quadrant2.colorContainer
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_3,
                                    getStringTime(it.totalQuadrant3ProgressMinutes)
                                ),
                                color = extendedDark.quadrant3.colorContainer
                            )
                            TextRow(
                                text = stringResource(
                                    R.string.quadrant_4,
                                    getStringTime(it.totalQuadrant4ProgressMinutes)
                                ),
                                color = extendedDark.quadrant4.colorContainer
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val sum = it.tagsTotalTasksDone.sumOf { it.second }
                        AnimatedCircle(
                            proportions = it.tagsTotalTasksDone.map { it.second / sum.toFloat() },
                            colors = it.tagsTotalTasksDone.map {
                                Color(android.graphics.Color.parseColor(
                                    (viewModel.findTagById(it.first) ?: Tag()).color)
                                )
                            },
                            modifier = Modifier
                                .size(128.dp)
                        )
                        Column(
                            modifier = Modifier
                                .height(128.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            it.tagsTotalTasksDone.forEach {
                                val tag = viewModel.findTagById(it.first) ?: Tag()
                                TextRow(
                                    text = "${tag.name}: ${it.second}",
                                    color = Color(android.graphics.Color.parseColor(tag.color))
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val sum = it.tagsTotalProgressMinutes.sumOf { it.second }
                        AnimatedCircle(
                            proportions = it.tagsTotalProgressMinutes.map { it.second / sum.toFloat() },
                            colors = it.tagsTotalProgressMinutes.map {
                                Color(android.graphics.Color.parseColor(
                                    (viewModel.findTagById(it.first) ?: Tag()).color)
                                )
                            },
                            modifier = Modifier
                                .size(128.dp)
                        )
                        Column(
                            modifier = Modifier
                                .height(128.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            it.tagsTotalProgressMinutes.forEach {
                                val tag = viewModel.findTagById(it.first) ?: Tag()
                                TextRow(
                                    text = "${tag.name}: ${getStringTime(it.second)}",
                                    color = Color(android.graphics.Color.parseColor(tag.color))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getStringTime(time: Int) = "${time / 60}:" +
        "${
            if (time % 60 < 10) 
                "0${time % 60}"
            else
                time % 60
        }"

@Composable
fun TextRow(
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
        )
    }
}