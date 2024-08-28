package com.example.hackatonandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hackatonandroid.ui.theme.HackatonAndroidTheme

class CarNumberActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackatonAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CarNumberLayout { carNumberSave(it) }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (prefs.getString("carNumber", "")!!.isEmpty()) {

        } else {
            finish()
        }
    }

    private fun carNumberSave(carNumber: String) {
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("carNumber", carNumber)
        editor.apply()
        finish()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CarNumberLayout(onClick: (String) -> Unit) {
    var carNumber by remember {
        mutableStateOf("")
    }
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.frame_46),
                    contentDescription = "title_logo",
                )
                Box(modifier = Modifier.padding(top = 44.dp)) {
                    CarNumberTextField(
                        label = "차량번호",
                        text = carNumber,
                        placeHolder = "01가 1234",
                        onTextChange = { carNumber = it }
                    )
                }
                Text(
                    text = "차량번호는 설정에서 변경할 수 있습니다.",
                    modifier = Modifier.padding(8.dp).padding(top = 8.dp),
                    fontSize = 14.sp,
                    fontFamily = suit,
                    fontWeight = FontWeight.Normal,
                    )
                Box(modifier = Modifier.padding(top = 48.dp)) {
                    NormalMaxButton("저장", { onClick(carNumber) })
                }

            }
        }
    }
}

@Composable
fun CarNumberTextField(
    label: String,
    text: String,
    placeHolder: String,
    onTextChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    HackatonAndroidTheme {
        CarNumberLayout(onClick = {})
    }
}