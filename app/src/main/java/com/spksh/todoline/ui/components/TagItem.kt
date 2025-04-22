package com.spksh.todoline.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.spksh.todoline.R
import com.spksh.todoline.data.Tag.Tag

@Composable
fun TagItem(
    tag: Tag = Tag(name = "tag"),
    selected: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var showDeleteButton by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = onCheckedChange
        )
        Card(
            //modifier = Modifier.weight(1f).padding(end = 16.dp),
            modifier = Modifier.clickable { showDeleteButton = true },
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
        DropdownMenu(
            expanded = showDeleteButton,
            onDismissRequest = { showDeleteButton = false }
        ) {
            DropdownMenuItem(
                text = {Text(stringResource(R.string.delete))},
                onClick = {
                    onDelete()
                    showDeleteButton = false
                }
            )
        }
    }
}

@Preview
@Composable
fun TagItemPreview() {
    TagItem()
}