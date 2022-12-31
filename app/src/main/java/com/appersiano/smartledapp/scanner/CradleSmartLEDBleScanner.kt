package com.appersiano.smartledapp.scanner

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.appersiano.smartledapp.client.SmartLedUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "CradleScanner"

/**
 * BleScanner define the mechanism to scan and find Ble devices.
 */
class CradleSmartLEDBleScanner(private val context: Context) {

    private var localScope = CoroutineScope(Dispatchers.IO)
    private var scanningScope = CoroutineScope(Dispatchers.IO)

    private val _bleManager =
        context.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager

    private val scanFilter =
        ScanFilter.Builder().setServiceUuid(ParcelUuid(SmartLedUUID.CradleSmartLightService.uuid))
            .build()
    private val scanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
    private val setMatches = hashMapOf<String, ScanResult>()

    val scanStatus = MutableStateFlow<SCScan>(SCScan.UNKNOWN)
    val scanResultFlow: Channel<ScanResult> by lazy { Channel {} }

    /**
     * ScanCallback return when a ble device is found.
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                localScope.launch {
                    val elementExist = setMatches[it.device.address]
                    if (elementExist == null) {
                        Log.i(TAG, "Smart LED detected: " + it.device.address)
                        setMatches[it.device.address] = it
                        scanResultFlow.send(it)
                    } else {
                        Log.i(TAG, "Exist Element - SKIP " + it.device.address)
                    }
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            localScope.launch {
                scanStatus.emit(SCScan.ERROR(errorCode))
                stopRollingScan(true)
            }
        }
    }

    /**
     * stopRollingScan let stop the scanning cycle definitely or pause it
     *
     * @param stopAll true to stop scan, false to pause
     */
    @Throws(SecurityException::class)
    private fun stopRollingScan(stopAll: Boolean = false) {
        if (!hasScanPermission()) {
            throw buildSecurityException()
        } else {
            _bleManager.adapter.bluetoothLeScanner?.stopScan(
                scanCallback
            )

            localScope.launch {
                if (stopAll) {
                    Log.i(TAG, "STOP Scan >")
                    scanStatus.emit(SCScan.STOP)
                } else {
                    scanStatus.emit(SCScan.PAUSE)
                }
            }

            if (stopAll) {
                setMatches.clear()
                scanningScope.cancel()
            }
        }
    }

    /**
     * Start scanning for iBeacon devices.
     *
     * @param scanDuration The duration of the scan in seconds
     */
    @Throws(SecurityException::class)
    fun startScan(
        @IntRange(from = 0, to = 30) scanDuration: Int = 20,
        @IntRange(from = 0, to = 30) scanInterval: Int = 5
    ) {
        if (!hasScanPermission()) {
            throw buildSecurityException()
        } else {
            _bleManager.adapter.bluetoothLeScanner?.stopScan(scanCallback)

            localScope.cancel()
            scanningScope.cancel()

            _bleManager.adapter.bluetoothLeScanner?.startScan(
                mutableListOf(scanFilter), scanSettings, scanCallback
            )

            localScope = CoroutineScope(Dispatchers.IO)
            localScope.launch {
                Log.i(
                    TAG, "Start Scan > scanDuration: $scanDuration, scanInterval: $scanInterval"
                )
                scanStatus.emit(SCScan.START)
            }

            scanningScope = CoroutineScope(Dispatchers.IO)
            scanningScope.launch {
                delay(TimeUnit.SECONDS.toMillis(scanDuration.toLong()))
                stopRollingScan()
                Log.i(TAG, "Scan Sleep: 😴")
                delay(TimeUnit.SECONDS.toMillis(scanInterval.toLong()))
                startScan(scanDuration, scanInterval)
            }
        }
    }

    /**
     * Stop scanning for iBeacon devices.
     */
    fun stopScan() = stopRollingScan(true)

    /**
     * Clear cache of found devices
     */
    fun clearCaches() = setMatches.clear()

    /**
     * Clear cache for specific device
     *
     * @param macAddress MacAddress of the device to remove from cache
     */
    fun clearCachesForMacAddress(macAddress: String) = setMatches.remove(macAddress)

    /**
     * Check if the app has the necessary permissions to scan for iBeacon devices.
     */
    private fun hasScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.hasPermissions(
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            context.hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Build a SecurityException with a message that explains why the app does not have the necessary
     * permissions to scan for iBeacon devices.
     */
    private fun buildSecurityException(): SecurityException {
        val stringBuilder = StringBuilder()

        if (!context.hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
            stringBuilder.append("Missing ACCESS_FINE_LOCATION permission")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!context.hasPermissions(Manifest.permission.BLUETOOTH_SCAN)) {
                stringBuilder.append("Missing BLUETOOTH_SCAN permission")
            }
        }

        return SecurityException(stringBuilder.toString())
    }
}

fun Context.hasPermissions(vararg permissions: String): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun Byte.toHex() = "%02x".format(this)

sealed class SCScan {
    object START : SCScan() {
        override fun toString() = "START"
    }

    object PAUSE : SCScan() {
        override fun toString() = "PAUSE"
    }

    object STOP : SCScan() {
        override fun toString() = "STOP"
    }

    class ERROR(val errorCode: Int) : SCScan() {
        override fun toString() = "ERROR $errorCode"
    }

    object UNKNOWN : SCScan() {
        override fun toString() = "UNKNOWN"
    }
}