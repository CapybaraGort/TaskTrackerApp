package com.tasktracker.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tasktracker.R
import com.tasktracker.domain.entity.Task
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskView(
    modifier: Modifier = Modifier,
    task: Task,
    isSelected: Boolean,
    onClick: (Task) -> Unit,
    onLongClick: () -> Unit,
    onCheckBoxClick: (Boolean) -> Unit,
    categoryName: String
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isSelected)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainer
            )
            .combinedClickable(
                onClick = { onClick(task) },
                onLongClick = { onLongClick() }
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp),
                        checked = task.isCompleted,
                        onCheckedChange = onCheckBoxClick
                    )
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (task.description.isNotEmpty()) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Text(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = "${stringResource(R.string.begin)}: ${task.begin.format(dateFormatter)} ${
                        stringResource(
                            R.string.at
                        )
                    } ${task.deadline.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    modifier = Modifier,
                    text = "${stringResource(R.string.deadline)}: ${
                        task.deadline.format(
                            dateFormatter
                        )
                    } ${
                        stringResource(
                            R.string.at
                        )
                    } ${task.deadline.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}