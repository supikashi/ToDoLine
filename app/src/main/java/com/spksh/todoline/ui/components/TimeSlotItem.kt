package com.spksh.todoline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spksh.todoline.R
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.model.TimeSlotUiModel

@Composable
fun TimeSlotItem(
    modifier: Modifier = Modifier,
    timeSlot: TimeSlotUiModel? = TimeSlotUiModel(),
    tag: Tag? = Tag(),
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        ) {
            Spacer(
                Modifier.fillMaxHeight().width(10.dp).background(Color(android.graphics.Color.parseColor(tag?.color ?: "#F5F5DC")))
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = tag?.name ?: stringResource(R.string.any_task))
                val startTime = minutesToString(timeSlot?.startTime ?: 0)
                val endTime = minutesToString(timeSlot?.endTime ?: 0)
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeSlotItemPreview() {
    TimeSlotItem()
}

private fun minutesToString(time: Int) : String {
    val hours = (if (time / 60 < 10) "0" else "") + (time / 60).toString()
    val minutes = (if (time % 60 < 10) "0" else "") + (time % 60).toString()
    return "$hours:$minutes"
}