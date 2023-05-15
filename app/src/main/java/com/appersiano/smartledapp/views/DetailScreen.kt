package com.appersiano.smartledapp.views

import android.util.Log
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
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.appersiano.smartledapp.R
import com.appersiano.smartledapp.client.CradleLedBleClient
import com.appersiano.smartledapp.toInt
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
    var slideMinBrightness by remember { viewModel.pirMinBrightness }
    val checkedPir by remember { viewModel.pirStatusBoolean }

    Column {
        Row {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                text = "PIR Status")
            Spacer(modifier = Modifier.width(5.dp))

            Button(
                onClick = {
                    viewModel.setPIRStatus(slideMinBrightness.toInt(), true)
                }
            ) {
                Text(text = "ON")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.setPIRStatus(slideMinBrightness.toInt(), false)
                }
            ) {
                Text(text = "OFF")
            }
            Spacer(modifier = Modifier.width(5.dp))
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
                    .background(getOnOffColor(checkedPir), shape = CircleShape)
                    .requiredSize(15.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        Row {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_brightness_24),
                contentDescription = "Brightness"
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text("Min Brightness")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = slideMinBrightness.toInt().toString())
        }
        Slider(
            value = slideMinBrightness,
            onValueChange = {
                slideMinBrightness = it
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary
            )
        )
    }
}

@Composable
private fun SetLEDStatusRow(viewModel: CradleClientViewModel) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        val checkedLed = remember { viewModel.ledStatusBoolean }
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

}

@Composable
private fun SetLEDColor(viewModel: CradleClientViewModel) {

    var mRGBColor by remember { viewModel.rgbValue }

    var textValueRed = mRGBColor.red.toString()
    var sliderPositionRed = mRGBColor.red

    var textValueGreen = mRGBColor.green.toString()
    var sliderPositionGreen = mRGBColor.green

    var textValueBlue = mRGBColor.blue.toString()
    var sliderPositionBlue = mRGBColor.blue

    val composeColor = Color(mRGBColor.red, mRGBColor.green, mRGBColor.blue)

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
                        .background(composeColor, shape = CircleShape)
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
            Text(text = textValueRed)
        }
        Slider(
            value = sliderPositionRed.toFloat(),
            onValueChange = {
                Log.i("RGB", "float: ${it}")
                mRGBColor = android.graphics.Color.rgb(it.toInt(), mRGBColor.green, mRGBColor.blue)
                textValueRed = sliderPositionRed.toInt().toString()
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
            Text(text = textValueGreen.toString())
        }
        Slider(
            value = sliderPositionGreen.toFloat(),
            onValueChange = {
                mRGBColor = android.graphics.Color.rgb(mRGBColor.red, it.toInt(), mRGBColor.blue)
                textValueGreen = sliderPositionGreen.toString()
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
            Text(text = textValueBlue)
        }
        Slider(
            value = sliderPositionBlue.toFloat(),
            onValueChange = {
                mRGBColor = android.graphics.Color.rgb(mRGBColor.red, mRGBColor.green, it.toInt())
                textValueBlue = sliderPositionBlue.toInt().toString()
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
                sliderPositionRed,
                sliderPositionGreen,
                sliderPositionBlue
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set RGB")
    }
}

@Composable
fun SetLEDBrightness(viewModel: CradleClientViewModel) {
    var sliderBrightness by remember { viewModel.brightnessValue }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_brightness_24),
                contentDescription = "Brightness"
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "Brightness")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = sliderBrightness.toInt().toString())

        }
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { viewModel.readLEDBrightness() }) {
            Text(text = "R")
        }
    }
    Slider(
        value = sliderBrightness,
        onValueChange = {
            sliderBrightness = it
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
                sliderBrightness.toLong()
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set LED Brightness")
    }
}

@Composable
fun SetCurrentTime(viewModel: CradleClientViewModel) {
    val currentDateTime by remember { viewModel.currentTimeValue }

    var hourValue = currentDateTime.currentHour
    var minuteValue = currentDateTime.currentMinute
    var secondValue = currentDateTime.currentSecond
    var dayValue = currentDateTime.currentDay
    var monthValue = currentDateTime.currentMonth
    var yearValue = currentDateTime.currentYear

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
            value = hourValue.toString(),
            label = { Text("Hour") },
            onValueChange = {
                try {
                    hourValue = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    hourValue = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = minuteValue.toString(),
            label = { Text("Minute") },
            onValueChange = {
                try {
                    minuteValue = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    minuteValue = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = secondValue.toString(),
            label = { Text("Second") },
            onValueChange = {
                try {
                    secondValue = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    secondValue = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Row {
        TextField(
            modifier = Modifier.width(100.dp),
            value = dayValue.toString(),
            label = { Text("Day") },
            onValueChange = {
                try {
                    dayValue = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    dayValue = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = monthValue.toString(),
            label = { Text("Month") },
            onValueChange = {
                try {
                    monthValue = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    monthValue = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = yearValue.toString(),
            label = { Text("year") },
            onValueChange = {
                try {
                    yearValue = it.trim().toInt()
                } catch (e: java.lang.Exception) {
                    yearValue = 0
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Button(
        onClick = {
            viewModel.setCurrentTime(
                hourValue,
                minuteValue,
                secondValue,
                dayValue,
                monthValue,
                yearValue
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set Current Timer")
    }
}

@Composable
fun SetTimerFeature(viewModel: CradleClientViewModel) {
    var timerFeature by remember { viewModel.timerFeatureValue }

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
            checked = timerFeature.timeFeatureStatus.toBool(),
            onCheckedChange = {
                timerFeature =
                    timerFeature.copy(timeFeatureStatus = CradleLedBleClient.ESwitch.fromInt(it.toInt()))
            })
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row {
        Text("ON", modifier = Modifier.width(30.dp))
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = timerFeature.switchOnHour.toString(),
            label = { Text("Hour") },
            onValueChange = {
                try {
                    timerFeature = timerFeature.copy(switchOnHour = it.trim().toInt())
                } catch (e: java.lang.Exception) {
                    timerFeature = timerFeature.copy(switchOnHour = 0)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = timerFeature.switchOnMinute.toString(),
            label = { Text("Minute") },
            onValueChange = {
                try {
                    timerFeature = timerFeature.copy(switchOnMinute = it.trim().toInt())
                } catch (e: java.lang.Exception) {
                    timerFeature = timerFeature.copy(switchOnMinute = 0)
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
            value = timerFeature.switchOffHour.toString(),
            label = { Text("Hour") },
            onValueChange = {
                try {
                    timerFeature = timerFeature.copy(switchOffHour = it.trim().toInt())
                } catch (e: java.lang.Exception) {
                    timerFeature = timerFeature.copy(switchOffHour = 0)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            modifier = Modifier.width(100.dp),
            value = timerFeature.switchOffMinute.toString(),
            label = { Text("Minute") },
            onValueChange = {
                try {
                    timerFeature = timerFeature.copy(switchOffMinute = it.trim().toInt())
                } catch (e: java.lang.Exception) {
                    timerFeature = timerFeature.copy(switchOnMinute = 0)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
    Button(onClick = {
        viewModel.setTimerFeature(
            timerFeature.timeFeatureStatus.toBool(),
            timerFeature.switchOnHour,
            timerFeature.switchOnMinute,
            timerFeature.switchOffHour,
            timerFeature.switchOffMinute
        )
    }, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Set Timer Feature")
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
