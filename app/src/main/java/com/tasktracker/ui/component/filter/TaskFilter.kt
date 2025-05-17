package com.tasktracker.ui.component.filter

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.tasktracker.R
import com.tasktracker.viewModel.filter.TaskFilterViewModel

@Composable
fun TaskFilter(
    modifier: Modifier = Modifier,
    parentEntry: NavBackStackEntry
) {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val taskFilterViewModel: TaskFilterViewModel = hiltViewModel()

    IconButton(
        modifier = modifier.testTag("FilterButton"),
        onClick = { showDialog = true }
    ) {
        Icon(painterResource(R.drawable.filter), contentDescription = null)
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter),
                    style = MaterialTheme.typography.titleLarge
                )
                CategoryFilter(parentEntry)
                BeginDateFilter()
                DeadlineDateFilter()

                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    Button(
                        modifier = Modifier.testTag("ResetFilterButton"),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        onClick = {
                            taskFilterViewModel.clearFilter()
                            showDialog = false
                        }) {
                        Text(
                            text = stringResource(R.string.reset),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        modifier = Modifier.testTag("ApplyFilterButton"),
                        contentPadding = PaddingValues(horizontal = 6.dp),
                        onClick = {
                            taskFilterViewModel.updateFilter()
                            showDialog = false
                        }) {
                        Text(
                            text = stringResource(R.string.ok),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}