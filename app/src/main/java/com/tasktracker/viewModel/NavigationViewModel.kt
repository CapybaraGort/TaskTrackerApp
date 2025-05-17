package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _navigation = MutableStateFlow<NavigationEvent>(NavigationEvent.Idle)
    val navigation get() = _navigation.asStateFlow()

    fun navigate(route: String) {
        _navigation.value = NavigationEvent.Navigate(route)
    }

    fun goBack() {
        _navigation.value = NavigationEvent.Back
    }

    fun reset() {
        _navigation.value = NavigationEvent.Idle
    }
}


sealed class NavigationEvent {
    data class Navigate(val route: String) : NavigationEvent()
    object Back : NavigationEvent()
    object Idle : NavigationEvent()
}