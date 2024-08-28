package com.example.hackatonandroid

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hackatonandroid.data.remote.GetHospitalDTO
import com.example.hackatonandroid.ui.theme.Gray20
import com.example.hackatonandroid.ui.theme.Gray50
import com.example.hackatonandroid.ui.theme.Gray60
import com.example.hackatonandroid.ui.theme.Gray70
import com.example.hackatonandroid.ui.theme.Green
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TransferScreen(hospital: GetHospitalDTO, patient: RegisterStateHolder, distance:Float, onStop: () -> Unit, onSuccess:() -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = Gray60)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

            Column(
                modifier = Modifier
                    .padding(top = 132.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "실시간으로 병원에",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 24.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "위치를 공유하고 있습니다.",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = 8.dp),
                    fontSize = 24.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), contentAlignment = Alignment.CenterStart
                    ){
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(color = Gray50)){
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth(distance)
                        .padding(2.dp)
                        .height(2.dp)
                        .background(color = Green)){
                    }
                    Box(modifier = Modifier
                        .size(12.dp)
                        .border(
                            width = 2.dp, color = Green,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Gray70))

                    Row(modifier = Modifier.fillMaxWidth(distance), horizontalArrangement = Arrangement.End){
                        Box(modifier = Modifier
                            .size(12.dp)
                            .border(
                                width = 2.dp, color = Green,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Gray70))
                    }

                }
                Box(
                    modifier = Modifier
                        .background(color = colorResource(id = R.color.gray50))
                        .padding(top = 40.dp)
                        .border(
                            BorderStroke(1.dp, color = colorResource(R.color.gray30)),
                            shape = RoundedCornerShape(12.dp)
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "환자 정보",
                            fontSize = 14.sp,
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.green)
                        )
                        Text(
                            text = "${patient.ageState}세, ${patient.sex}", fontSize = 18.sp,
                            fontFamily = suit,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.gray10),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${patient.symptomState}",
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 14.sp,
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal
                        )

                    }

                }
                Box(
                    modifier = Modifier
                        .background(color = colorResource(id = R.color.gray50))
                        .padding(top = 8.dp)
                        .border(
                            BorderStroke(1.dp, color = colorResource(R.color.gray30)),
                            shape = RoundedCornerShape(8.dp)
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "병원 정보",
                            fontSize = 14.sp,
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.green)
                        )
                        Text(
                            text = hospital.name, fontSize = 18.sp,
                            fontFamily = suit,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.gray10),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = hospital.address,
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 14.sp,
                            fontFamily = suit,
                            fontWeight = FontWeight.Normal,
                            color = Gray20
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HospitalInfoComponent(
                            painterResource(id = R.drawable.info),
                            text = convertEmergencyTypeText(hospital.beds)
                            , Gray20)
                        Spacer(modifier = Modifier.height(4.dp))
                        HospitalInfoBedsComponent(
                            painterResource(id = R.drawable.groups),
                            "남은 병상", "${convertEmergencyBedCount(hospital.beds)}자리 남음",
                            Gray20, Green)
                        Spacer(modifier = Modifier.height(4.dp))
                        HospitalInfoComponent(
                            painterResource(id = R.drawable.campaign),
                            "",
                            Green
                        )
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {

                    TransferSuspendButton(text = "이송 중단") {
                        onStop()
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    NormalMaxButton(text = "이송 완료") {
                        onSuccess()
                    }
                }
            }
        }

    }
}

@Composable
fun TransferInfo(label: String, text: String, icon: Int) {
    Row(modifier = Modifier.padding(top = 8.dp))
    {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "locate",
            tint = colorResource(
                id = R.color.gray10
            ),
            modifier =
            Modifier
                .width(16.dp)
                .height(16.dp)
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.gray10)
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp),
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.gray20)
        )
    }
}

@Composable
fun TransferSuspendButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.white)),
        contentPadding = PaddingValues(0.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    BorderStroke(2.dp, color = colorResource(R.color.gray30)),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 16.dp)
            ) {

                Text(
                    text = text,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colorResource(id = R.color.red)
                )
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

@Preview
@Composable
fun Preview() {
    HackatonAndroidTheme() {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransferScreen(
                GetHospitalDTO("ㄹㅇㅁㄴㄻㄻㄴㄹ", listOf(Bed("1 / 1", "일반")), "10km", listOf(), listOf(), "백병원"),
                RegisterStateHolder("", 1, listOf(true, true, true, true, true, true, true, true), "",""),
                0.4f
                ,{},
                {}
            )
        }
    }
}