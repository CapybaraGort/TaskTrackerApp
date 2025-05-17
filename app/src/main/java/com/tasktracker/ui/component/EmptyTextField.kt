package com.tasktracker.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle

@Composable
fun EmptyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String = "",
    hintTextStyle: TextStyle = LocalTextStyle.current,
    textStyle: TextStyle = LocalTextStyle.current,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        maxLines = maxLines,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = hintText,
                style = hintTextStyle
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
        ),
        shape = RectangleShape,
        textStyle = textStyle
    )
}