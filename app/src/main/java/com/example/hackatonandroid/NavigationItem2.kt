package com.example.hackatonandroid

sealed class NavigationItem2(
    val title: String, val screenRoute: String
) {
    object SecondScreen : NavigationItem2("Hospital", "HOSPITAL")
    object ThirdScreen : NavigationItem2("Transfer",  "TRANSFER")
}