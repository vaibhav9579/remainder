package com.remainder.app.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.remainder.app.presentation.action.ActionDetailsScreen
import com.remainder.app.presentation.action.AddActionScreen
import com.remainder.app.presentation.calendar.CalendarScreen
import com.remainder.app.presentation.completed.CompletedScreen
import com.remainder.app.presentation.home.HomeScreen
import com.remainder.app.presentation.settings.SettingsScreen
import com.remainder.app.ui.theme.FabShape

private data class BottomNavItem(
    val destination: Destination,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(Destination.Home, "Home", Icons.Default.Home),
    BottomNavItem(Destination.Calendar, "Calendar", Icons.Default.DateRange),
    BottomNavItem(Destination.Completed, "Completed", Icons.Default.CheckCircle),
    BottomNavItem(Destination.Settings, "Settings", Icons.Default.Settings),
)

private val chromeHiddenRoutes = setOf(Destination.AddAction.route, Destination.ActionDetails.route)

@Composable
fun RemainderApp(initialActionId: Long? = null) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showChrome = currentRoute !in chromeHiddenRoutes

    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(initialActionId) {
        if (initialActionId != null) {
            navController.navigate(Destination.ActionDetails.createRoute(initialActionId))
        }
    }

    Scaffold(
        bottomBar = {
            if (showChrome) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.destination.route,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showChrome) {
                FloatingActionButton(
                    onClick = { navController.navigate(Destination.AddAction.createRoute()) },
                    shape = FabShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add action")
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    onOpenAction = { id -> navController.navigate(Destination.ActionDetails.createRoute(id)) },
                )
            }
            composable(Destination.Calendar.route) { CalendarScreen() }
            composable(Destination.Completed.route) {
                CompletedScreen(
                    onOpenAction = { id -> navController.navigate(Destination.ActionDetails.createRoute(id)) },
                )
            }
            composable(Destination.Settings.route) { SettingsScreen() }
            composable(
                route = Destination.AddAction.route,
                arguments = listOf(
                    navArgument(Destination.AddAction.ARG_ACTION_ID) {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) {
                AddActionScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(
                route = Destination.ActionDetails.route,
                arguments = listOf(
                    navArgument(Destination.ActionDetails.ARG_ACTION_ID) { type = NavType.LongType },
                ),
            ) {
                ActionDetailsScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Destination.AddAction.createRoute(id)) },
                    onDeleted = { navController.popBackStack() },
                )
            }
        }
    }
}
