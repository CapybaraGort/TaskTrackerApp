package com.tasktracker.ui.view.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.tasktracker.R
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthType
import com.tasktracker.domain.entity.Task
import com.tasktracker.ui.Screen
import com.tasktracker.ui.component.ShimmerTaskPlaceholder
import com.tasktracker.ui.component.filter.TaskFilter
import com.tasktracker.ui.component.TaskView
import com.tasktracker.ui.component.button.DebouncedButton
import com.tasktracker.viewModel.CategoryViewModel
import com.tasktracker.viewModel.NavigationEvent
import com.tasktracker.viewModel.NavigationViewModel
import com.tasktracker.viewModel.SelectTaskViewModel
import com.tasktracker.viewModel.SideBarViewModel
import com.tasktracker.viewModel.TaskUiState
import com.tasktracker.viewModel.TaskViewModel
import com.tasktracker.viewModel.filter.TaskFilterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    navController: NavHostController,
    parentEntry: NavBackStackEntry,
    taskViewModel: TaskViewModel
) {
    val taskFilterViewModel: TaskFilterViewModel = viewModel()
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val navEvent by navigationViewModel.navigation.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val taskUiState by taskViewModel.taskUiState.collectAsStateWithLifecycle()
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()

    val filteredTasks by taskFilterViewModel.filteredTasks(taskViewModel.tasks)
        .collectAsStateWithLifecycle(emptyList())

    val completedTasks by remember(filteredTasks) {
        derivedStateOf { filteredTasks.filter { it.isCompleted } }
    }
    val notCompletedTasks by remember(filteredTasks) {
        derivedStateOf { filteredTasks.filter { !it.isCompleted } }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var taskRoute by rememberSaveable {
        mutableStateOf(TaskRoutes.ALL)
    }

    LaunchedEffect(navEvent) {
        when (navEvent) {
            NavigationEvent.Back -> Unit
            NavigationEvent.Idle -> Unit
            is NavigationEvent.Navigate -> {
                navController.navigate((navEvent as NavigationEvent.Navigate).route)
                navigationViewModel.reset()
            }
            is NavigationEvent.PopUpTo -> {
                navController.navigate((navEvent as NavigationEvent.PopUpTo).route) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                }
                navigationViewModel.reset()
            }
        }
    }

    SideBar(drawerState, {
        navigationViewModel.popUpTo(Screen.AuthScreen.route)
    }) {
        Scaffold(
            modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
            floatingActionButton = {
                FloatingActionButton {
                    navigationViewModel.navigate(Screen.AddTask.route)
                }
            },
            topBar = {
                TasksTopBar(taskViewModel, onSideBarClick = {
                    scope.launch {
                        drawerState.open()
                    }
                })
            },
            bottomBar = {
                TasksBottomBar(currentRoute = taskRoute, onSelect = { route ->
                    taskRoute = route
                })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                TasksHeader(
                    titleId = when (taskRoute) {
                        TaskRoutes.ALL -> R.string.all
                        TaskRoutes.COMPLETED -> R.string.done
                        TaskRoutes.NOT_COMPLETED -> R.string.planned
                    },
                    parentEntry = parentEntry,
                    tasksSize = when(taskRoute) {
                        TaskRoutes.ALL ->  filteredTasks.size
                        TaskRoutes.NOT_COMPLETED -> notCompletedTasks.size
                        TaskRoutes.COMPLETED -> completedTasks.size
                    }
                ) {
                    when (taskUiState) {
                        is TaskUiState.Error -> {}
                        TaskUiState.Loading -> {
                            repeat(tasks.size) {
                                ShimmerTaskPlaceholder()
                                Spacer(Modifier.height(6.dp))
                            }
                        }

                        is TaskUiState.Success -> {
                            TasksColumn(
                                tasks = when (taskRoute) {
                                    TaskRoutes.ALL -> filteredTasks
                                    TaskRoutes.NOT_COMPLETED -> notCompletedTasks
                                    TaskRoutes.COMPLETED -> completedTasks
                                },
                                onTaskClick = {
                                    onTaskClick(it)
                                    navigationViewModel.navigate(Screen.UpdateTask.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksHeader(
    tasksSize: Int,
    @StringRes titleId: Int,
    parentEntry: NavBackStackEntry,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titleId) + " (${tasksSize})" + ":",
            style = MaterialTheme.typography.headlineSmall
        )
        TaskFilter(parentEntry = parentEntry)
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )
    content()
}

@Composable
private fun TasksColumn(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit
) {
    val taskViewModel: TaskViewModel = hiltViewModel()
    val selectTaskViewModel: SelectTaskViewModel = viewModel()
    val selectedTasks by selectTaskViewModel.selectedTasks.collectAsStateWithLifecycle()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val categoryMap by remember {
        derivedStateOf {
            categories.associateBy { it.id }
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        reverseLayout = true
    ) {
        items(
            items = tasks,
            key = { task -> task.id ?: 0 }
        ) { task ->
            TaskView(
                task = task,
                isSelected = selectedTasks.contains(task),
                onClick = {
                    if (selectedTasks.isNotEmpty()) {
                        selectTaskViewModel.toggleTaskSelection(task)
                    } else {
                        onTaskClick(task)
                    }
                },
                onLongClick = {
                    selectTaskViewModel.toggleTaskSelection(task)
                },
                onCheckBoxClick = { checked ->
                    taskViewModel.updateTask(task.copy(isCompleted = checked))
                },
                categoryName = categoryMap[task.categoryId]?.name ?: "None"
            )
        }
    }
}

@Composable
private fun TasksTopBar(taskViewModel: TaskViewModel, onSideBarClick: () -> Unit) {
    val selectTaskViewModel: SelectTaskViewModel = viewModel()
    val selectedTasks by selectTaskViewModel.selectedTasks.collectAsState()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        if (selectedTasks.isNotEmpty()) {
            IconButton(onClick = {
                selectTaskViewModel.clearSelection()
            }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
            }

            IconButton(onClick = {
                selectedTasks.forEach {
                    taskViewModel.deleteTask(it)
                    selectTaskViewModel.clearSelection()
                }
            }) {
                Icon(Icons.Outlined.Delete, null)
            }
        } else {
            IconButton(onClick = onSideBarClick) {
                Icon(Icons.Default.Menu, null)
            }
        }
    }
}

@Composable
private fun TasksBottomBar(currentRoute: TaskRoutes, onSelect: (TaskRoutes) -> Unit = { }) {
    val navItems = remember {
        listOf(
            NavigationItem(
                route = TaskRoutes.ALL,
                icon = {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.notepad_filled_icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.all))
                }
            ),
            NavigationItem(
                route = TaskRoutes.NOT_COMPLETED,
                icon = {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.select_all_off_icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.planned))
                }
            ),
            NavigationItem(
                route = TaskRoutes.COMPLETED,
                icon = {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.select_all_on_icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.done))
                }
            )
        )
    }

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = item.route == currentRoute,
                onClick = {
                    onSelect(item.route)
                },
                icon = item.icon,
                label = item.label
            )
        }
    }
}

