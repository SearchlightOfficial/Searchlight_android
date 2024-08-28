package com.example.hackatonandroid

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hackatonandroid.ui.theme.Gray10
import com.example.hackatonandroid.ui.theme.Gray20
import com.example.hackatonandroid.ui.theme.Gray30
import com.example.hackatonandroid.ui.theme.Gray40
import com.example.hackatonandroid.ui.theme.Gray50
import com.example.hackatonandroid.ui.theme.Gray60
import com.example.hackatonandroid.ui.theme.Gray70
import com.example.hackatonandroid.ui.theme.Green
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme
import com.example.hackatonandroid.ui.theme.Red

data class RegisterStateHolder(
    val sex: String,
    val ageState: Int,
    val emergencyState: List<Boolean>,
    val ktasState: String,
    val symptomState: String
    )

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterLayout(
    address: String,
    screenNumber: Int,
    onNext: (RegisterStateHolder) -> Unit,
    onValid: (RegisterStateHolder) -> Boolean,
    refreshLocation: () -> Unit,
) {

    var sexPositionState by remember {
        mutableStateOf(4)
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var sexState by remember { mutableStateOf("") }
    val sexList = listOf("남성", "여성")
    var ageState by remember { mutableStateOf(0) }
    var isCategoryValid by remember { mutableStateOf(true) }
    var sexDialogState by remember { mutableStateOf(false) }
    var infoCheckDialogState by remember {
        mutableStateOf(false)
    }
    var positionState by remember {
        mutableStateOf(0)
    }
    val emergencyState = remember {
        mutableStateListOf(*Array(8) { false })
    }
    var ktasState by remember {
        mutableStateOf("")
    }
    var symptomState by remember {
        mutableStateOf("")
    }
    var isAgeValid by remember { mutableStateOf(true) }
    var location by remember {
        mutableStateOf("")
    }
    var mainCategoryState by remember { mutableStateOf("") }
    var subCategoryState by remember { mutableStateOf("") }
    val mainList = listOf("긴급 환자", "응급 환자", "비응급 환자")
    val subList = listOf(
        "호흡 곤란",
        "쇼크",
        "저산소증",
        "심장병",
        "심정지",
        "중증의 출혈",
        "중증의 화상",
        "다발성 골절",
        "단순 골절",
        "소량의 출혈",
        "타박상"
    )
    var patientMemoState by remember { mutableStateOf("") }

    if(sexDialogState)
        SexDropDownDialog(sexPositionState, {
            sexPositionState = it
            sexDialogState = false
            when(it){
                0 -> sexState = "남성"
                1 -> sexState = "여성"
                2 -> sexState = "알 수 없음"
            }
        })

    if(infoCheckDialogState)
        RegisterInfoCheckDialog(
            ageState,sexState,emergencyState, ktasState, {onNext(RegisterStateHolder(
                sexState,
                ageState,
                emergencyState,
                ktasState,
                symptomState
            ))},
            {infoCheckDialogState = false}
        )

    Surface (
        Modifier
            .background(Gray60)
            .fillMaxSize()
    ){
        Box(modifier = Modifier.background(Gray60)){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(Gray60)
                .padding(bottom = 88.dp)
                .verticalScroll(scrollState)) {
                Text(
                    text = "안녕하세요, ",
                    modifier = Modifier.padding(top = 104.dp, start = 8.dp),
                    fontSize = 24.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "환자 정보를 입력해주세요",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 24.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                )

                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 24.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Location(address,{refreshLocation()})
                }
                val emergencyFunctionList = mutableListOf<(Boolean) -> Unit>()
                repeat(8){
                    position ->
                    emergencyFunctionList.add { emergencyState[position] = !it }
                }


                val density = LocalDensity.current
                AnimatedVisibility(
                    visible = screenNumber>3,
                    enter = slideInVertically {
                        with(density) { -40.dp.roundToPx() }
                    } + expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(
                        initialAlpha = 0.3f
                    ),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()
                ) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)){
                        RegisterBasicTextFieldComponent("환자 증상",symptomState, {symptomState = it}, "환자 증상을 입력해주세요." )
                    }
                }

                AnimatedVisibility(
                    visible = screenNumber>2,
                    enter = slideInVertically {
                        with(density) { -40.dp.roundToPx() }
                    } + expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(
                        initialAlpha = 0.3f
                    ),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()
                ) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)){
                        RegisterBasicTextFieldComponent("KTAS",ktasState, {ktasState = it}, "AAAAA01" )
                    }
                }

                AnimatedVisibility(
                    visible = screenNumber>1,
                    enter = slideInVertically {
                        with(density) { -40.dp.roundToPx() }
                    } + expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(
                        initialAlpha = 0.3f
                    ),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()
                ) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                            RegisterEmergencyOption(state = emergencyState, onStateChange =
                            emergencyFunctionList
                            )
                    }
                }
                AnimatedVisibility(
                    visible = screenNumber>0,
                    enter = slideInVertically {
                        with(density) { -40.dp.roundToPx() }
                    } + expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(
                        initialAlpha = 0.3f
                    ),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()
                ){
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        RegisterDropDown(
                            text = "환자 성별",
                            sexState,
                            sexList,
                            label = "성별을 선택해주세요"
                        ) { sexDialogState = true}
                    }
                }
                Box(modifier = Modifier.padding(vertical = 12.dp)) {
                    RegisterAgeField(label = "환자 추정 나이",
                        text = ageState.toString(),
                        onTextChange = { ageState = it })
                }
            }

            Column(modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp)
                .padding(bottom = 16.dp), verticalArrangement = Arrangement.Bottom){
                NormalMaxButton(text = "다음", onClick = {
                    if(screenNumber < 4) {
                        onNext(RegisterStateHolder(
                            sexState,
                            ageState,
                            emergencyState,
                            ktasState,
                            symptomState
                        ))
                    }else{
                    infoCheckDialogState = true
                    }
                }
                )
            }
        }


    }


}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterLayoutLegercy(
    address: String,
    onNext: (RegisterStateHolder) -> Unit,
    onValid: (RegisterStateHolder) -> Boolean
) {
    var sexState by remember { mutableStateOf("남성") }
    val sexList = listOf("남성", "여성")
    var ageState by remember { mutableStateOf(0) }
    var positionState by remember { mutableStateOf(0) }
    var isCategoryValid by remember { mutableStateOf(true) }
    var isAgeValid by remember { mutableStateOf(true) }
    var location by remember {
        mutableStateOf("")
    }
    location = address
    var mainCategoryState by remember { mutableStateOf("") }
    var subCategoryState by remember { mutableStateOf("") }
    val mainList = listOf("긴급 환자", "응급 환자", "비응급 환자")
    val subList = listOf(
        "호흡 곤란",
        "쇼크",
        "저산소증",
        "심장병",
        "심정지",
        "중증의 출혈",
        "중증의 화상",
        "다발성 골절",
        "단순 골절",
        "소량의 출혈",
        "타박상"
    )
    var patientMemoState by remember { mutableStateOf("") }


    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = colorResource(id = R.color.gray40))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "안녕하세요, ",
                    modifier = Modifier.padding(top = 104.dp, start = 8.dp),
                    fontSize = 24.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "환자 정보를 입력해주세요",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 24.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                )
                Box(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                    Location(address, {})
                }
                Row(modifier = Modifier.padding(top = 48.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        RegisterDropDown(
                            text = "환자 성별",
                            sexState,
                            sexList,
                            label = ""
                        ) {

                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        RegisterAgeField(label = "환자 추정 나이",
                            text = ageState.toString(),
                            onTextChange = { ageState = it })
                    }
                }
                Box(modifier = Modifier.padding(top = 28.dp)) {
                    RegisterClassification(isCategoryValid,
                        mainState = mainCategoryState,
                        mainList = mainList,
                        subState = subCategoryState,
                        subList = subList,
                        mainDropDownEvent = { mainCategoryState = it },
                        subDropDownEvent = { subCategoryState = it }
                    )
                }
                if (positionState > 0) {
                    Box(modifier = Modifier.padding(top = 28.dp)) {
                    }
                }

                Box(modifier = Modifier.padding(top = 28.dp)) {
                    RegisterTextField(
                        "환자 메모 (선택)",
                        patientMemoState,
                        "추가적인 환자 정보를 입력하세요...",
                        onTextChange = { patientMemoState = it })
                }

            }



            Box(modifier = Modifier.padding(top = 28.dp, bottom = 40.dp)) {
                NormalMaxButton(text = "주변 병원 찾기", onClick = {
                    val state = RegisterStateHolder(
                        sexState,
                        ageState,
                        listOf(false, false, false, false, false, false, false, false),
                        mainCategoryState,
                        subCategoryState
                    )
                    if (onValid(state)) {
                        Log.d(TAG, "RegisterLayout: ${mainCategoryState}")
                        onNext(state)
                    } else {
                        Log.d(TAG, "RegisterLayout: ${mainCategoryState}")
                    }

                })
            }
        }
    }
}


