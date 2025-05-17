package com.tasktracker.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tasktracker.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    dateTime: LocalDateTime,
    onDateTimePicked: (LocalDateTime) -> Unit,
    onDismissRequest: () -> Unit
) {
    var date by rememberSaveable {
        mutableStateOf(dateTime)
    }
    DatePick(
        initialDate = dateTime.toLocalDate(),
        onConfirm = { state ->
            val d = Instant.ofEpochMilli(state.selectedDateMillis ?: 0)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            date = date.withYear(d.year).withMonth(d.monthValue).withDayOfMonth(d.dayOfMonth)
            onDateTimePicked(date)
        },
        onDismiss = onDismissRequest
    ) {
        TimePick(
            initialTime = date.toLocalTime(),
            onConfirm = { state ->
                date = date.withHour(state.hour).withMinute(state.minute).withSecond(0)
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePick(
    initialDate: LocalDate,
    onConfirm: (DatePickerState) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    DatePickerDialog(
        modifier = Modifier.fillMaxHeight(),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(datePickerState)
                onDismiss()
            }) {
                Text("ОК")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            content()
            DatePicker(
                state = datePickerState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePick(
    initialTime: LocalTime,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit = { },
) {
    var showDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )

    var showDial by remember { mutableStateOf(true) }

    val toggleIcon = if (showDial) {
        Icons.Filled.Edit
    } else {
        Icons.Filled.Search
    }

    OutlinedTextField(
        value = initialTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = {},
        label = { Text("Выберите время") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { showDialog = true },
        enabled = false,
        trailingIcon = { Icon(Icons.Default.DateRange, null) }
    )

    if (showDialog) {
        TimePickerDialog(
            onDismiss = {
                onDismiss()
                showDialog = false
            },
            onConfirm = {
                onConfirm(timePickerState)
                showDialog = false
            },
            toggle = {
                IconButton(onClick = { showDial = !showDial }) {
                    Icon(
                        imageVector = toggleIcon,
                        contentDescription = null,
                    )
                }
            },
        ) {
            if (showDial) {
                androidx.compose.material3.TimePicker(
                    state = timePickerState,
                )
            } else {
                TimeInput(
                    state = timePickerState,
                )
            }
        }
    }

}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Выберите время",
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}