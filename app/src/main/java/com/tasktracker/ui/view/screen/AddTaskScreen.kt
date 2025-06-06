package com.tasktracker.ui.view.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.tasktracker.R
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.domain.entity.Task
import com.tasktracker.ui.component.CategoriesRow
import com.tasktracker.ui.component.DateTimePickView
import com.tasktracker.ui.component.DateTimePickerDialog
import com.tasktracker.ui.component.EmptyTextField
import com.tasktracker.ui.component.button.CustomOutlinedButton
import com.tasktracker.ui.component.button.DebouncedIconButton
import com.tasktracker.viewModel.NavigationEvent
import com.tasktracker.viewModel.NavigationViewModel
import com.tasktracker.viewModel.TaskEditViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit,
    addTask: (Task) -> Unit,
    parentEntry: NavBackStackEntry
) {
    val taskEditViewModel: TaskEditViewModel = viewModel()
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val navEvent by navigationViewModel.navigation.collectAsStateWithLifecycle()

    val title by taskEditViewModel.title.collectAsStateWithLifecycle()
    val description by taskEditViewModel.description.collectAsStateWithLifecycle()
    val deadline by taskEditViewModel.deadline.collectAsStateWithLifecycle()
    val begin by taskEditViewModel.begin.collectAsStateWithLifecycle()
    val selectedCategoryId by taskEditViewModel.categoryId.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val alertToast = remember(context) {
        Toast.makeText(
            context,
            context.getString(R.string.not_all_fields_filled_in),
            Toast.LENGTH_SHORT
        )
    }

    var showDeadlineDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showBeginDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yy HH:mm") }
    val currentUser by AuthManager.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(navEvent) {
        when (navEvent) {
            NavigationEvent.Back -> {
                goBack()
                navigationViewModel.reset()
            }

            NavigationEvent.Idle -> Unit
            is NavigationEvent.Navigate -> Unit
            is NavigationEvent.PopUpTo -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                DebouncedIconButton(
                    onClick = {
                        navigationViewModel.goBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }

            }
        },
        bottomBar = {
            CustomOutlinedButton(
                text = stringResource(R.string.add),
                onClick = {
                    if (title.isNotEmpty() && selectedCategoryId != null) {
                        val task = Task(
                            title = title,
                            description = description,
                            deadline = deadline,
                            isCompleted = false,
                            categoryId = selectedCategoryId ?: -1,
                            userId = currentUser.id,
                            begin = begin
                        )
                        addTask(task)
                        navigationViewModel.goBack()
                    } else {
                        alertToast.show()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                EmptyTextField(
                    modifier = Modifier,
                    value = title,
                    onValueChange = { taskEditViewModel.setTitle(it) },
                    hintText = stringResource(R.string.title),
                    textStyle = MaterialTheme.typography.titleLarge,
                    maxLines = 3
                )
                HorizontalDivider(Modifier.fillMaxWidth())
                EmptyTextField(
                    modifier = Modifier,
                    value = description,
                    onValueChange = { taskEditViewModel.setDescription(it) },
                    hintText = stringResource(R.string.description),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    hintTextStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 5
                )

                Spacer(Modifier.height(10.dp))
                Text(text = stringResource(R.string.categories), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp))

                CategoriesRow(onCategorySelect = { id ->
                    taskEditViewModel.setCategoryId(id)
                }, parentEntry = parentEntry)

                Spacer(Modifier.height(16.dp))
                Text(text = stringResource(R.string.terms), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.begin)}: ${begin.format(formatter)}" ,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    DateTimePickView(onClick = { showBeginDialog = !showBeginDialog }) {
                        if (showBeginDialog) {
                            DateTimePickerDialog(
                                dateTime = begin,
                                onDismissRequest = { showBeginDialog = false },
                                onDateTimePicked = { newDateTime ->
                                    taskEditViewModel.setBegin(newDateTime)
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.deadline)}: ${deadline.format(formatter)}" ,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    DateTimePickView(onClick = { showDeadlineDialog = !showDeadlineDialog }) {
                        if (showDeadlineDialog) {
                            DateTimePickerDialog(
                                dateTime = deadline,
                                onDismissRequest = { showDeadlineDialog = false },
                                onDateTimePicked = { newDateTime ->
                                    taskEditViewModel.setDeadline(newDateTime)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}