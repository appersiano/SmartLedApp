package com.appersiano.smartledapp.views

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appersiano.smartledapp.R
import com.appersiano.smartledapp.client.CradleLedBleClient

@Composable
fun DetailScreen(
    macAddress: String,
    onConnect: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    status: CradleLedBleClient.SDeviceStatus,
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(scrollState, reverseScrolling = true)
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
            setLEDStatus()
            Divider(
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            setPIRStatus()
            Divider(
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
        }
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        setLEDColor()
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        setLEDBrightness()
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        setCurrentTime()
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        setTimerFeature()
    }
}

@Composable
fun setTimerFeature() {
    Row {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_timer_24),
            contentDescription = "Time Feature"
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = "Timer Feature")
    }
    Spacer(modifier = Modifier.height(5.dp))
    val checkedLed = remember { mutableStateOf(false) }
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
            checked = checkedLed.value,
            onCheckedChange = {
                if (it) {
                    //onEnableNotification()
                    checkedLed.value = true
                } else {
                    //onDisableNotification()
                    checkedLed.value = false
                }
            })
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row {
        Text("ON", modifier = Modifier.width(30.dp))
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Hour") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Minute") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Row {
        Text("OFF", modifier = Modifier.width(30.dp))
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Hour") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Minute") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Set Timer Feature")
    }
}

@Composable
fun setCurrentTime() {
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
            value = "10",
            label = { Text("Hour") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Minute") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Second") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Row {
        TextField(
            modifier = Modifier.width(100.dp),
            value = "10",
            label = { Text("Day") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "1",
            label = { Text("Month") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = "2023",
            label = { Text("year") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Set Current Timer")
    }
}

@Composable
fun setLEDBrightness() {
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
            // launch some business logic update with the state you hold
            // viewModel.updateSelectedSliderValue(sliderPosition)
        },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.primary,
            activeTrackColor = MaterialTheme.colors.primary
        )
    )
}

@Composable
private fun setLEDColor() {
    val checked = remember { mutableStateOf(false) }
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
                    .background(Color.Red, shape = CircleShape)
                    .requiredSize(25.dp)

            )
        }
        var sliderPositionRed = remember { mutableStateOf(0f) }
        Row {
            Text(text = "RED")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "0")
        }
        Slider(
            value = sliderPositionRed.value,
            onValueChange = { sliderPositionRed.value = it },
            valueRange = 0f..255f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
        var sliderPositionGreen = remember { mutableStateOf(0f) }
        Row {
            Text(text = "GREEN")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "0")
        }
        Slider(
            value = sliderPositionGreen.value,
            onValueChange = { sliderPositionGreen.value = it },
            valueRange = 0f..255f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
        var sliderPositionBlue = remember { mutableStateOf(0f) }
        Row {
            Text(text = "BLUE")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "0")
        }
        Slider(
            value = sliderPositionBlue.value,
            onValueChange = { sliderPositionBlue.value = it },
            valueRange = 0f..255f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
    }
}

@Composable
private fun setLEDStatus() {
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
                    //onEnableNotification()
                    checkedLed.value = true
                } else {
                    //onDisableNotification()
                    checkedLed.value = false
                }
            })
    }
}

@Composable
private fun setPIRStatus() {
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
                    //onEnableNotification()
                    checkedLed.value = true
                } else {
                    //onDisableNotification()
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

@Preview(showSystemUi = true)
@Composable
fun previewDetail() {
    DetailScreen(
        "00:11:22:33:44:55",
        onConnect = {},
        onDisconnect = {},
        CradleLedBleClient.SDeviceStatus.READY
    )
}
