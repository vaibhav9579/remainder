package com.remainder.app.navigation

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Calendar : Destination("calendar")
    data object Completed : Destination("completed")
    data object Settings : Destination("settings")
    data object AddAction : Destination("add_action")
}
