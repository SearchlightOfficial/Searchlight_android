package com.example.hackatonandroid.data.remote

data class PatientDAO (
    val age: Int,
    val gender: Int,
    val bedType: List<String>,
    val ktas: String,
    val latitude: Double,
    val longitude: Double,
    val hospitalName: String
)