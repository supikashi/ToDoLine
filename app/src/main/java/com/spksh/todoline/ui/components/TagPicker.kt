package com.spksh.todoline.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spksh.todoline.data.Tag

@Composable
fun TagPicker(
    tags: List<Tag> = emptyList(),
    selectedTagsIds: List<Int> = emptyList(),
    onDismiss: () -> Unit = {},
    onTagSelected: (Tag, Boolean) -> Unit = {_,_->},
    onTagCreated: (Tag) -> Unit = {},
    onDeleted: (Tag) -> Unit = {}
) {
    var newTagName by remember { mutableStateOf("") }
    var newTagColor by remember { mutableStateOf("#3498DB") }
    var showDialog by remember { mutableStateOf(false) }

    if (selectedTagsIds.isEmpty()) {
        TextButton(
            onClick = {showDialog = true}
        ) {
            Text("Choose Tags")
        }
    } else {
        LazyRow(
            modifier = Modifier.clickable { showDialog = true }
        ) {
            items(tags.filter {it.id in selectedTagsIds} ) { tag ->
                Card(
                    modifier = Modifier.padding(4.dp),
                    colors = CardDefaults
                        .cardColors()
                        .copy(containerColor = Color(android.graphics.Color.parseColor(tag.color)))
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = tag.name
                    )
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            title = { Text("Choose Tags") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTagName,
                        singleLine = true,
                        onValueChange = { newTagName = it },
                        label = { Text("Tag name") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newTagColor,
                        onValueChange = { newTagColor = it },
                        label = { Text("Color (HEX)") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newTagName.isNotEmpty()) {
                                onTagCreated(Tag(name = newTagName, color = newTagColor))
                                newTagName = ""
                            }
                        }
                    ) {
                        Text("Create Tag")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.heightIn(0.dp, 200.dp)) {
                        items(tags) { tag ->
                            TagItem(
                                tag = tag,
                                selected = tag.id in selectedTagsIds,
                                onCheckedChange = {selected -> onTagSelected(tag, selected)},
                                onDelete = { onDeleted(tag) },
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
