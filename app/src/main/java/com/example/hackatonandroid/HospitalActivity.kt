package com.example.hackatonandroid

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hackatonandroid.data.remote.GetHospitalDTO
import com.example.hackatonandroid.ui.theme.Gray20
import com.example.hackatonandroid.ui.theme.Gray60
import com.example.hackatonandroid.ui.theme.Green
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HospitalLayout(list:List<GetHospitalDTO>, onClick: (Int) -> Unit) {
    Log.d(TAG, "HospitalLayout: ${list}")
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(Gray60)
    ) {
        Column(modifier = Modifier.padding(top = 132.dp)) {
            Text(
                text = "병원 세 곳중",
                fontSize = 24.sp,
                fontFamily = suit,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "적합한 곳을 선택해주세요",
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 24.sp,
                fontFamily = suit,
                fontWeight = FontWeight.SemiBold
            )
            Box(modifier = Modifier.padding(top = 36.dp)){
                HospitalList(list, { onClick(it) })
            }
        }

    }
}

@Composable
fun HospitalList(list: List<GetHospitalDTO>, onClick: (Int) -> Unit) {
    LazyColumn() {
        items(list.size) {
            CardView(data = list[it], { onClick(it) })
        }
    }
}

@Composable
fun CardView(data: GetHospitalDTO, onClick: () -> Unit) {
    Box(modifier = Modifier
        .padding(top = 8.dp)
        .background(color = colorResource(id = R.color.gray50))
        .border(
            BorderStroke(1.dp, color = colorResource(R.color.gray30)),
            shape = RoundedCornerShape(8.dp)
        )
        .clickable {
            onClick()
        }) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .padding(20.dp)
                .weight(1f)) {
                Row {
                    Text(
                        text = data.name,
                        fontSize = 18.sp,
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = R.color.gray10)
                    )
                    Text(
                        text = "",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 18.sp,
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                    )
                }

                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = data.distance,
                        fontSize = 14.sp,
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = R.color.gray20)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(2.dp)
                            .padding(horizontal = 8.dp)
                    ){

                    }
                    Text(
                        text = data.address,
                        fontSize = 14.sp,
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = R.color.gray20)
                    )
                }

                Column(modifier = Modifier.padding(top = 12.dp)){
                    HospitalInfoComponent(
                        painterResource(id = R.drawable.info),
                        text = convertEmergencyTypeText(data.beds)
                        , Gray20)
                    Spacer(modifier = Modifier.height(4.dp))
                    HospitalInfoBedsComponent(
                        painterResource(id = R.drawable.groups),
                        "남은 병상", "${convertEmergencyBedCount(data.beds)}자리 남음",
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

    }
}

fun convertEmergencyTypeText(beds: List<Bed>):String{
    var returnBed = ""
    repeat(beds.size){
        val bedCounts = beds[it].count.split(" / ")
        if(bedCounts[0].toInt() < bedCounts[1].toInt()){
            returnBed += beds[it].type
        }
    }
    return returnBed + " 병상 보유 중"
}

fun convertEmergencyBedCount(beds: List<Bed>):Int{
    var count = 0
    beds.forEach{
        val bedCount = it.count.split(" / ")
        count += bedCount[1].toInt() - bedCount[0].toInt()
    }
    return count
}

@Composable
fun HospitalInfoComponent(painterResources :Painter, text: String, color: Color){
    Row(modifier = Modifier.fillMaxWidth()){
        Icon(painter = painterResources,
            contentDescription = "info",
            tint = color)
        Text(text = text,
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            color = color,
            modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun HospitalInfoBedsComponent(painterResources :Painter, text: String, countText:String, color: Color, highLightColor: Color){
    Row(modifier = Modifier.fillMaxWidth()){
        Icon(painter = painterResources,
            contentDescription = "info",
            tint = color)
        Text(text = text,
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            color = color,
            modifier = Modifier.padding(start = 4.dp))
        Text(text = countText,
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            color = highLightColor,
            modifier = Modifier.padding(start = 4.dp))
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun GreetingPreview6() {
    HackatonAndroidTheme {
        HospitalLayout(listOf(
            GetHospitalDTO(
            "",
            listOf(Bed("1 / 3", "일반")),
                "1.1",
                listOf(),
                listOf(),
                ""
        )
        ),{})
    }
}