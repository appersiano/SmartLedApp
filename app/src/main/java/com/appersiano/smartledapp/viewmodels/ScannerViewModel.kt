package com.appersiano.smartledapp.viewmodels

import android.app.Application
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appersiano.smartledapp.scanner.CradleSmartLEDBleScanner
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ScannerViewModel is in charge to manage the scanning process and logic
 */
private const val TAG = "ScannerViewModel"

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val tataPadDeviceScanner by lazy { CradleSmartLEDBleScanner(application) }

    val scanStatus = tataPadDeviceScanner.scanStatus
    val listDevices = mutableStateListOf<ScanResult>()

    init {
        viewModelScope.launch {
            tataPadDeviceScanner.scanResultFlow.receiveAsFlow().collect {
                listDevices.add(it)
            }
        }
    }

    fun startScan(scanDuration: Int, scanInterval: Int) {
        tataPadDeviceScanner.startScan(scanDuration, scanInterval)
        listDevices.clear()
    }

    fun stopScan() {
        tataPadDeviceScanner.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared: ")
    }
}
