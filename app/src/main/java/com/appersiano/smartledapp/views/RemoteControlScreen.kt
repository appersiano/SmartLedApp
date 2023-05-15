package com.appersiano.smartledapp.views

import android.app.TimePickerDialog
import android.graphics.Region
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.appersiano.smartledapp.R
import com.appersiano.smartledapp.client.CradleLedBleClient
import com.appersiano.smartledapp.viewmodels.CradleClientViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


@Composable
fun RemoteControlScreen(
    viewModel: CradleClientViewModel,
    macAddress: String,
    onConnect: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    status: CradleLedBleClient.SDeviceStatus,
) {
    val scrollState = rememberScrollState()

    val isLedEnable = remember { viewModel.ledStatusBoolean }
    val showTemperature = remember { mutableStateOf(false) }
    Box(
        Modifier
            .verticalScroll(scrollState)
            .fillMaxHeight()
    ) {

        TopColorSelectionRow(showTemperature.value, isLedEnable.value,
            toggle = { toogleLedEnable ->
                viewModel.toggleLedEnable(toogleLedEnable)
            }, selectedRGBColor = {
                viewModel.selectColor(it)
            })

        AnimatedVisibility(
            visible = !isLedEnable.value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.zIndex(10f)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 400.dp)
                    .fillMaxHeight()
                    .clickable { }
            ) {
                OffStateScreen(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
            visible = isLedEnable.value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.zIndex(10f)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 250.dp, start = 16.dp, end = 16.dp)
                    .zIndex(10f)
                    .clickable { }
            ) {
                Spacer(modifier = Modifier.size(32.dp))
                ColorOrTemperatureRow(showTemperature)
                Spacer(modifier = Modifier.size(16.dp))
                SeekBarBrightness(viewModel.brightnessValue.value.normalizeFrom255().toInt()) {
                    viewModel.setLEDBrightnessZeroOneHundred(it)
                }
                //SelectionSceneRow()
                Divider(thickness = 1.dp, color = Color.Gray)
                PIRFunctionRow(
                    pirStatus = viewModel.pirStatusBoolean.value,
                    minBrightness = viewModel.pirMinBrightness.value.normalizeFrom255().toInt(),
                    onCheckedChange = {
                        viewModel.setPIRStatus(null, it)
                    }, onMinBrightness = {
                        viewModel.setPIRStatus(it, null)
                    })
                Divider(thickness = 1.dp, color = Color.Gray)
                ProgrammingOnOffRow()
                OnOffButtonsRow()
            }
        }
    }
}

