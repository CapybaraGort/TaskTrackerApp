package com.tasktracker.ui.component.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.tasktracker.R
import com.tasktracker.ui.component.CategoryButton
import com.tasktracker.viewModel.CategoryViewModel
import com.tasktracker.viewModel.filter.TaskFilterViewModel
import androidx.compose.runtime.getValue

@Composable
fun CategoryFilter(parentEntry: NavBackStackEntry) {
    val taskFilterViewModel: TaskFilterViewModel = hiltViewModel()
    val categoryViewModel: CategoryViewModel = hiltViewModel(parentEntry)
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val selectedCategoryId by taskFilterViewModel.selectedCategoryId.collectAsStateWithLifecycle()

    Text(text = stringResource(R.string.categories), style = MaterialTheme.typography.bodyLarge)
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(
            items = categories,
            key = { category -> category.id ?: -1 }
        ) {
            CategoryButton(
                it,
                isSelected = it.id == selectedCategoryId,
                onSelect = { id ->
                    taskFilterViewModel.setCategoryId(id)
                },
                onDeselect = {
                    taskFilterViewModel.resetCategoryId()
                }
            )
        }
    }
}