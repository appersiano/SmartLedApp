package com.appersiano.smartledapp.views

import android.bluetooth.le.ScanResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.appersiano.smartledapp.scanner.SCScan

@Composable
fun MainScreen(
    navController: NavController,
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    scanStatus: State<SCScan?>,
    scanResult: SnapshotStateList<ScanResult>
) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
        ) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = onStartScan) {
                Text(text = "Start Scan")
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = onStopScan) {
                Text(text = "Stop Scan!")
            }

            var counter = 1.0f
            var color = Color.Red
            val coroutine = rememberCoroutineScope()

            when (scanStatus.value) {
                is SCScan.ERROR -> {
                    counter = 1f
                    color = Color.Red
                }
                SCScan.PAUSE -> {
                    counter = 0.5f
                    color = Color.Yellow
                }
                SCScan.START -> {
                    counter = 1f
                    color = Color.Green
                }
                SCScan.STOP -> {
                    counter = 1f
                    color = Color.Red
                }
                SCScan.UNKNOWN -> {
                    counter = 1f
                    color = Color.Gray
                }
                null -> {
                    counter = 0f
                }
            }

            ProgressScan(counter, color)

            ScanStatusText(scanStatus.value.toString())

            LazyColumn {
                items(scanResult) {
                    //Text(it.device.address)
                    DevicePickerItem(navController, it.device.address, it.rssi)
                }
            }
        }
    }
}

@Composable
fun DevicePickerItem(navController: NavController, macAddress: String, rssi: Int) {
    Column {
        ClickableText(
            text = AnnotatedString("MacAddress: $macAddress"),
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                navController.navigate("detail/$macAddress")
            }
        )
        Text(text = "RSSI: $rssi")
    }
}

@Composable
fun ScanStatusText(scanStatus: String) {
    Text(
        text = "Scan Status: $scanStatus",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Composable
fun ProgressScan(value: Float, colorState: Color) {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = colorState,
        progress = value
    )
}