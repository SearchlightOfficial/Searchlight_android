package com.example.hackatonandroid

import com.example.hackatonandroid.data.remote.DistanceGetDAO
import com.example.hackatonandroid.data.remote.DistanceResponse
import com.example.hackatonandroid.data.remote.GetGeoCodingDAO
import com.example.hackatonandroid.data.remote.GetGeoCodingDTO
import com.example.hackatonandroid.data.remote.GetHospitalDAO
import com.example.hackatonandroid.data.remote.GetHospitalDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MainService {
    @POST("/ambulances")
    suspend fun postPatient(
        @Header("Authorization") token: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InduYWNucmVlZnV0amd6Ym5va3NwIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY4OTgyNjY2NywiZXhwIjoyMDA1NDAyNjY3fQ.1-988qEssSfAmdbUQhidoibPQl_Ve0RN3o4KclOdM64",
        @Body patient: Patient
    ): Response<List<Hospital>>

    @POST("/ambulances/remain")
    suspend fun getDistance(
        @Header("Authorization") token: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InduYWNucmVlZnV0amd6Ym5va3NwIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY4OTgyNjY2NywiZXhwIjoyMDA1NDAyNjY3fQ.1-988qEssSfAmdbUQhidoibPQl_Ve0RN3o4KclOdM64",
        @Body distanceGetDAO: DistanceGetDAO
    ): Response<DistanceResponse>

    @POST("/hospital/fetch")
    suspend fun getHospital(
        @Body request: GetHospitalDAO
    ): Response<List<GetHospitalDTO>>

    @POST("/geocoding")
    suspend fun getGeocoding(
        @Body request: GetGeoCodingDAO
    ): Response<GetGeoCodingDTO>
}