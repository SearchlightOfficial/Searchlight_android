package com.example.hackatonandroid.data.remote

import com.example.hackatonandroid.Bed

data class GetHospitalDTO(
    val address: String,
    val beds: List<Bed>,
    val distance: String,
    val emergencyMessage: List<String>,
    val impossibleMessage: List<String>,
    val name: String
)