@Composable
fun Location(text: String, refreshLocation: () -> Unit) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = R.drawable.location_on3), contentDescription = "location",
            Modifier.size(20.dp),
            tint = Gray30
        )
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 4.dp),
            color = Gray30
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            Text(
                text = "새로고침",
                fontSize = 16.sp,
                fontFamily = suit,
                fontWeight = FontWeight.Normal,
                color = Green,
                modifier = Modifier.clickable {
                    refreshLocation()
                }
            )
        }
    }
}

@Composable
fun RegisterTextField(
    label: String,
    text: String,
    placeHolder: String,
    onTextChange: (String) -> Unit
) {
    Column(modifier = Modifier) {
        Text(
            text = label,
            color = colorResource(id = R.color.gray20),
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp)
        )
        Box(modifier = Modifier.padding(top = 8.dp)) {
            NormalTextField(
                text = text,
                onTextChange = onTextChange,
                label = placeHolder
            )
        }

    }
}


@Composable
fun RegisterAgeField(label: String, text: String, onTextChange: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (text.length >= 4) {
                "제대로 된 값을 입력하세요."
            } else {
                label
            },
            color =
            if (text.length >= 4) {
                colorResource(id = R.color.red)
            } else {
                colorResource(id = R.color.gray20)
            },
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {

            RegisterAgeTextField(
                text = text,
                onTextChange = { onTextChange(it) },
                label = "나이를 입력해주세요."
            )
        }

    }
}

