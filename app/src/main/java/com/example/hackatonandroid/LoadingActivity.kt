package com.example.hackatonandroid

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.hackatonandroid.ui.theme.Gray10
import com.example.hackatonandroid.ui.theme.Gray20
import com.example.hackatonandroid.ui.theme.Gray50
import com.example.hackatonandroid.ui.theme.Gray60
import com.example.hackatonandroid.ui.theme.Gray70
import com.example.hackatonandroid.ui.theme.Green
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

class LoadingActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackatonAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    LoadingLayout()
                }
            }
        }
        getMylocation()

    }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                it.key in locationPermissions
            }

            if (responsePermissions.filter { it.value == true }.size == locationPermissions.size) {
                setLocationListener()
            } else {
                Toast.makeText(this, "no", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getMylocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnable) {
            permissionLauncher.launch(locationPermissions)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        postPatientData(location.latitude.toString(),location.longitude.toString())

    }

    @Suppress("MissingPermission")
    private fun setLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )

            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast
                .makeText(this@LoadingActivity, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT)
                .show()


            removeLocationListener()
        }

        private fun removeLocationListener() {
            if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
                locationManager.removeUpdates(myLocationListener)
            }
        }
    }



    private fun postPatientData(latitude: String, longtitude: String) {
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val carNumber = prefs.getString("carNumber", "").toString()
        if(carNumber.isEmpty()){
            return
        }
        val patient = Patient(
            message = intent.getStringExtra("message").toString(),
            age = intent.getIntExtra("age", 0),
            grade = when (intent.getStringExtra("main")) {
                "긴급 환자" -> 1
                "응급 환자" -> 2
                "비응급 환자" -> 0
                else -> 101
            },
            disease = intent.getStringExtra("sub").toString(),
            gender = if (intent.getStringExtra("sex").toString() == "남성") {
                0
            } else {
                1
            },
            current_latitude = latitude,
            current_longitude = longtitude,
            car_number = prefs.getString("carNumber", "").toString()
        )

        Log.d(TAG, "postPatientData: $latitude")
        CoroutineScope(Dispatchers.IO).launch {
            val service = RetrofitClient.retrofit().create(MainService::class.java)

            val data = service.postPatient(patient = patient)
            if(!data.isSuccessful){
                Log.d(TAG, "postPatientData: ${data.code()} $patient")
            }else{
                Log.d(TAG, "postPatientData: ${data.code()} $patient")

            }
        }
    }


}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoadingLayout(ageState:Int, sexState:String, emergencyState:List<Boolean>, ktasState:String) {
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .background(color = Gray60)) {
        Box(modifier = Modifier.fillMaxSize().background(Gray60)) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.contents),
                    contentDescription = "Searching",
                    modifier = Modifier
                        .width(224.dp)
                        .height(224.dp)
                )

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp)
                        .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                        .padding(vertical = 8.dp)
                ){
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .background(color = Gray70, shape = RoundedCornerShape(12.dp))

                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "환자 추정 나이",
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray20
                        )

                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End){
                            Text(
                                text = "${ageState}세",
                                fontFamily = suit,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Gray10
                            )
                        }

                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "환자 성별",
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray20
                        )


                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End){
                            Text(
                                text = sexState,
                                fontFamily = suit,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Gray10
                            )
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "응급실 종류",
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray20
                        )

                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End){
                            Text(
                                text = emergencyString(emergencyState),
                                fontFamily = suit,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Gray10
                            )
                        }

                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KTAS",
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray20
                        )

                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End){
                            Text(
                                text = ktasState,
                                fontFamily = suit,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Gray10
                            )
                        }

                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    HackatonAndroidTheme {
        LoadingLayout(20,"여성",
            listOf(true,true,true,false,false,false,false,false),
            "10",
            )

    }
}