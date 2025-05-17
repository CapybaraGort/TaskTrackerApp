package com.tasktracker.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DateTimePickView(onClick: () -> Unit, dateTimePicker: @Composable (() -> Unit)) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = Icons.Default.DateRange,
            contentDescription = null
        )
    }
    dateTimePicker()
}