@Composable
fun RegisterBasicTextFieldComponent(label: String, text: String, onTextChange: (String) -> Unit, placeHolder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Gray30,
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            RegisterBasicTextField(
                text = text,
                onTextChange = { onTextChange(it) },
                label = placeHolder
            )
        }

    }
}

@Composable
fun RegisterBasicTextField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(2.dp, color = Gray50),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Gray70,
                shape = RoundedCornerShape(12.dp)
            )
    ) {

        Row(
            modifier = Modifier
        ) {

            BasicTextField(
                value = text,
                onValueChange = { it ->
                        onTextChange(
                            it
                        )

                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(id = R.color.gray10)),

            ) { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = label,
                        color = Gray40
                    )
                }
                innerTextField()
            }
        }


    }

}

@Composable
fun RegisterAgeTextField(
    text: String,
    onTextChange: (Int) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(2.dp, color = if (text.length >= 4) Red else Gray50),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Gray70,
                shape = RoundedCornerShape(12.dp)
            )
    ) {

        Row(
            modifier = Modifier
        ) {

            BasicTextField(
                value = if (text == "0") "" else text,
                onValueChange = { it ->
                    if (it.toIntOrNull() != null) {
                        onTextChange(
                            it.toInt()
                        )
                    } else {
                        onTextChange(0)
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(id = R.color.gray10)),
                keyboardOptions = keyboardOptions,
            ) { innerTextField ->

                if (text == "0") {
                    Text(
                        text = label,
                        color = Gray40
                    )
                }
                innerTextField()
            }
        }


    }
}

