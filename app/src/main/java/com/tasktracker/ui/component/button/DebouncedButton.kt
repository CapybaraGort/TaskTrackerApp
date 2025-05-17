package com.tasktracker.ui.component.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.delay

@Composable
fun DebouncedButton(
    modifier: Modifier = Modifier,
    debounceTimeMillis: Long = 400L,
    onClick: () -> Unit,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    content: @Composable RowScope.() -> Unit
) {
    var isClickable by remember { mutableStateOf(true) }

    Button(
        shape = shape,
        colors = colors,
        modifier = modifier,
        enabled = isClickable,
        onClick = {
            if (isClickable) {
                onClick()
                isClickable = false
            }
        },
        content = content
    )

    if (!isClickable) {
        LaunchedEffect(Unit) {
            delay(debounceTimeMillis)
            isClickable = true
        }
    }
}

@Composable
fun DebouncedIconButton(
    modifier: Modifier = Modifier,
    debounceTimeMillis: Long = 400L,
    onClick: () -> Unit,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    content: @Composable (() -> Unit)
) {
    var isClickable by remember { mutableStateOf(true) }

    IconButton(
        enabled = isClickable,
        modifier = modifier,
        onClick = {
            if(isClickable) {
                onClick()
                isClickable = false
            }
        },
        colors = colors,
        content = content
    )

    if (!isClickable) {
        LaunchedEffect(Unit) {
            delay(debounceTimeMillis)
            isClickable = true
        }
    }
}

@Composable
fun DebouncedOutlinedButton(
    modifier: Modifier = Modifier,
    debounceTimeMillis: Long = 400L,
    onClick: () -> Unit,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    content: @Composable RowScope.() -> Unit
) {
    var isClickable by remember { mutableStateOf(true) }

    OutlinedButton(
        shape = shape,
        colors = colors,
        modifier = modifier,
        enabled = isClickable,
        onClick = {
            if (isClickable) {
                onClick()
                isClickable = false
            }
        },
        content = content
    )

    if (!isClickable) {
        LaunchedEffect(Unit) {
            delay(debounceTimeMillis)
            isClickable = true
        }
    }
}