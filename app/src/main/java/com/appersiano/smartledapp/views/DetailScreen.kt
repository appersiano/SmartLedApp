package com.appersiano.smartledapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appersiano.smartledapp.client.SmartLedBleClient

@Composable
fun DetailScreen(
    macAddress: String,
    onConnect: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    status: SmartLedBleClient.SDeviceStatus,
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .padding(16.dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
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
        Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
    }
}

fun getStatusColor(status: SmartLedBleClient.SDeviceStatus): Color {
    return when (status) {
        SmartLedBleClient.SDeviceStatus.CONNECTED -> Color.Yellow
        SmartLedBleClient.SDeviceStatus.READY -> Color.Green
        SmartLedBleClient.SDeviceStatus.UNKNOWN -> Color.Gray
        is SmartLedBleClient.SDeviceStatus.DISCONNECTED -> Color.Red
    }
}

@Preview(showSystemUi = true)
@Composable
fun previewDetail() {
    DetailScreen(
        "00:11:22:33:44:55",
        onConnect = {},
        onDisconnect = {},
        SmartLedBleClient.SDeviceStatus.READY
    )
}
