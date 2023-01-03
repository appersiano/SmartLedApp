package com.appersiano.smartledapp.views

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appersiano.smartledapp.R
import com.appersiano.smartledapp.client.CradleLedBleClient
import com.appersiano.smartledapp.viewmodels.CradleClientViewModel

@Composable
fun DetailScreen(
    viewModel: CradleClientViewModel,
    macAddress: String,
    onConnect: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    status: CradleLedBleClient.SDeviceStatus,
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            text = "Connect to $macAddress"
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Button(onClick = onConnect) {
                Text("Connect")
            }
            Button(onClick = onDisconnect, modifier = Modifier.padding(start = 10.dp)) {
                Text("Disconnect")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .background(getStatusColor(status), shape = CircleShape)
                        .requiredSize(15.dp)
                )
            }
        }
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        Row(Modifier.height(IntrinsicSize.Min)) {
            SetLEDStatus(viewModel)
            Divider(
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            SetPIRStatus(viewModel)
            Divider(
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
        }
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        SetLEDColor(viewModel)
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        SetLEDBrightness(viewModel)
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        SetCurrentTime(viewModel)
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        SetTimerFeature(viewModel)
    }
}

@Composable
fun SetTimerFeature(viewModel: CradleClientViewModel) {
    val switchValue = remember { mutableStateOf(false) }

    val hourONValue = remember { mutableStateOf(0) }
    val hourOFFValue = remember { mutableStateOf(0) }
    val minuteONValue = remember { mutableStateOf(0) }
    val minuteOFFValue = remember { mutableStateOf(0) }

    Row {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_timer_24),
            contentDescription = "Time Feature"
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = "Timer Feature")
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "Switch ON/OFF"
        )
        Switch(
            modifier = Modifier.padding(start = 16.dp),
            checked = switchValue.value,
            onCheckedChange = {
                switchValue.value = it
            })
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row {
        Text("ON", modifier = Modifier.width(30.dp))
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = hourONValue.value.toString(),
            label = { Text("Hour") },
            onValueChange = { hourONValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteONValue.value.toString(),
            label = { Text("Minute") },
            onValueChange = { minuteONValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Row {
        Text("OFF", modifier = Modifier.width(30.dp))
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = hourOFFValue.value.toString(),
            label = { Text("Hour") },
            onValueChange = { hourOFFValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteOFFValue.value.toString(),
            label = { Text("Minute") },
            onValueChange = { minuteOFFValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Button(onClick = {
        viewModel.setTimerFeature(
            switchValue.value,
            hourONValue.value,
            minuteONValue.value,
            hourOFFValue.value,
            minuteOFFValue.value
        )
    }, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Set Timer Feature")
    }
}

@Composable
fun SetCurrentTime(viewModel: CradleClientViewModel) {
    val hourValue = remember { mutableStateOf(0) }
    val minuteValue = remember { mutableStateOf(0) }
    val secondValue = remember { mutableStateOf(0) }
    val dayValue = remember { mutableStateOf(0) }
    val monthValue = remember { mutableStateOf(0) }
    val yearValue = remember { mutableStateOf(0) }

    Row {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_access_time_24),
            contentDescription = "Current Time"
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = "Current Time")
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row {
        TextField(
            modifier = Modifier.width(100.dp),
            value = hourValue.value.toString(),
            label = { Text("Hour") },
            onValueChange = { hourValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteValue.value.toString(),
            label = { Text("Minute") },
            onValueChange = { minuteValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = secondValue.value.toString(),
            label = { Text("Second") },
            onValueChange = { secondValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Row {
        TextField(
            modifier = Modifier.width(100.dp),
            value = dayValue.value.toString(),
            label = { Text("Day") },
            onValueChange = { dayValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = monthValue.value.toString(),
            label = { Text("Month") },
            onValueChange = { monthValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = yearValue.value.toString(),
            label = { Text("year") },
            onValueChange = { yearValue.value = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Button(
        onClick = {
            viewModel.setCurrentTime(
                hourValue.value,
                minuteValue.value,
                secondValue.value,
                dayValue.value,
                monthValue.value,
                yearValue.value
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set Current Timer")
    }
}

@Composable
fun SetLEDBrightness(viewModel: CradleClientViewModel) {
    val sliderBrightness = remember { mutableStateOf(0f) }
    Row {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_brightness_24),
            contentDescription = "Brightness"
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = "Brightness")
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = "0")
    }
    Slider(
        value = sliderBrightness.value,
        onValueChange = { sliderBrightness.value = it },
        valueRange = 0f..100f,
        onValueChangeFinished = {
            viewModel.setLEDBrightness(sliderBrightness.value.toLong())
        },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.primary,
            activeTrackColor = MaterialTheme.colors.primary
        )
    )
}

@Composable
private fun SetLEDColor(viewModel: CradleClientViewModel) {

    val mRGBColor = remember { mutableStateOf(Color.Red) }

    val textValueRed = remember { mutableStateOf(0) }
    val sliderPositionRed = remember { mutableStateOf(0) }

    val textValueGreen = remember { mutableStateOf(0) }
    val sliderPositionGreen = remember { mutableStateOf(0) }

    val textValueBlue = remember { mutableStateOf(0) }
    val sliderPositionBlue = remember { mutableStateOf(0) }

    Column {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                text = "LED RGB Color"
            )
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .background(mRGBColor.value, shape = CircleShape)
                    .requiredSize(25.dp)
            )
        }

        Row {
            Text(text = "RED")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = textValueRed.value.toString())
        }
        Slider(
            value = sliderPositionRed.value.toFloat(),
            onValueChange = {
                sliderPositionRed.value = it.toInt()
                textValueRed.value = sliderPositionRed.value
                updateCircleRGBColor(
                    mRGBColor,
                    sliderPositionRed.value,
                    sliderPositionGreen.value,
                    sliderPositionBlue.value
                )
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
        Row {
            Text(text = "GREEN")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = textValueGreen.value.toString())
        }
        Slider(
            value = sliderPositionGreen.value.toFloat(),
            onValueChange = {
                sliderPositionGreen.value = it.toInt()
                textValueGreen.value = sliderPositionGreen.value
                updateCircleRGBColor(
                    mRGBColor,
                    sliderPositionRed.value,
                    sliderPositionGreen.value,
                    sliderPositionBlue.value
                )
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )

        Row {
            Text(text = "BLUE")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = textValueBlue.value.toString())
        }
        Slider(
            value = sliderPositionBlue.value.toFloat(),
            onValueChange = {
                sliderPositionBlue.value = it.toInt()
                textValueBlue.value = sliderPositionBlue.value
                updateCircleRGBColor(
                    mRGBColor,
                    sliderPositionRed.value,
                    sliderPositionGreen.value,
                    sliderPositionBlue.value
                )
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
    }
    Button(
        onClick = {
            viewModel.setLEDColor(
                sliderPositionRed.value.toLong(),
                sliderPositionGreen.value.toLong(),
                sliderPositionBlue.value.toLong()
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set RGB")
    }
}

fun updateCircleRGBColor(RGBColor: MutableState<Color>, red: Int, green: Int, blue: Int) {
    val color = android.graphics.Color.rgb(red, green, blue)
    RGBColor.value = Color(color)
}

@Composable
private fun SetLEDStatus(viewModel: CradleClientViewModel) {
    val checkedLed = remember { mutableStateOf(false) }
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "LED Status"
        )
        Switch(
            modifier = Modifier.padding(start = 16.dp),
            checked = checkedLed.value,
            onCheckedChange = {
                if (it) {
                    viewModel.setLEDStatus(true)
                    checkedLed.value = true
                } else {
                    viewModel.setLEDStatus(false)
                    checkedLed.value = false
                }
            })
    }
}

@Composable
private fun SetPIRStatus(viewModel: CradleClientViewModel) {
    val checkedLed = remember { mutableStateOf(false) }
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "PIR Status"
        )
        Switch(
            modifier = Modifier.padding(start = 16.dp),
            checked = checkedLed.value,
            onCheckedChange = {
                if (it) {
                    viewModel.setPIRStatus(true)
                    checkedLed.value = true
                } else {
                    viewModel.setPIRStatus(false)
                    checkedLed.value = false
                }
            })
    }
}

fun getStatusColor(status: CradleLedBleClient.SDeviceStatus): Color {
    return when (status) {
        CradleLedBleClient.SDeviceStatus.CONNECTED -> Color.Yellow
        CradleLedBleClient.SDeviceStatus.READY -> Color.Green
        CradleLedBleClient.SDeviceStatus.UNKNOWN -> Color.Gray
        is CradleLedBleClient.SDeviceStatus.DISCONNECTED -> Color.Red
    }
}

@Preview
@Composable
fun PrevieColor() {
    val color = android.graphics.Color.rgb(255, 0, 0)
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(color = Color(color), shape = RectangleShape)
    ) {

    }
}

//@Preview(showSystemUi = true)
//@Composable
//fun previewDetail() {
//    DetailScreen(
//        viewModel = //,
//        "00:11:22:33:44:55",
//        onConnect = {},
//        onDisconnect = {},
//        CradleLedBleClient.SDeviceStatus.READY
//    )
//}