@Composable
fun RegisterClassification(
    isValid: Boolean,
    mainState: String, mainList: List<String>, subState: String, subList: List<String>,
    mainDropDownEvent: (String) -> Unit, subDropDownEvent: (String) -> Unit,
) {
    Column {
        Text(
            text =
            if (!isValid) {
                "필수로 입력해야 합니다."

            } else {
                "중증도 분류"
            },
            color =
            if (!isValid) {
                colorResource(id = R.color.red)
            } else {
                colorResource(id = R.color.gray20)
            },
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .border(
                    BorderStroke(2.dp, color = colorResource(R.color.gray30)),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .width(132.dp)

            ) {
                RegisterSimpleDropDown(
                    state = mainState,
                    list = mainList,
                    label = "대분류",
                    dropDownEvent = {
                        mainDropDownEvent(it)
                    })
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .padding(vertical = 10.dp)
                    .background(
                        color = colorResource(
                            id = R.color.gray30
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .weight(1f)

            ) {
                RegisterSimpleDropDown(
                    state = subState,
                    list = when (mainState) {
                        "긴급 환자" -> subList.slice(0..4)
                        "응급 환자" -> subList.slice(5..7)
                        "비응급 환자" -> subList.slice(8..10)
                        else -> listOf()
                    },
                    label = "소분류",
                    dropDownEvent = {
                        subDropDownEvent(it)
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterSimpleDropDown(
    state: String,
    list: List<String>,
    label: String,
    dropDownEvent: (String) -> Unit,
) {
    var isDropMenuDown by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { isDropMenuDown = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.gray50)),
            contentPadding = PaddingValues(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = state.ifEmpty {
                        label
                    },
                    fontFamily = suit,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(start = 16.dp),
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.gray20)
                )
                Icon(
                    painter = painterResource(R.drawable.expand_more),
                    contentDescription = "expand",

                    tint = colorResource(
                        id = R.color.gray20
                    ), modifier = Modifier
                )

            }


        }
        DropdownMenu(
            modifier = Modifier.background(color = colorResource(id = R.color.gray50)),
            expanded = isDropMenuDown,
            onDismissRequest = { isDropMenuDown = false },
        ) {

            list.forEach {
                DropdownMenuItem(onClick = {
                    dropDownEvent(it)
                    isDropMenuDown = false
                }, modifier = Modifier.background(color = colorResource(id = R.color.gray50))) {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}


@Composable
fun RegisterDropDown(
    text: String,
    state: String,
    list: List<String>,
    label: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
        .border(
            BorderStroke(2.dp, color = colorResource(R.color.gray30)),
            shape = RoundedCornerShape(12.dp)
        ),

    dropDownEvent: () -> Unit,

    ) {

    Column {
        Text(
            text = text,
            color = colorResource(id = R.color.gray20),
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Button(
            onClick = { dropDownEvent()
                      },
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gray70),
            contentPadding = PaddingValues(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isEmpty()) {
                        label
                    } else {
                        state
                    },
                    fontFamily = suit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = if (state.isEmpty()) Gray40 else Gray10
                )
                Icon(
                    painter = painterResource(R.drawable.expand_more),
                    contentDescription = "expand",
                    tint = Gray20, modifier = Modifier
                        .size(20.dp)
                )

            }
        }

        /*
                DropdownMenu(
                    modifier = Modifier.background(color = colorResource(id = R.color.gray50)),
                    expanded = isDropMenuDown,
                    onDismissRequest = { isDropMenuDown = false },

                    ) {

                    list.forEach {
                        DropdownMenuItem(onClick = {
                            dropDownEvent(it)
                            isDropMenuDown = false
                        }, modifier = Modifier.background(color = colorResource(id = R.color.gray50))) {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                fontFamily = suit,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
        */
    }
}

@Composable
fun RegisterEmergencyOption(state: List<Boolean>, onStateChange: List<(Boolean) -> Unit>) {
    Column {
        Text(text = "응급실 종류",
            fontSize = 14.sp,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            color = Gray30,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                .border(
                    BorderStroke(width = 1.dp, color = Gray50),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 4.dp)
        ) {
            val textList = listOf("일반", "코호트 격리", "음압격리", "일반격리", "외상소아실", "소아", "소아음압격리", "소아일반격리")

            textList.forEachIndexed { index, s ->
                EmergencyCheckBox(text = textList[index], state = state[index], onStateChange = {onStateChange[index](it)})
            }
        }
    }
}

@Composable
fun EmergencyCheckBox(text: String, state: Boolean, onStateChange : (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onStateChange(state)
            }
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .background(shape = RoundedCornerShape(12.dp), color = Gray70)

        ,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.check),
            tint = if (state) Green else Gray40,
            contentDescription = "check",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontFamily = suit,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 16.sp,
            color = if (state) Gray10 else Gray40
        )
    }
}

@Preview
@Composable
fun GreetingPreview3() {
    HackatonAndroidTheme {
        RegisterLayout(address = "", screenNumber = 4, onNext = {}, {true}, {})
    }
}

@Composable
fun SexDropDownDialog(position :Int, onPositionChange : (Int) -> Unit){
    Dialog(onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(modifier = Modifier
            .defaultMinSize(1.dp, 1.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            ){
                Text(text = "환자 성별",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    fontFamily = suit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Gray20
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPositionChange(0)
                            //dismiss

                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .background(color = Gray70, shape = RoundedCornerShape(12.dp))

                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "남성",
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Gray10
                    )
                    if(position == 0){
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            Icon(
                                painter = painterResource(id = R.drawable.check),
                                tint = Green,
                                contentDescription = "check",
                                modifier = Modifier.size(20.dp)

                            )
                        }
                    }

                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPositionChange(1)
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "여성",
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Gray10
                    )
                    if(position == 1){
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            Icon(
                                painter = painterResource(id = R.drawable.check),
                                tint = Green,
                                contentDescription = "check",
                                modifier = Modifier.size(20.dp)

                            )
                        }
                    }

                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPositionChange(2)
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "알 수 없음",
                        fontFamily = suit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Gray10
                    )
                    if(position == 2){
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            Icon(
                                painter = painterResource(id = R.drawable.check),
                                tint = Green,
                                contentDescription = "check",
                                modifier = Modifier.size(20.dp)

                            )
                        }
                    }

                }

            }

        }
    }
}
@Composable
fun RegisterInfoCheckDialog(ageState: Int,
                            sexState: String,
                            emergencyState: List<Boolean>,
                            ktasState: String,
                            onPositiveAction : () -> Unit,
                            onNegativeAction : () -> Unit,
                            ){
    Dialog(onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(modifier = Modifier
            .defaultMinSize(1.dp, 1.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(color = Gray70, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            ){
                Text(text = "환자 정보를 확인해주세요.",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    fontFamily = suit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Gray10
                )
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
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray10)
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
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray10)
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
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray10)
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
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Gray10)
                    }

                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp)){
                    Button(modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(end = 4.dp)
                        .height(56.dp),
                        onClick = {onNegativeAction()},
                        colors = ButtonDefaults.buttonColors(containerColor = Gray60),
                        border = BorderStroke(width = 1.dp, color =  Gray50),
                        shape = RoundedCornerShape(8.dp)
                        ){
                        Text(
                            text = "아니요",
                            fontFamily = suit,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Gray10)

                    }

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                        .height(56.dp),
                        onClick = {onPositiveAction()},
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        shape = RoundedCornerShape(8.dp)
                    ){
                        Text(
                            text = "네, 맞아요",
                            fontFamily = suit,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Gray70)

                    }

                }
            }


        }
    }
}

fun emergencyString(emergencyState: List<Boolean>) : String{
    val textList = listOf("일반", "코호트 격리", "음압격리", "일반격리", "외상소아실", "소아", "소아음압격리", "소아일반격리")
    val returnList = mutableListOf<String>()
    for(i in 0 until emergencyState.size){
        if(emergencyState[i]){
            returnList.add(textList[i])
        }
    }

    return if(returnList.size > 2) {
        "${returnList[0]}, ${returnList[1]} 외 ${returnList.size - 2}개"
    }else if(returnList.size == 2){
        "${returnList[0]}, ${returnList[1]}"
    }else {
        if(returnList.isNotEmpty()){
            returnList[0]
        }else{
            ""
        }
    }
}

