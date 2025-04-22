package com.spksh.todoline.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spksh.todoline.R
import com.spksh.todoline.ui.MainActivity
import com.spksh.todoline.ui.MainViewModel

@Composable
fun MainSettingsScreen(
    viewModel: MainViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.todoline_settings),
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
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openScheduleSettingsScreen() }
                        .padding(8.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.schedule))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openStatisticsScreen() }
                        .padding(8.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.statistics))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(stringResource(R.string.language))

                    TextButton(
                        onClick = {
                            viewModel.settingsFeatures.setLanguage(!uiState.value.settings.isEnglish, context)
                        }
                    ) {
                        Text(if (uiState.value.settings.isEnglish) "English" else "Русский")
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(stringResource(R.string.dark_theme))
                    Switch(
                        checked = uiState.value.settings.isDarkTheme,
                        onCheckedChange = {viewModel.settingsFeatures.setTheme(it, context)}
                    )
                }
            }
        }
    }
}