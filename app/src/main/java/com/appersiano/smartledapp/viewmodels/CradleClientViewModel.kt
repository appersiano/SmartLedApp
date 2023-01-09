package com.appersiano.smartledapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appersiano.smartledapp.client.CradleLedBleClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val TAG = "CradleClientViewModel"

class CradleClientViewModel(application: Application) : AndroidViewModel(application) {

    private val bleClient by lazy { CradleLedBleClient(application) }

    //region MutableStateFlow
    val bleDeviceStatus = bleClient.deviceConnectionStatus
    val ledStatusBoolean = bleClient.ledStatus.map { it.toBool() }
    val pirStatusBoolean = bleClient.pirStatus.map { it.toBool() }
    val brightnessValue = mutableStateOf(0f)
    //endregion

    init {
        viewModelScope.launch {
            bleClient.brightness.collect {
                brightnessValue.value = it.toFloat()
            }
        }
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

    fun setCurrentTime(hour: Int, minute: Int, second: Int, day: Int, month: Int, year: Int) {
        bleClient.setCurrentTime(hour, minute, second, day, month, year)
    }

    fun setTimerFeature(value: Boolean, hourON: Int, minuteON: Int, hourOFF: Int, minuteOFF: Int) {
        if (value) {
            bleClient.setTimerFeature(
                CradleLedBleClient.ESwitch.ON,
                hourON,
                minuteON,
                hourOFF,
                minuteOFF
            )
        } else {
            bleClient.setTimerFeature(
                CradleLedBleClient.ESwitch.OFF,
                hourON,
                minuteON,
                hourOFF,
                minuteOFF
            )
        }
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
