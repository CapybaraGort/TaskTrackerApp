package com.tasktracker.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tasktracker.ui.Screen
import com.tasktracker.ui.view.screen.AddTaskScreen
import com.tasktracker.ui.view.screen.AuthScreen
import com.tasktracker.ui.view.screen.EditTaskScreen
import com.tasktracker.ui.view.screen.TaskScreen
import com.tasktracker.viewModel.SharedTaskViewModel
import com.tasktracker.viewModel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.AuthScreen.route) {

        composable(route = Screen.Home.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val sharedTaskViewModel: SharedTaskViewModel = hiltViewModel(parentEntry)
            val taskViewModel: TaskViewModel = hiltViewModel(parentEntry)

            TaskScreen(
                onTaskClick = { task ->
                    sharedTaskViewModel.selectTask(task)
                },
                navController = navController,
                taskViewModel = taskViewModel,
                parentEntry = parentEntry
            )
        }
        composable(route = Screen.AddTask.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home.route)
            }

            val taskViewModel: TaskViewModel = hiltViewModel(parentEntry)
            AddTaskScreen(
                goBack = {
                    navController.popBackStack()
                },
                addTask = { task ->
                    taskViewModel.addTask(task)
                },
                parentEntry = parentEntry
            )
        }

        composable(
            route = Screen.UpdateTask.route,
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val sharedTaskViewModel: SharedTaskViewModel = hiltViewModel(parentEntry)
            val taskViewModel: TaskViewModel = hiltViewModel(parentEntry)

            EditTaskScreen(
                sharedTaskViewModel = sharedTaskViewModel,
                goBack = {
                    navController.popBackStack()
                },
                deleteTask = {
                    taskViewModel.deleteTask(it)
                },
                updateTask = {
                    taskViewModel.updateTask(it)
                },
                parentEntry = parentEntry
            )
        }

        composable(route = Screen.AuthScreen.route) {
            AuthScreen(navController)
        }
    }
}