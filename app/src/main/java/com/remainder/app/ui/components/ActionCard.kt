package com.remainder.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.presentation.common.displayLabel
import com.remainder.app.ui.theme.RemainderTheme
import com.remainder.app.ui.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ActionCard(
    action: Action,
    categoryName: String?,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RemainderCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = action.isCompleted, onCheckedChange = { onToggleComplete() })
            Spacer(Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (action.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (action.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Spacer(Modifier.height(Spacing.xs))
                val metadata = listOfNotNull(action.scheduledTime.displayLabel(), categoryName)
                    .joinToString(" • ")
                Text(
                    text = metadata,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            PriorityIndicator(priority = action.priority)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionCardPreview() {
    RemainderTheme {
        ActionCard(
            action = Action(
                title = "Call client",
                scheduledDate = LocalDate.now(),
                scheduledTime = LocalTime.of(10, 30),
                reminderOffset = ReminderOffset.MIN_15,
                repeatType = RepeatType.NEVER,
                categoryId = 1,
                priority = Priority.HIGH,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            categoryName = "Work",
            onClick = {},
            onToggleComplete = {},
        )
    }
}