@Composable
private fun OffStateScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_led_strips),
            contentDescription = "Led Strips Icon"
        )
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = "Funzioni disabilitate",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "Per abilitare nuovamente le funzioni,\naccendi la luce",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun HalfArcGradient(modifier: Modifier, currentSelectedColor: MutableState<Color>) {
    Canvas(
        modifier = modifier
    ) {
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(currentSelectedColor.value, Color(0x00272530)),
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

private const val TAG = "RemoteControlScreen"

@Composable
fun ColorPickerWheel(
    onSelectedColor: (Color) -> Unit,
    onDragEnd: (Color) -> Unit,
    showTemperature: Boolean = false,
    isLedEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current

    val viewSize = 520.dp
    val widthElement = 6.dp
    val heightElement = 50.dp

    val offsetX = remember { mutableStateOf(0f) }
    val lineLists = remember { mutableListOf<Path>() }
    val elementsColor = remember { mutableListOf<Color>() }
    var elementsColorTemperature = remember { listOf<Color>() }
    val selectedColor = remember { mutableStateOf(Color.Unspecified) }

    lineLists.clear()
    elementsColor.clear()

    if (showTemperature) {
        elementsColorTemperature = LEDTemperatureUtils.generateTemperatureArray(120)
    }

    Canvas(
        modifier = Modifier
            .size(viewSize)
            .pointerInput(isLedEnabled) {
                detectDragGestures(onDrag = { change, dragAmount ->
                    if (isLedEnabled) {
                        change.consume()
                        offsetX.value -= (dragAmount.x / 10)

                        val region = Region()
                        val left = this.size.width / 2 - widthElement.toPx() / 2
                        val top = (this.size.height - heightElement.toPx()).toInt()
                        val right = (this.size.width / 2 + widthElement.toPx() / 2).toInt()
                        val bottom = this.size.height

                        region.set(
                            left.toInt(),
                            top,
                            right,
                            bottom
                        )

                        lineLists.forEachIndexed { index, currentPath ->
                            val leftPath = currentPath.getBounds().left.toInt()
                            val topPath = currentPath.getBounds().top.toInt()

                            if (region.contains(
                                    leftPath,
                                    topPath
                                )
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedColor.value = elementsColor.toList()[index]
                                onSelectedColor.invoke(selectedColor.value)
                                return@detectDragGestures
                            }
                        }
                    }

                }, onDragEnd = {
                    //callback to send command
                    onDragEnd.invoke(selectedColor.value)
                })
            }
    ) {

        lineLists.clear()
        elementsColor.clear()

        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = 4.dp.toPx()

        //translate let show "half" circle outside the screen
        val translateY = -750f
        translate(top = translateY) {
            //this let rotate the while when dragged horizontally
            rotate(degrees = offsetX.value) {
                var hue = 0f//175f
                //this for effectively create the wheel
                for (i in 0 until 360 step 3) {

                    rotate(degrees = -i.toFloat()) {
                        val calcColor: Color
                        val currentHueCalculated: Float
                        if (isLedEnabled) {
                            if (showTemperature) {
                                calcColor = elementsColorTemperature[i / 3]
                            } else {
                                currentHueCalculated = hue + 3f

                                hue = if (currentHueCalculated <= 360f) {
                                    currentHueCalculated
                                } else {
                                    0f
                                }

                                calcColor = Color.hsl(hue, 0.72f, 0.63f)
                            }
                        } else {
                            calcColor = Color.White
                        }

                        elementsColor.add(calcColor)

                        val startPoint = Offset(
                            x = this.center.x,
                            y = this.size.height - (heightElement.toPx())
                        )
                        val endPoint = Offset(
                            x = this.center.x,
                            y = this.size.height
                        )

                        val matrix = android.graphics.Matrix()
                        matrix.setRotate(-i.toFloat() + offsetX.value, this.center.x, this.center.y)
                        val pts = floatArrayOf(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
                        matrix.mapPoints(pts)

                        val path = Path().apply {
                            moveTo(pts[0], pts[1])
                            lineTo(pts[2], pts[3])
                        }
                        lineLists.add(path)

                        if (isLedEnabled) {
                            drawLine(
                                color = calcColor,
                                start = startPoint,
                                end = endPoint,
                                strokeWidth = 6.dp.toPx()
                            )
                        } else {
                            val brush = Brush.horizontalGradient(listOf(Color.Black, Color.White))
                            drawLine(
                                start = startPoint,
                                end = endPoint,
                                strokeWidth = 6.dp.toPx(),
                                brush = brush
                            )
                        }
                    }
                }
            }

            //Small White Dot over the selected color
            if (isLedEnabled) {
                drawCircle(
                    color = Color.White,
                    center = Offset(
                        x = canvasWidth / 2,
                        y = canvasHeight - heightElement.toPx() - radius * 2
                    ),
                    radius = radius
                )
            }
        }
    }
}

fun getRandomColor(): Int {
    val rnd = Random(255)
    return android.graphics.Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}


@Composable
fun TopColorSelectionRow(
    showTemperature: Boolean, isLedEnabled: Boolean,
    toggle: (Boolean) -> Unit,
    selectedRGBColor: (Color) -> Unit
) {

    val currentSelectedColor = remember { mutableStateOf(Color.Red) }
    if (isLedEnabled) {
        //color from viewmodel
    } else {
        currentSelectedColor.value = Color(0xFF919191)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .zIndex(1f),
    ) {
        ColorPickerWheel(
            onSelectedColor = {
                currentSelectedColor.value = it
                selectedRGBColor.invoke(it)
            }, onDragEnd = {
                //selectedRGBColor.invoke(it)
            },
            showTemperature,
            isLedEnabled
        )
        HalfArcGradient(
            modifier = Modifier
                .height(150.dp)
                .width(300.dp)
                .align(Alignment.TopCenter),
            currentSelectedColor
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconToggleButton(
                checked = isLedEnabled,
                onCheckedChange = {
                    toggle.invoke(it)
                }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_on_off_icon),
                    contentDescription = "Button ON/OFF"
                )
            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "Scorri la ruota per scegliere una\ntonalità",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ColorOrTemperatureRow(showTemperature: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                showTemperature.value = false
            },
            modifier = Modifier
                .width(143.dp)
                .height(76.dp)
                .border(
                    1.dp,
                    if (!showTemperature.value) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF323035), contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Colori")
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                showTemperature.value = true
            },
            modifier = Modifier
                .width(143.dp)
                .height(76.dp)
                .border(
                    1.dp,
                    if (showTemperature.value) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(24.dp)
                ),
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
fun SeekBarBrightness(value : Int, block: (Int) -> Unit) {
    var brightness by remember { mutableStateOf(value) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_brightness),
            contentDescription = "Brightness"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Slider(
            modifier = Modifier.weight(1f).background(Color.Red),
            value = brightness.toFloat(),
            onValueChange = {
                brightness = it.toInt()
                block.invoke(brightness)
            },
            valueRange = 0f..100f,
            onValueChangeFinished = {
                //nothing
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "${brightness}%", color = Color.White)
    }
}

@Composable
fun SeekBarMinBrightness(minBrightness: Int, onMinBrightness: (Int) -> Unit) {
    var brightness = minBrightness
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_brightness),
            contentDescription = "Brightness"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Slider(
            modifier = Modifier.weight(1f),
            value = brightness.toFloat(),
            onValueChange = { brightness = it.toInt() },
            valueRange = 0f..100f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                onMinBrightness.invoke(brightness)
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "${brightness}%", color = Color.White)
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
fun PIRFunctionRow(pirStatus : Boolean, minBrightness : Int,  onCheckedChange: (Boolean) -> Unit, onMinBrightness: (Int) -> Unit) {
    val checkedState = pirStatus
    Column(Modifier.padding(bottom = 24.dp, top = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                "Affievolisciti allontanandoti",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = checkedState,
                onCheckedChange = {
                    onCheckedChange.invoke(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF3FA02F),
                    uncheckedTrackColor = Color.Gray,
                    checkedTrackAlpha = 1.0f,
                    uncheckedTrackAlpha = 1.0f
                )
            )
        }
        Text(
            "La luce ridurrà di intensità quando ti\nallontanerai",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
        if (checkedState) {
            SeekBarMinBrightness(minBrightness, onMinBrightness)
        }
    }
}

@Composable
fun ProgrammingOnOffRow() {
    val checkedState = remember { mutableStateOf(true) }
    Column(Modifier.padding(16.dp, bottom = 24.dp, top = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Text(
                "Programmmazione",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF3FA02F),
                    uncheckedTrackColor = Color.Gray,
                    checkedTrackAlpha = 1.0f,
                    uncheckedTrackAlpha = 1.0f
                )
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
    val value = ""
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val time = if (value.isNotBlank()) LocalTime.parse(value, formatter) else LocalTime.now()
    val dialog = TimePickerDialog(
        LocalContext.current,
        { _, hour, minute ->
            //onValueChange(LocalTime.of(hour, minute).toString())
        },
        time.hour,
        time.minute,
        true,
    )

    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { dialog.show() },
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
            onClick = { dialog.show() },
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