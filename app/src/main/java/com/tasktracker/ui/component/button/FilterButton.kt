package com.tasktracker.ui.component.button

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun FilterButton(
    isSelected: Boolean,
    text: String,
    onSelect: () -> Unit = { },
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

            if (selected)
                onSelect()
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
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}