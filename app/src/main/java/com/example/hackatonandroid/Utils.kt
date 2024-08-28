package com.example.hackatonandroid

import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val suit = FontFamily(
    Font(R.font.suitb, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.suitm, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.suitl, FontWeight.Light, FontStyle.Normal),
    Font(R.font.suitr, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.suitsb, FontWeight.SemiBold, FontStyle.Normal)

)


@Composable
fun SearchTextField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
) {

    Box(
        modifier = Modifier
            .padding(horizontal = 48.dp, vertical = 12.dp)
            .border(
                BorderStroke(2.dp, color = colorResource(R.color.gray30)),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = Color.White
            )
    ) {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            Box() {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "search",
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    tint = colorResource(id = R.color.gray20)
                )
            }

            Box() {

                if (text.isEmpty()) {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        color = colorResource(R.color.gray20),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp),
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal
                    )
                }

                BasicTextField(
                    value = text, onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.gray10)
                    ),
                    keyboardOptions = keyboardOptions
                )
            }
        }


    }
}

@Composable
fun NormalMaxButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green)),
        contentPadding = PaddingValues(0.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .align(Alignment.Center)
                .height(56.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = text,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_forward),
                    contentDescription = "arrow",
                    tint = colorResource(
                        id = R.color.gray50
                    ),
                )
            }
        }

    }
}

@Composable
fun HospitalCard() {
    Card {

    }
}

@Preview
@Composable
fun HospitalCardPreview() {
    Card {

    }
}

@Preview
@Composable
fun SearchPreview() {
    HackatonAndroidTheme() {
        SearchTextField(text = "", onTextChange = {}, label = "검색하세요?")

    }

}

@Preview
@Composable
fun NormalPreview() {
    HackatonAndroidTheme() {
        NormalTextField(text = "", onTextChange = {}, label = "텍스트")

    }

}
@Composable
fun NormalTextField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                BorderStroke(2.dp, color = colorResource(R.color.gray30)),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.White
            )
    ) {
        if (text.isEmpty()) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = colorResource(R.color.gray20),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
            )
        }

        BasicTextField(
            value = text, onValueChange = { it -> onTextChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            textStyle = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.gray10)),
            keyboardOptions = keyboardOptions
        )

    }
}

@Preview
@Composable
fun NormalMaxButtonPreview() {
    HackatonAndroidTheme {
        NormalMaxButton(text = "text", onClick = {})

    }
}


@SuppressLint("MissingPermission")
@Composable
fun GetCurrentLocation(onLocationReceived1: (Location) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    RequestLocationPermission {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived1(it)
                } ?: run {
                    CoroutineScope(Dispatchers.Main).launch {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            location?.let {
                                onLocationReceived1(it)
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            // 권한이 거부된 경우 처리
        }
    }
    LaunchedEffect(Unit) {
        while(true){
            permissionLauncher1.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            delay(1000)
        }
    }

}
@Composable
fun firstRequestLocationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            // 권한이 거부된 경우 처리
        }
    }
    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
}



