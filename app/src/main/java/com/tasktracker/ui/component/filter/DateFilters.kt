package com.tasktracker.ui.component.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasktracker.R
import com.tasktracker.ui.component.button.FilterButton
import com.tasktracker.viewModel.filter.TaskFilterViewModel
import java.time.LocalDate

@Composable
fun BeginDateFilter() {
    val filterViewModel: TaskFilterViewModel = hiltViewModel()
    val selectedBeginDate by filterViewModel.selectedBeginDate.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val dates: Map<String, Int> = remember {
        mapOf(
            context.getString(R.string.today) to LocalDate.now().dayOfMonth,
            context.getString(R.string.tomorrow) to LocalDate.now().dayOfMonth + 1,
        )
    }

    Text(text = stringResource(R.string.begin), style = MaterialTheme.typography.bodyLarge)
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
         items(
             items = dates.toList(),
             key = { it.first }
         ) { date ->
             FilterButton(
                 isSelected = selectedBeginDate?.dayOfMonth == date.second,
                 onSelect = {
                     filterViewModel.setBeginDateTime(LocalDate.now().withDayOfMonth(date.second))
                 },
                 onDeselect = {
                     filterViewModel.setBeginDateTime(null)
                 },
                 text = date.first
             )
         }
    }
}

@Composable
fun DeadlineDateFilter() {
    val filterViewModel: TaskFilterViewModel = hiltViewModel()
    val selectedDeadline by filterViewModel.selectedDeadline.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val dates: Map<String, Int> = remember {
        mapOf(
            context.getString(R.string.today) to LocalDate.now().dayOfMonth,
            context.getString(R.string.tomorrow) to LocalDate.now().dayOfMonth + 1,
        )
    }

    Text(text = stringResource(R.string.deadline), style = MaterialTheme.typography.bodyLarge)
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(
            items = dates.toList(),
            key = { it.first }
        ) { date ->
            FilterButton(
                isSelected = selectedDeadline?.dayOfMonth == date.second,
                onSelect = {
                    filterViewModel.setDeadline(LocalDate.now().withDayOfMonth(date.second))
                },
                onDeselect = {
                    filterViewModel.setDeadline(null)
                },
                text = date.first
            )
        }
    }
}

