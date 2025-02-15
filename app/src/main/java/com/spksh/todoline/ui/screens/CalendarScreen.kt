package com.spksh.todoline.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spksh.todoline.R

@Composable
fun CalendarScreen(

) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "ToDoLine Calendar",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Day",
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
    }
}


