package com.tasktracker.ui.component.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CustomOutlinedButton(modifier: Modifier = Modifier, onClick: () -> Unit, text: String) {
    DebouncedOutlinedButton(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .padding(bottom = 12.dp, start = 32.dp, end = 32.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}