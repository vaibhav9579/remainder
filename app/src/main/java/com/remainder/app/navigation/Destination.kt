package com.remainder.app.navigation

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Calendar : Destination("calendar")
    data object Completed : Destination("completed")
    data object Settings : Destination("settings")
    data object Search : Destination("search")

    data object AddAction : Destination("add_action?actionId={actionId}") {
        const val ARG_ACTION_ID = "actionId"
        fun createRoute(actionId: Long? = null) = "add_action?actionId=${actionId ?: -1L}"
    }

    data object ActionDetails : Destination("action_details/{actionId}") {
        const val ARG_ACTION_ID = "actionId"
        fun createRoute(actionId: Long) = "action_details/$actionId"
    }
}
