package com.appersiano.smartledapp.viewmodels

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.annotation.IntRange
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appersiano.smartledapp.client.CradleLedBleClient
import com.appersiano.smartledapp.toInt
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "CradleClientViewModel"

class CradleClientViewModel(application: Application) : AndroidViewModel(application) {

    private val bleClient by lazy { CradleLedBleClient(application) }

    //region MutableStateFlow
    val bleDeviceStatus = bleClient.deviceConnectionStatus
    val ledStatusBoolean = mutableStateOf(false)
    val pirStatusBoolean = mutableStateOf(false)
    val pirMinBrightness = mutableStateOf(0f)
    val rgbValue = mutableStateOf(0)
    val brightnessValue = mutableStateOf(0f)
    val currentTimeValue = mutableStateOf(CradleLedBleClient.CurrentTimeDTO(0, 0, 0, 0, 0, 0))
    val timerFeatureValue = mutableStateOf(
        CradleLedBleClient.FeatureTimerDTO(
            CradleLedBleClient.ESwitch.ON,
            0,
            0,
            0,
            0
        )
    )
    //endregion

    init {
        viewModelScope.launch {
            bleClient.ledColor.collect {
                rgbValue.value = it
            }
        }
        viewModelScope.launch {
            bleClient.brightness.collect {
                brightnessValue.value = it.toFloat()
            }
        }
        viewModelScope.launch {
            bleClient.currentTime.collect {
                currentTimeValue.value = it
            }
        }
        viewModelScope.launch {
            bleClient.featureTime.collect {
                timerFeatureValue.value = it
            }
        }

        viewModelScope.launch {
            bleClient.pirStatus.collect {
                pirStatusBoolean.value = it.switch.toBool()
                pirMinBrightness.value = it.minBrightness.toFloat()
            }
        }

        viewModelScope.launch {
            bleClient.ledStatus.map {
                ledStatusBoolean.value = it.toBool()
            }
        }
        initCurrentTime()
    }

    private fun initCurrentTime() {
        val currentDateTime = Calendar.getInstance()

        currentTimeValue.value = CradleLedBleClient.CurrentTimeDTO(
            currentDateTime.get(Calendar.HOUR_OF_DAY),
            currentDateTime.get(Calendar.MINUTE),
            currentDateTime.get(Calendar.SECOND),
            currentDateTime.get(Calendar.DAY_OF_MONTH),
            (currentDateTime.get(Calendar.MONTH) + 1),
            currentDateTime.get(Calendar.YEAR) - 2000
        )
    }

    fun connect(macAddress: String) {
        bleClient.connect(macAddress)
    }

    fun disconnect() {
        bleClient.disconnect()
    }

    fun setLEDStatus(value: Boolean) {
        bleClient.setLEDStatus(CradleLedBleClient.ESwitch.fromInt(value.toInt()))
    }

    fun setPIRStatus(minBrightness: Int, value: Boolean) {
        this.pirMinBrightness.value = minBrightness.toFloat()
        pirStatusBoolean.value = value
        bleClient.setPIRStatus(minBrightness, CradleLedBleClient.ESwitch.fromInt(value.toInt()))
    }

    fun setLEDColor(red: Int, green: Int, blue: Int) {
        rgbValue.value = Color.rgb(red, green, blue)
        bleClient.setLEDColor(red, green, blue)
    }

    fun setLEDBrightnessZeroOneHundred(@IntRange(from = 0L, to = 100L) value: Int) {
        brightnessValue.value = value.toFloat()
        val completeValue = value * 255 / 100
        bleClient.setLEDBrightness(completeValue.toLong())
    }
    fun setLEDBrightness(@IntRange(from = 0L, to = 255L) value: Long) {
        brightnessValue.value = value.toFloat()
        bleClient.setLEDBrightness(value)
    }

    fun setCurrentTime(
        hour: Int,
        minute: Int,
        second: Int,
        day: Int,
        month: Int,
        year: Int
    ) {
        val currentTimeDTO =
            CradleLedBleClient.CurrentTimeDTO(hour, minute, second, day, month, year)
        currentTimeValue.value = currentTimeDTO
        bleClient.setCurrentTime(hour, minute, second, day, month, year)
    }

    fun setTimerFeature(
        value: CradleLedBleClient.ESwitch,
        hourON: Int,
        minuteON: Int,
        hourOFF: Int,
        minuteOFF: Int
    ) {
        timerFeatureValue.value = CradleLedBleClient.FeatureTimerDTO(
            value,
            hourON,
            minuteON,
            hourOFF,
            minuteOFF
        )

        bleClient.setTimerFeature(
            value,
            hourON,
            minuteON,
            hourOFF,
            minuteOFF
        )
    }

    fun readLedStatus() {
        bleClient.readLEDStatus()
    }

    fun readPIRStatus() {
        bleClient.readPIRStatus()
    }

    fun readLEDColor() {
        bleClient.readLEDColor()
    }

    fun readLEDBrightness() {
        bleClient.readLEDBrightness()
    }

    fun readCurrentTime() {
        bleClient.readCurrentTime()
    }

    fun readTimerFeature() {
        bleClient.readTimerFeature()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared: ")
    }

    fun toggleLedEnable(value: Boolean) {
        ledStatusBoolean.value = value
        setLEDStatus(value)
    }

    fun selectColor(it: androidx.compose.ui.graphics.Color) {
        setLEDColor(it.toArgb().red, it.toArgb().green, it.toArgb().blue)
    }
}
