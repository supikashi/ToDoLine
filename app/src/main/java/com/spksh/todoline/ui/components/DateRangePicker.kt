package com.spksh.todoline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateRangePicker(
    initialVisibleMonth: YearMonth = YearMonth.now(),
    onDatesSelected: (startDate: LocalDate?, endDate: LocalDate?) -> Unit
) {
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }

    val monthsList = remember {
        generateMonthsList(initialVisibleMonth)
    }

    HorizontalPager(
        state = rememberPagerState(
            initialPage = monthsList.size / 2,
            pageCount = {monthsList.size}
        ),
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        CalendarMonth(
            yearMonth = monthsList[page],
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            onDateClicked = { date ->
                if (selectedStartDate == date) {
                    selectedStartDate = null
                } else if (selectedEndDate == date) {
                    selectedEndDate = null
                } else if (selectedStartDate == null) {
                    selectedStartDate = date
                    if (selectedEndDate != null && selectedEndDate!!.isBefore(date)) {
                        selectedEndDate = null
                    }
                } else if (!selectedStartDate!!.isAfter(date)) {
                    selectedEndDate = date
                } else {
                    selectedStartDate = date
                    selectedEndDate = null
                }
                onDatesSelected(selectedStartDate, selectedEndDate)
            }
        )
    }
}

@Composable
private fun CalendarMonth(
    yearMonth: YearMonth,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateClicked: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek
    val emptyDays = (firstDayOfMonth.value - 1) % 7

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            DayOfWeek.values().forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        val today = LocalDate.now()
        Column(modifier = Modifier.padding(top = 8.dp)) {
            var currentRow = 0
            val rows = 6
            val columns = 7

            repeat(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    repeat(columns) { column ->
                        val dayIndex = row * columns + column - emptyDays + 1
                        val date = if (dayIndex in 1..daysInMonth) {
                            yearMonth.atDay(dayIndex)
                        } else null
                        val isSelected = if (selectedStartDate != null && selectedEndDate != null) {
                            (selectedStartDate.isAfter(date ?: LocalDate.MIN) || selectedEndDate.isBefore(date ?: LocalDate.MIN)).not()
                        } else {
                            false
                        }
                        DateCell(
                            date = date,
                            isToday = date?.isEqual(today) == true,
                            isSelected = isSelected,
                            isStartDate = date == selectedStartDate,
                            isEndDate = date == selectedEndDate,
                            onClick = { if (date != null) onDateClicked(date) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCell(
    date: LocalDate?,
    isToday: Boolean,
    isSelected: Boolean,
    isStartDate: Boolean,
    isEndDate: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = date != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isStartDate || isEndDate) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

private fun generateMonthsList(
    initialMonth: YearMonth,
    monthsBefore: Int = 1000,
    monthsAfter: Int = 1000
): List<YearMonth> {
    val months = mutableListOf<YearMonth>()
    for (i in -monthsBefore..monthsAfter) {
        months.add(initialMonth.plusMonths(i.toLong()))
    }
    return months
}