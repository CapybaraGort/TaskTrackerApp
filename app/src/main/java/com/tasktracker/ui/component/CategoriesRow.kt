package com.tasktracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.tasktracker.R
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.domain.entity.Category
import com.tasktracker.viewModel.CategorySelectViewModel
import com.tasktracker.viewModel.CategoryViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoriesRow(
    onCategorySelect: (Int?) -> Unit,
    onDelete: (Int?) -> Unit = {},
    parentEntry: NavBackStackEntry,
    initCategoryId: Int? = null
) {
    val scope = rememberCoroutineScope()
    val categorySelectViewModel: CategorySelectViewModel = viewModel()
    val selectedId by categorySelectViewModel.selectedId.collectAsStateWithLifecycle()

    val categoryViewModel: CategoryViewModel = hiltViewModel(parentEntry)
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val categoryName by categorySelectViewModel.categoryName.collectAsStateWithLifecycle()

    var deleteAlert by remember { mutableStateOf(false) }

    var isInitialized by rememberSaveable {
        mutableStateOf(false)
    }
    val currentUser by AuthManager.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(initCategoryId) {
        if (categories.isNotEmpty() && isInitialized == false) {
            if (initCategoryId != null) {
                categorySelectViewModel.selectCategory(initCategoryId)
                onCategorySelect(initCategoryId)
            } else {
                val firstCategory = categories.first().id
                categorySelectViewModel.selectCategory(firstCategory)
                onCategorySelect(firstCategory)
            }
            isInitialized = true
        }
    }

    if (deleteAlert) {
        AlertDialog(
            onDismissRequest = { deleteAlert = false },
            confirmButton = {
                Button(
                    onClick = {
                        if(selectedId != null) {
                            val category = categories.first { it.id == selectedId }
                            try {
                                val firstCategoryId = categories.first { category.id != it.id }.id
                                categorySelectViewModel.selectCategory(firstCategoryId)
                                onCategorySelect(firstCategoryId)
                                onDelete(category.id)
                            } catch (e: Exception) {
                                categorySelectViewModel.selectCategory(null)
                                onCategorySelect(null)
                                onDelete(category.id)
                            }
                            categoryViewModel.deleteCategory(category)
                        }
                        deleteAlert = false
                    },
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(onClick = { deleteAlert = false }) {
                    Text("Отмена")
                }
            },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить этот элемент?") }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier.weight(0.95f),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
        ) {
            items(
                items = categories,
                key = { category -> category.id ?: -1 }
            ) {
                CategoryButton(
                    it,
                    isSelected = it.id == selectedId,
                    onSelect = { id ->
                        categorySelectViewModel.selectCategory(id)
                        onCategorySelect(id)
                    },
                    onDeselect = {
                        categorySelectViewModel.selectCategory(null)
                        onCategorySelect(null)
                    }
                )
            }
        }

        if(selectedId != null) {
            IconButton(
                onClick = {
                    deleteAlert = true
                }
            ) {
                Icon(Icons.Default.Delete, null)
            }
        }

        IconButton(
            onClick = {
                showDialog = true
            }
        ) {
            Icon(Icons.Default.AddCircle, null)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                categoryViewModel.addCategory(
                                    Category(name = categoryName, userId = currentUser.id)
                                )
                                categorySelectViewModel.setCategoryName("")
                            }
                            showDialog = false
                        },
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(text = stringResource(R.string.add))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                title = {
                    Text(stringResource(R.string.new_category), style = MaterialTheme.typography.titleLarge)
                },
                text = {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categorySelectViewModel.setCategoryName(it) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.title),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun CategoryButton(
    category: Category,
    isSelected: Boolean,
    onSelect: (Int) -> Unit = { },
    onDeselect: () -> Unit = { },
) {
    var selected by remember(isSelected) {
        mutableStateOf(isSelected)
    }

    Button(
        modifier = Modifier,
        shape = MaterialTheme.shapes.large,
        onClick = {
            selected = !selected

            if (selected && category.id != null)
                onSelect(category.id)
            else
                onDeselect()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Text(category.name, style = MaterialTheme.typography.bodyLarge)
    }
}