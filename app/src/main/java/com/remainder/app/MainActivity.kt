package com.remainder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.navigation.RemainderApp
import com.remainder.app.presentation.common.AppThemeViewModel
import com.remainder.app.ui.theme.RemainderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialActionId = intent?.getLongExtra(EXTRA_OPEN_ACTION_ID, -1L)?.takeIf { it != -1L }
        setContent {
            val themeViewModel: AppThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            RemainderTheme(themeMode = themeMode) {
                RemainderApp(initialActionId = initialActionId)
            }
        }
    }

    companion object {
        const val EXTRA_OPEN_ACTION_ID = "extra_open_action_id"
    }
}
