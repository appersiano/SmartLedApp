package com.appersiano.smartledapp.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appersiano.smartledapp.R

@Composable
fun RemoteControlScreen(
//    viewModel: CradleClientViewModel,
//    macAddress: String,
//    onConnect: () -> Unit = {},
//    onDisconnect: () -> Unit = {},
//    status: CradleLedBleClient.SDeviceStatus,
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .verticalScroll(scrollState)
            .fillMaxHeight()
    ) {
        TopColorSelectionRow()
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            Spacer(modifier = Modifier.size(32.dp))
            ColorOrTemperatureRow()
            SeekBarBrightness()
            SelectionSceneRow()
            Divider(thickness = 1.dp, color = Color.Gray)
            PIRFunctionRow()
            Divider(thickness = 1.dp, color = Color.Gray)
            ProgrammingOnOffRow()
            OnOffButtonsRow()
        }
    }
}

@Composable
private fun HalfArcGradient(modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE55D5D), Color(0x00E55D5D)),
                center = Offset(x = 150.dp.toPx(), y = 0.dp.toPx()),
                tileMode = TileMode.Clamp,
                radius = 150.dp.toPx()
            ),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(x = 0.dp.toPx(), y = -150.dp.toPx())
        )
    }
}

@Composable
private fun CircularGradientPicker(modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE55D5D), Color(0x00E55D5D)),
                center = Offset(x = 150.dp.toPx(), y = 0.dp.toPx()),
                tileMode = TileMode.Clamp,
                radius = 150.dp.toPx()
            ),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(x = 0.dp.toPx(), y = -150.dp.toPx())
        )
    }
}

@Preview
@Composable
fun PreviewPicker() {
    val widthElement = 6.dp
    val halfWidthElement = 6.dp / 2
    val heightElement = 50.dp
    val halfHeightElement = 50.dp / 2
    Canvas(
        modifier = Modifier.size(300.dp)
    ) {
        val rect = Rect(Offset.Zero, Size(widthElement.toPx(), heightElement.toPx()))
        for (i in 1..340 step 6){
            rotate(degrees = -i.toFloat()) {
                drawRect(
                    color = Color.Red,
                    size = Size(width = widthElement.toPx(), height = heightElement.toPx()),
                    topLeft = Offset(
                        x = 155.dp.toPx(),
                        y = 300.dp.toPx() - heightElement.toPx()
                    )
                )
            }
        }

        drawRect(
            color = Color.Yellow,
            size = Size(width = widthElement.toPx(), height = heightElement.toPx()),
            topLeft = Offset(
                x = 150.dp.toPx() - halfWidthElement.toPx(),
                y = 300.dp.toPx() - heightElement.toPx()
            )
        )

    }
}

@Composable
fun TopColorSelectionRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        HalfArcGradient(
            modifier = Modifier
                .height(150.dp)
                .width(300.dp)
                .align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_on_off_icon),
                    contentDescription = "Butt"
                )
            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "Scorri la ruota per scegliere una tonalità",
                color = Color.White
            )
        }
    }
}

@Composable
fun ColorOrTemperatureRow() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .width(143.dp)
                .height(76.dp)
                .border(1.dp, Color.White, shape = RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF323035), contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Colori")
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .width(143.dp)
                .height(76.dp)
                .border(1.dp, Color.White, shape = RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF323035), contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Temperatura")
        }
    }
}

@Composable
fun SeekBarBrightness() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_brightness),
            contentDescription = "Brightness"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Slider(
            modifier = Modifier.weight(1f),
            value = 50f,
            onValueChange = { /* TODO */ },
            valueRange = 0f..100f,
            steps = 1,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
            },

            )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "50%", color = Color.White)
    }
}

@Composable
fun SelectionSceneRow() {
    Column(Modifier.padding(16.dp, bottom = 24.dp, top = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Scene", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Select Scene"
            )
        }
        Text(
            "Seleziona una delle scene create da noi",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun PIRFunctionRow() {
    Column(Modifier.padding(16.dp, bottom = 24.dp, top = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Affievolisciti allontanandoti",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Select Scene"
            )
        }
        Text(
            "La luce ridurrà di intensità quando ti\nallontanerai",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProgrammingOnOffRow() {
    Column(Modifier.padding(16.dp, bottom = 24.dp, top = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Programmmazione",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Select Scene"
            )
        }
        Text(
            "Imposta degli orari di accensione e\nspegnimento del dispositivo",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun OnOffButtonsRow() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .width(143.dp)
                .height(92.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF323035), contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Accensione")
                Text("06:00")
            }

        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .width(143.dp)
                .height(92.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF323035), contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Spegnimento")
                Text("06:00")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview() {
    RemoteControlScreen()
}