package com.example.hackatonandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.hackatonandroid.data.remote.GetGeoCodingDAO
import com.example.hackatonandroid.data.remote.GetHospitalDAO
import com.example.hackatonandroid.data.remote.GetHospitalDTO
import com.example.hackatonandroid.data.remote.LocationDAO
import com.example.hackatonandroid.data.remote.PatientDAO
import com.example.hackatonandroid.ui.theme.Gray30
import com.example.hackatonandroid.ui.theme.Gray40
import com.example.hackatonandroid.ui.theme.Gray60
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URISyntaxException

class MainActivity : ComponentActivity() {
    private lateinit var mSocket: Socket
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private lateinit var patient: RegisterStateHolder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(
                color = Color.Transparent
            )
            systemUiController.setStatusBarColor(
                color = Gray40
            )
            var address by remember {
                mutableStateOf("")
            }
            var latState by remember {
                mutableStateOf<Double>(0.0)
            }
            var lonState by remember {
                mutableStateOf<Double>(0.0)
            }

            GetCurrentLocation( {
                latState = it.latitude
                lonState = it.longitude
//                latState = 37.5172852105915
//                lonState = 126.982167988487
            }
                )

            LaunchedEffect(lonState, latState){
                if(latState > 0.0 && lonState > 0.0){
                    getAddress(
                        latState,
                        lonState,
//                    it.latitude, it.longitude,
                        function = {
                            address = it

                        })
                }
            }

            var locationPermissionGranted by remember { mutableStateOf(false) }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    locationPermissionGranted = isGranted
                } else {
                    finish()
                }
            }
            if (!locationPermissionGranted) {
                LaunchedEffect(Unit) {
                    launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    Log.d(TAG, "onCreate:${locationPermissionGranted.toString()}")
                }
            }

            HackatonAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainLayout(
                        onValid = { true },
                        getLocation = {
                            Log.d(TAG, "onCreate: layout")
                            getMyLocation() { address = it }
                        },
                        address = address,
                        lat = latState,
                        lon = lonState,
                        {  },
                        getAddress = {getAddress(latState,lonState,{address = it})},
                        { data,hospital, controller ->
                            socketStart(
                                PatientDAO(data.ageState,
                                getSexNumber(data.sex),
                                emergencyStateToBedsName(data.emergencyState),
                                data.ktasState,
                                latState,
                                lonState,
                                hospital)
                            )

                            CoroutineScope(Dispatchers.Main).launch {
                                var count = 0;
                                while(true){
                                    delay(1000)
                                    if(isSocketConnected()){
                                        socketAmbulanceDisconnect(controller)
                                        sendLocation(latState,lonState)
                                    }else{
                                        count++;
                                    }
                                    if(count > 5){
                                        break
                                    }

                                }
                            }

                        },
                        {socketStop()}
                    )
                }
            }
        }
    }



    private fun getSexNumber(sex:String):Int {
        if(sex == "남성"){
            return 0
        }else if(sex == "여성") {
            return 1
        }else {
            return 2
        }

    }
    private fun getAddress(lat: Double?, lon: Double?, function: (String) -> Unit) {
        if(lat != null && lon != null){
            Log.d(TAG, "getAddress: start")
            val client = RetrofitClient.retrofit().create(MainService::class.java)
            lifecycleScope.launch {
                Log.d(TAG, "getAddress: start2")
                val data = client.getGeocoding(request = GetGeoCodingDAO(lat, lon))
                if (data.isSuccessful) {
                    Log.d(TAG, "getAddress: success${data.body()!!}")
                    function(data.body()!!.address)
                } else {
                    Log.d(TAG, "getAddress: fail")
                }

            }
        }
    }

    private fun carNumberCheck() {
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val carNumber = prefs.getString("carNumber", "")

        if (carNumber!!.isEmpty()) {
            startActivity(Intent(this, CarNumberActivity::class.java))
        }
    }

    private fun getMyLocation(function: (String) -> Unit) {

    }

    private fun socketStart(patient: PatientDAO){
        try {
            mSocket = IO.socket("http://searchlight.kwl.kr/ambulance")
            mSocket.connect()
            val jsonAdapter = RetrofitClient.moshi.adapter(PatientDAO::class.java)

            mSocket.emit("patient_info", jsonAdapter.toJson(patient))
        } catch (e: URISyntaxException) {
            Log.d("ERR", e.toString())
        }

    }

    private fun sendLocation(lat : Double, lon:Double){
        Log.d("send location", "OK send location")

        val jsonAdapter = RetrofitClient.moshi.adapter(LocationDAO::class.java)
        mSocket.emit("update_location", jsonAdapter.toJson(LocationDAO(lat,lon)))
    }

    private fun socketAmbulanceDisconnect(controller: NavController){
        Log.d("ambulance disconnect", "OK")
        mSocket.on("hospital_disconnect_ambulance"){
            mSocket.disconnect()

        }
    }

    private fun socketStop(){
        mSocket.disconnect()
    }

    private fun isSocketConnected():Boolean{
        Log.d(TAG, "socketStop: ${mSocket.connected()}")
        return mSocket.connected()
    }



    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast
                .makeText(
                    this@MainActivity,
                    "${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                )
                .show()


            removeLocationListener()
        }

        private fun removeLocationListener() {
            if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
                locationManager.removeUpdates(myLocationListener)
            }
        }
    }
}


