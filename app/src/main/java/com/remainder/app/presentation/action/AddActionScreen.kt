package com.remainder.app.presentation.action

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.remainder.app.ui.components.RemainderPrimaryButton
import com.remainder.app.ui.components.RemainderTextField
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.RemainderTheme
import com.remainder.app.ui.theme.Spacing

// Shell only — date/time/reminder/repeat/category/priority pickers are
// added in Phase 4 once the Action model and use cases exist. Save is not
// yet wired to persistence.
@Composable
fun AddActionScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Add Action", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontal),
        ) {
            Spacer(Modifier.height(Spacing.lg))
            RemainderTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                placeholder = "e.g. Call client",
            )
            Spacer(Modifier.height(Spacing.lg))
            RemainderTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (optional)",
                singleLine = false,
            )
            Spacer(Modifier.height(Spacing.xxl))
            RemainderPrimaryButton(
                text = "Save Action",
                onClick = onSave,
                enabled = title.isNotBlank(),
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddActionScreenPreview() {
    RemainderTheme {
        AddActionScreen(onBack = {}, onSave = {})
    }
}
