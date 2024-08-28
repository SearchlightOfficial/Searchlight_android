package com.example.hackatonandroid

sealed class BottomNavItem(
    val title: String, val icon: Int, val screenRoute: String
) {
    object Calendar : BottomNavItem("Calendar", 0, "CALENDAR")
    object Timeline : BottomNavItem("TimeLine", 0,"TIMELINE")
    object Analysis : BottomNavItem("Analysis", 0, "ANALYSIS")
    object Settings : BottomNavItem("Setting", 0, "SETTINGS")
}