@Composable
fun Header(index: Int, controller: NavController, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        androidx.compose.material3.Icon(painter = painterResource(id = R.drawable.back),
            contentDescription = "back",
            tint = Gray40,
            modifier = Modifier.clickable {
                onBack()
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.setting), contentDescription = "back",
                tint = Gray40,
                modifier = Modifier.background(color = Gray60, shape = RoundedCornerShape(12.dp))
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainLayout(
    onValid: (RegisterStateHolder) -> Boolean,
    getLocation: () -> Unit,
    address: String,
    lat: Double,
    lon: Double,
    onNextToHospital: () -> Unit,
    getAddress: () -> Unit,
    onSocketStart : (RegisterStateHolder,String, NavController) -> Unit,
    onSocketStop : () -> Unit,
) {


    var screenNumberState by remember {
        mutableStateOf(0)
    }
    val navController = rememberAnimatedNavController()
    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            NavigationGraph(navController = navController,
                onNext = { it ->
                    screenNumberState++
                    when (screenNumberState) {
                        0 -> navController.navigate("Register")
                        5 -> navController.navigate("Loading")
                        6 -> navController.navigate("Hospital")
                        7 -> navController.navigate("Transfer")
                    }

                },
                onValid = { it -> onValid(it) },
                getLocation = getLocation,
                screenNumberState,
                address = address,
                lat = lat,
                lon = lon,
                { screenNumberState = it },
                {getAddress()},
                {data, hospital ->
                    onSocketStart(data, hospital,navController)},
                {onSocketStop()}

            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Header(screenNumberState, controller = navController) {
                    if (screenNumberState in 1..4) {
                        screenNumberState--
                        Log.d(TAG, "MainLayout: ${screenNumberState}")
                    }else{
                        if(screenNumberState>0){
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    onNext: (RegisterStateHolder) -> Unit,
    onValid: (RegisterStateHolder) -> Boolean,
    getLocation: () -> Unit,
    screenNumber: Int,
    address: String,
    lat: Double,
    lon: Double,
    setScreenNumber: (Int) -> Unit,
    refreshLocation: () -> Unit,
    onSocketStart : (RegisterStateHolder, String) -> Unit,
    onSocketStop : () -> Unit,
) {
    var registerStateHolder by remember {
        mutableStateOf(
            RegisterStateHolder(
                "",
                1,
                listOf(true, true, true, true, true, true, true, true),
                "",
                ""
            )
        )
    }
    var navigationPosition by remember {
        mutableStateOf(0)
    }
    var hospitalList = remember {
        mutableStateListOf<GetHospitalDTO>()
    }
    var hospital by remember{
        mutableStateOf(
            GetHospitalDTO(
            "",
            listOf(Bed("1 / 1", "일반")),
            "",
            listOf(),
            listOf(),
            ""
        )
        )
    }
    AnimatedNavHost(
        navController = navController, startDestination = NavigationItem.FirstScreen.screenRoute
        , modifier = Modifier.statusBarsPadding()
    ) {

        composable(
            route = NavigationItem.FirstScreen.screenRoute,
            enterTransition = { fadeIn(initialAlpha = 1f) }, exitTransition = {
                fadeOut(targetAlpha = 1f)
            }) {
            LaunchedEffect(Unit) {
                setScreenNumber(0)
            }
            RegisterLayout(address = address, screenNumber = screenNumber, onNext = {
                onNext(it)
                registerStateHolder = it
            }, onValid = onValid, {refreshLocation()})
            getLocation()
        }
        composable(
            NavigationItem.LoadingScreen.screenRoute,
            enterTransition = { fadeIn(initialAlpha = 1f) }, exitTransition = {
                fadeOut(targetAlpha = 1f)
            }
        ) {
            setScreenNumber(5)
            LoadingLayout(
                registerStateHolder.ageState,
                registerStateHolder.sex,
                registerStateHolder.emergencyState,
                registerStateHolder.ktasState,
            )
            Log.d(TAG, "NavigationGraph: com")
            LaunchedEffect(Unit) {
                getHospital(lat = lat, lon = lon, registerStateHolder = registerStateHolder) {
                    CoroutineScope(Dispatchers.Main).launch {
                        hospitalList.clear()
                        hospitalList.addAll(it.take(3).toMutableStateList())
                        Log.d(TAG, "NavigationGraph: ${hospitalList[0]}")
                        onNext(registerStateHolder)
                    }
                }
            }
        }
        composable(NavigationItem.SecondScreen.screenRoute,
            enterTransition = { fadeIn(initialAlpha = 1f) }, exitTransition = {
                fadeOut(targetAlpha = 1f)
            }) {
            setScreenNumber(6)
            HospitalLayout(list = hospitalList) {
                hospital = hospitalList[it]
                onNext(registerStateHolder)
            }
        }
        composable(NavigationItem.ThirdScreen.screenRoute,
            enterTransition = { fadeIn(initialAlpha = 1f) }, exitTransition = {
                fadeOut(targetAlpha = 1f)
            }){
            setScreenNumber(7)
            LaunchedEffect (Unit){
                onSocketStart(registerStateHolder,hospital.name)
            }
            TransferScreen(hospital = hospital, patient = registerStateHolder,0.0f,{
                navController.popBackStack()
                onSocketStop()
            },{
                navController.popBackStack()
                navController.popBackStack()
                navController.popBackStack()
                onSocketStop()
            })
        }
    }
}

fun getHospital(
    lat: Double, lon: Double, registerStateHolder: RegisterStateHolder,
    onNext: (List<GetHospitalDTO>) -> Unit
) {

    val client = RetrofitClient.retrofit().create(MainService::class.java)
    CoroutineScope(Dispatchers.IO).launch {
        val rltmEmerCds = emergencyStateToBedsCode(registerStateHolder.emergencyState)
        val data = client.getHospital(
            GetHospitalDAO(
                "",
                lat = lat.toString(),
                lon = lon.toString(),
                rltmEmerCds = rltmEmerCds
            )
        )
        if (data.isSuccessful) {
            onNext(data.body()!!)
        } else {
            Log.d(TAG, "getHospital: ${data.message()}")
        }
    }
}

private fun emergencyStateToBedsCode(emergencyState: List<Boolean>): MutableList<String> {
    val emergencyCode = listOf("O001", "O059", "O003", "O004", "O060", "O002", "O048", "O049")
    val rltmEmerCds = mutableListOf<String>()

    emergencyState.forEachIndexed { index, b ->
        if (b) {
            rltmEmerCds.add(emergencyCode[index])
        }
    }
    return rltmEmerCds
}

private fun emergencyStateToBedsName(emergencyState: List<Boolean>): MutableList<String> {
    val textList = listOf("일반", "코호트 격리", "음압격리", "일반격리", "외상소아실", "소아", "소아음압격리", "소아일반격리")
    val rltmEmerCds = mutableListOf<String>()

    emergencyState.forEachIndexed { index, b ->
        if (b) {
            rltmEmerCds.add(textList[index])
        }
    }
    return rltmEmerCds
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HackatonAndroidTheme {
    }
}