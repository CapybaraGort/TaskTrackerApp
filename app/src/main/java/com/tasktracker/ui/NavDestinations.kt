package com.tasktracker.ui

sealed class Screen(val route: String) {

    object Home : Screen("home")

    object AddTask : Screen("add_task")

    object UpdateTask : Screen("update_task")

    object AuthScreen : Screen("auth")
}
