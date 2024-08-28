package com.example.hackatonandroid.data.remote

data class GetHospitalDAO(
    val juso: String,
    val lat: String,
    val lon: String,
    val rltmEmerCds: List<String>
)