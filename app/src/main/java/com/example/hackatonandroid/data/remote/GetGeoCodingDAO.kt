package com.example.hackatonandroid.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetGeoCodingDAO(
    val lat: Double,
    val lon: Double
)