private data class NavigationItem(
    val route: TaskRoutes,
    val icon: @Composable (() -> Unit),
    val label: @Composable (() -> Unit)
)

private enum class TaskRoutes {
    ALL, NOT_COMPLETED, COMPLETED
}

@Composable
private fun SideBar(
    drawerState: DrawerState,
    navToAuthScreen: () -> Unit,
    content: @Composable (() -> Unit)
) {
    val sideBarViewModel: SideBarViewModel = hiltViewModel()
    val currentAuthType by sideBarViewModel.authType.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val currentUser by AuthManager.currentUser.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars),
        drawerState = drawerState,
        content = content,
        drawerContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(DrawerDefaults.MaximumDrawerWidth / 1.5f)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(horizontal = 16.dp)
            ) {
                Column{
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "${stringResource(R.string.user)}: ${currentUser.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                DebouncedButton(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = {
                        scope.launch {
                            sideBarViewModel.authManager.logout(AuthManager.currentAuthType.value)
                            withContext(Dispatchers.Main) {
                                navToAuthScreen()
                            }
                        }
                    }) {
                    Text(
                        text = stringResource(
                            if (currentAuthType == AuthType.Anonymous) R.string.enter
                            else R.string.exit
                        )
                    )
                }
            }
        }
    )
}

@Composable
private fun FloatingActionButton(goToAddTaskScreen: () -> Unit) {
    DebouncedButton(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.size(64.dp),
        onClick = { goToAddTaskScreen() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.plus_36),
            contentDescription = null
        )
    }
}