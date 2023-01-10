package com.appersiano.smartledapp.viewmodels

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appersiano.smartledapp.client.CradleLedBleClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "CradleClientViewModel"

class CradleClientViewModel(application: Application) : AndroidViewModel(application) {

    private val bleClient by lazy { CradleLedBleClient(application) }

    //region MutableStateFlow
    val bleDeviceStatus = bleClient.deviceConnectionStatus
    val ledStatusBoolean = bleClient.ledStatus.map { it.toBool() }
    val pirStatusBoolean = bleClient.pirStatus.map { it.toBool() }
    val rgbValue = mutableStateOf(Color.valueOf(0f, 0f, 0f))
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
        if (value) {
            bleClient.setLEDStatus(CradleLedBleClient.ESwitch.ON)
        } else {
            bleClient.setLEDStatus(CradleLedBleClient.ESwitch.OFF)
        }
    }

    fun setPIRStatus(value: Boolean) {
        if (value) {
            bleClient.setPIRStatus(CradleLedBleClient.ESwitch.ON)
        } else {
            bleClient.setPIRStatus(CradleLedBleClient.ESwitch.OFF)
        }
    }

    fun setLEDColor(red: Long, green: Long, blue: Long) {
        bleClient.setLEDColor(red, green, blue)
    }

    fun setLEDBrightness(value: Long) {
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
}
