package com.appersiano.smartledapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import java.util.*

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
            .padding(24.dp)
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
        SetLEDStatusRow(viewModel)
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
        SetPiRStatusRow(viewModel)
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
private fun SetPiRStatusRow(viewModel: CradleClientViewModel) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        val checkedPir = viewModel.pirStatusBoolean.collectAsState(initial = false)
        SetPIRStatusCommands(viewModel)
        Divider(
            modifier = Modifier
                .fillMaxHeight()  //fill the max height
                .width(1.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Button(onClick = { viewModel.readPIRStatus() }) {
            Text(text = "R")
        }
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .background(getOnOffColor(checkedPir.value), shape = CircleShape)
                .requiredSize(15.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SetLEDStatusRow(viewModel: CradleClientViewModel) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        val checkedLed = viewModel.ledStatusBoolean.collectAsState(initial = false)
        SetLEDStatusCommands(viewModel)
        Divider(
            modifier = Modifier
                .fillMaxHeight()  //fill the max height
                .width(1.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Button(onClick = { viewModel.readLedStatus() }) {
            Text(text = "R")
        }
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .background(getOnOffColor(checkedLed.value), shape = CircleShape)
                .requiredSize(15.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SetLEDStatusCommands(viewModel: CradleClientViewModel) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "LED Status"
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Button(
            onClick = {
                viewModel.setLEDStatus(true)
            }
        ) {
            Text(text = "ON")
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Button(
            onClick = {
                viewModel.setLEDStatus(false)
            }
        ) {
            Text(text = "OFF")
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )
    }
}

@Composable
private fun SetPIRStatusCommands(viewModel: CradleClientViewModel) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "PIR Status"
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Button(
            onClick = {
                viewModel.setPIRStatus(true)
            }
        ) {
            Text(text = "ON")
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Button(
            onClick = {
                viewModel.setPIRStatus(false)
            }
        ) {
            Text(text = "OFF")
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )
    }
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
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_color_lens_24),
                    contentDescription = "Brightness"
                )
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
            Button(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { viewModel.readLEDColor() }) {
                Text(text = "R")
            }
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

@Composable
fun SetLEDBrightness(viewModel: CradleClientViewModel) {
    val sliderBrightness = remember { mutableStateOf(0f) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_brightness_24),
                contentDescription = "Brightness"
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "Brightness")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = sliderBrightness.value.toInt().toString())

        }
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { viewModel.readLEDBrightness() }) {
            Text(text = "R")
        }
    }
    Slider(
        value = sliderBrightness.value,
        onValueChange = {
            sliderBrightness.value = it
        },
        valueRange = 0f..255f,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.primary,
            activeTrackColor = MaterialTheme.colors.primary
        )
    )
    Button(
        onClick = {
            viewModel.setLEDBrightness(
                sliderBrightness.value.toLong()
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set LED Brightness")
    }
}

@Composable
fun SetCurrentTime(viewModel: CradleClientViewModel) {
    val currentDateTime = Calendar.getInstance()

    val hourValue = remember { mutableStateOf(currentDateTime.get(Calendar.HOUR_OF_DAY)) }
    val minuteValue = remember { mutableStateOf(currentDateTime.get(Calendar.MINUTE)) }
    val secondValue = remember { mutableStateOf(currentDateTime.get(Calendar.SECOND)) }
    val dayValue = remember { mutableStateOf(currentDateTime.get(Calendar.DAY_OF_MONTH)) }
    val monthValue = remember { mutableStateOf(currentDateTime.get(Calendar.MONTH) + 1) }
    val yearValue = remember { mutableStateOf(currentDateTime.get(Calendar.YEAR) - 2000) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_access_time_24),
                contentDescription = "Current Time"
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "Current Time")

        }
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { viewModel.readCurrentTime() }) {
            Text(text = "R")
        }
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row {
        TextField(
            modifier = Modifier.width(100.dp),
            value = hourValue.value.toString(),
            label = { Text("Hour") },
            onValueChange = {
                try {
                    hourValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    hourValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteValue.value.toString(),
            label = { Text("Minute") },
            onValueChange = {
                try {
                    minuteValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    minuteValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = secondValue.value.toString(),
            label = { Text("Second") },
            onValueChange = {
                try {
                    secondValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    secondValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Row {
        TextField(
            modifier = Modifier.width(100.dp),
            value = dayValue.value.toString(),
            label = { Text("Day") },
            onValueChange = {
                try {
                    dayValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    dayValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = monthValue.value.toString(),
            label = { Text("Month") },
            onValueChange = {
                try {
                    monthValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    monthValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = yearValue.value.toString(),
            label = { Text("year") },
            onValueChange = {
                try {
                    yearValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    yearValue.value = 0
                }
            },
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
fun SetTimerFeature(viewModel: CradleClientViewModel) {
    val switchValue = remember { mutableStateOf(false) }

    val hourONValue = remember { mutableStateOf(0) }
    val hourOFFValue = remember { mutableStateOf(0) }
    val minuteONValue = remember { mutableStateOf(0) }
    val minuteOFFValue = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_timer_24),
                contentDescription = "Time Feature"
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "Timer Feature")

        }
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { viewModel.readTimerFeature() }) {
            Text(text = "R")
        }
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
            onValueChange = {
                try {
                    hourONValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    hourONValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteONValue.value.toString(),
            label = { Text("Minute") },
            onValueChange = {
                try {
                    minuteONValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    minuteONValue.value = 0
                }
            },
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
            onValueChange = {
                try {
                    hourOFFValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    hourOFFValue.value = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteOFFValue.value.toString(),
            label = { Text("Minute") },
            onValueChange = {
                try {
                    minuteOFFValue.value = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    minuteOFFValue.value = 0
                }
            },
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

fun updateCircleRGBColor(RGBColor: MutableState<Color>, red: Int, green: Int, blue: Int) {
    val color = android.graphics.Color.rgb(red, green, blue)
    RGBColor.value = Color(color)
}

fun getStatusColor(status: CradleLedBleClient.SDeviceStatus): Color {
    return when (status) {
        CradleLedBleClient.SDeviceStatus.CONNECTED -> Color.Yellow
        CradleLedBleClient.SDeviceStatus.READY -> Color.Green
        CradleLedBleClient.SDeviceStatus.UNKNOWN -> Color.Gray
        is CradleLedBleClient.SDeviceStatus.DISCONNECTED -> Color.Red
    }
}

fun getOnOffColor(status: Boolean): Color {
    return when (status) {
        true -> Color.Green
        false -> Color.Red
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
