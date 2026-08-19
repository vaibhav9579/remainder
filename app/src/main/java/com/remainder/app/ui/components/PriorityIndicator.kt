package com.remainder.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.remainder.app.domain.model.Priority
import com.remainder.app.ui.theme.RemainderExtendedTheme

// A small, deliberately subtle dot — priority should never dominate the
// card the way a Jira-style badge would. Color alone isn't accessible, so
// it carries a content description for screen readers too.
@Composable
fun PriorityIndicator(priority: Priority, modifier: Modifier = Modifier) {
    val color = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> RemainderExtendedTheme.colors.warning
        Priority.LOW -> MaterialTheme.colorScheme.outline
    }
    val label = when (priority) {
        Priority.HIGH -> "High priority"
        Priority.MEDIUM -> "Medium priority"
        Priority.LOW -> "Low priority"
    }
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
            .semantics { contentDescription = label },
    )
}
