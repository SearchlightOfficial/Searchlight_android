package com.example.hackatonandroid

sealed class NavigationItem(
    val title: String, val screenRoute: String
) {
    object FirstScreen : NavigationItem("Register",  "REGISTER")
    object LoadingScreen : NavigationItem("Loading", "LOADING")
    object SecondScreen : NavigationItem("Hospital", "HOSPITAL")
    object ThirdScreen : NavigationItem("Transfer",  "TRANSFER")
}