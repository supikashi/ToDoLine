package com.spksh.todoline


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MatrixScreen(modifier: Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = stringResource(R.string.todoline_matrix),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.tags)
            )
            Icon(imageVector = Icons.Filled.MoreVert, stringResource(R.string.more_options))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) {
                        Quadrant(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun Quadrant(modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(color = Color.DarkGray, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "Urgent & Important")
        LazyColumn {
            items(generateToDoes()) { data ->
                ToDoItem(data)
            }
        }
    }
}

@Composable
fun ToDoItem(data : ToDoData) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(data.isDone, {})
        Text(data.name)
    }
}

@Preview(showBackground = true)
@Composable
fun MatrixScreenPreview() {
    MatrixScreen(Modifier)
}

fun generateToDoes() = listOf(
    ToDoData(true, "to do"),
    ToDoData(false, "to do 2"),
    ToDoData(false, "clean the room"),
    ToDoData(true, "hw"),
    ToDoData(false, "to do 2"),
    ToDoData(true, "to do long long long long long text"),
    ToDoData(false, "to do 5"),
    ToDoData(false, "to do 6"),
    
)