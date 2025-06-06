package com.spksh.todoline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spksh.todoline.R
import com.spksh.todoline.data.Tag.Tag

@Composable
fun TagPicker(
    tags: List<Tag> = emptyList(),
    selectedTagsIds: List<Long> = emptyList(),
    onDismiss: () -> Unit = {},
    onTagSelected: (Tag, Boolean) -> Unit = { _, _->},
    onTagCreated: (Tag) -> Unit = {},
    onDeleted: (Tag) -> Unit = {}
) {
    var newTagName by remember { mutableStateOf("") }
    var newTagColor by remember { mutableStateOf("#E6E6FA") }
    var showDialog by remember { mutableStateOf(false) }

    if (selectedTagsIds.isEmpty()) {
        TextButton(
            onClick = {showDialog = true}
        ) {
            Text(stringResource(R.string.choose_tags))
        }
    } else {
        LazyRow(
            modifier = Modifier.clickable { showDialog = true },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tags.filter {it.id in selectedTagsIds} ) { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(android.graphics.Color.parseColor(tag.color)),
                                shape = CircleShape
                            )
                    )
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
            title = { Text(stringResource(R.string.choose_tags)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTagName,
                        singleLine = true,
                        onValueChange = { newTagName = it },
                        label = { Text(stringResource(R.string.tag_name)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val tagColors = listOf(
                        "#E6E6FA",
                        "#673147",
                        "#FFDAB9",
                        "#FF7F50",
                        "#C8A2C8",
                        "#800080",
                        "#E2725B",
                        "#FFB6C1",
                        //"#F5F5DC",
                        //"#808080"
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        tagColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(color)))
                            ) {
                                RadioButton(
                                    selected = (color == newTagColor),
                                    onClick = { newTagColor = color },
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newTagName.isNotEmpty()) {
                                onTagCreated(Tag(name = newTagName, color = newTagColor))
                                newTagName = ""
                            }
                        }
                    ) {
                        Text(stringResource(R.string.create_tag))
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
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
