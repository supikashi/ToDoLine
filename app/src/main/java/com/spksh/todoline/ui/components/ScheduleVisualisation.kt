package com.spksh.todoline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.model.TimeSlotUiModel

@Composable
fun ScheduleVisualisation(
    timeSlots: List<TimeSlotUiModel> = emptyList(),
    tags: List<Tag> = emptyList(),
    height: Int = 200
) {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(7) { dayOfWeek ->
                Box(
                    modifier = Modifier.height(height.dp).border(1.dp, Color.Gray).weight(1f)
                ) {
                    timeSlots.filter { it.daysOfWeek[dayOfWeek] } .forEach { timeSlot ->
                        val start = (timeSlot.startTime * height).toDouble() / (24 * 60)
                        val timeSlotHeight = ((timeSlot.endTime - timeSlot.startTime) * height).toDouble() / (24 * 60)
                        val tag = tags.find { it.id == timeSlot.tagId } ?: Tag()
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = start.dp)
                                .height(timeSlotHeight.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(android.graphics.Color.parseColor(tag.color)))

                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun ScheduleVisualisationPreview() {
    ScheduleVisualisation()
}