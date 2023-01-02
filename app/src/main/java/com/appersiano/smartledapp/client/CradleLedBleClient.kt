package com.appersiano.smartledapp.client

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * CradleLedBleClient let you communicate with the Smart Led Cradle Device
 */
private const val TAG = "CradleLedBleClient"

class CradleLedBleClient(private val context: Context) {

    private var mBluetoothGatt: BluetoothGatt? = null
    private val bleManager =
        context.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
    private var bluetoothDevice: BluetoothDevice? = null
    private val localScopeStatus = CoroutineScope(Dispatchers.IO)

    //region Mutable State Flow
    private val _deviceConnectionStatus = MutableStateFlow<SDeviceStatus>(SDeviceStatus.UNKNOWN)
    val deviceConnectionStatus = _deviceConnectionStatus as StateFlow<SDeviceStatus>
    //endregion

    private lateinit var mMacAddress: String

    //region Basic Connect/Disconnect
    /**
     * Connect to the Cradle Smart Light by macAddress
     *
     * @param macAddress the macAddress of the safety cushion
     */
    @SuppressLint("MissingPermission")
    fun connect(macAddress: String) {
        mMacAddress = macAddress
        if (!hasConnectPermission()) {
            return
        }

        bleManager.adapter.getRemoteDevice(macAddress)?.let {
            bluetoothDevice = it
            mBluetoothGatt = bluetoothDevice?.connectGatt(context, false, gattCallback)
        } ?: run {
            throw Exception("Bluetooth device not found, please scan first")
        }
    }

    /**
     * Disconnect from the safety cushion
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (!hasConnectPermission()) {
            return
        }
        mBluetoothGatt?.disconnect()
    }
    //endregion

    //region Write
    /**
     * Turn ON / OFF the LED
     *
     * @param value ESwitch.ON select on/off status
     */
    fun setLEDStatus(value: ESwitch): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.CradleSmartLightService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.CradleSmartLightService.LEDStatus.uuid)

        val payload = byteArrayOf(value.value.toByte())
        return sendCommand(characteristic, payload)
    }

    /**
     * Set PIR funcionality ON / OFF
     *
     * @param value use ESwitch ON/OFF
     */
    fun setPIRStatus(value: ESwitch): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.CradleSmartLightService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.CradleSmartLightService.PIRStatus.uuid)

        val payload = byteArrayOf(value.value.toByte())
        return sendCommand(characteristic, payload)
    }

    /**
     * Change LED Color by using RGB value
     *
     * @param red Red Color
     * @param green Green Color
     * @param blue Blue Color
     */
    fun setLEDColor(
        @IntRange(from = 0L, to = 255L) red: Long,
        @IntRange(from = 0L, to = 255L) green: Long,
        @IntRange(from = 0L, to = 255L) blue: Long
    ): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.CradleSmartLightService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.CradleSmartLightService.LEDColor.uuid)

        val payload = byteArrayOf(red.toByte(), green.toByte(), blue.toByte())
        return sendCommand(characteristic, payload)
    }

    /**
     * Set LED Brightness.
     *
     * @param brightness range 0-100
     */
    fun setLEDBrightness(@IntRange(from = 0L, to = 100L) brightness: Long): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.CradleSmartLightService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.CradleSmartLightService.LEDBrightness.uuid)

        val payload = byteArrayOf(brightness.toByte())
        return sendCommand(characteristic, payload)
    }

    /**
     * Set the current time to the device to perform automatica turn on/off
     *
     * @param hour Current hour of the day (0-23)
     * @param minute Current minute of the day (0-59)
     * @param second Current second of the day (0-59)
     * @param day Current day of the month (1-31)
     * @param month Current month of the year (1-12)
     * @param year Current year calculated on 2000 + x (0 <= x <= 255)
     */
    fun setCurrentTime(
        @IntRange(from = 0L, to = 23L) hour: Int,
        @IntRange(from = 0L, to = 59) minute: Int,
        @IntRange(from = 0L, to = 59L) second: Int,
        @IntRange(from = 1L, to = 31L) day: Int,
        @IntRange(from = 1L, to = 12L) month: Int,
        @IntRange(from = 0L, to = 255L) year: Int,
    ): Boolean? {

        val service = mBluetoothGatt?.getService(SmartLedUUID.CradleSmartLightService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.CradleSmartLightService.CurrentTime.uuid)

        val payload = byteArrayOf(hour.toByte(), minute.toByte(), second.toByte())
        return sendCommand(characteristic, payload)
    }

    /**
     * Set the timer to let the LED turn ON/OFF automatically
     *
     * @param timerFeatureStatus set status ON/OFF
     * @param switchONHour Set hour when the LED turn ON (0-23)
     * @param switchOnMinute Set minute of switchONHour when the LED turn ON (0-59)
     * @param switchOFFHour Set hour when the LED turn ON (0-23)
     * @param switchOFFMinute Set minute of switchOFFHour when the LED turn ON (0-59)
     */
    fun setTimerFeature(
        timerFeatureStatus: ESwitch,
        @IntRange(from = 0L, to = 23L) switchONHour: Int,
        @IntRange(from = 0L, to = 59) switchOnMinute: Int,
        @IntRange(from = 0L, to = 23L) switchOFFHour: Int,
        @IntRange(from = 0L, to = 59) switchOFFMinute: Int,
    ): Boolean? {

        val service = mBluetoothGatt?.getService(SmartLedUUID.CradleSmartLightService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.CradleSmartLightService.TimerFeature.uuid)

        val payload = byteArrayOf(
            timerFeatureStatus.value.toByte(),
            switchONHour.toByte(),
            switchOnMinute.toByte(),
            switchOFFHour.toByte(),
            switchOFFMinute.toByte(),
        )
        return sendCommand(characteristic, payload)
    }

    @SuppressLint("MissingPermission")
    private fun sendCommand(
        characteristic: BluetoothGattCharacteristic?,
        payload: ByteArray
    ): Boolean? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            characteristic?.let {
                val result = mBluetoothGatt?.writeCharacteristic(
                    characteristic,
                    payload,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )

                val res = when (result) {
                    BluetoothStatusCodes.SUCCESS -> true
                    else -> false
                }
                return res
            }
        } else {
            characteristic?.value = payload
            return mBluetoothGatt?.writeCharacteristic(characteristic)
        }
        return false
    }

    //endregion

    @SuppressLint("MissingPermission")
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
        }

        override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            localScopeStatus.launch {
                val state = when (newState) {
                    BluetoothGatt.STATE_CONNECTED -> {
                        if (!hasConnectPermission()) {
                            return@launch
                        }
                        Log.i(TAG, "Successful connect to device. Status: Success")

                        mBluetoothGatt?.discoverServices()
                        SDeviceStatus.CONNECTED
                    }
                    BluetoothGatt.STATE_DISCONNECTED -> {
                        mBluetoothGatt?.close()
                        SDeviceStatus.DISCONNECTED
                    }
                    else -> SDeviceStatus.UNKNOWN
                }

                _deviceConnectionStatus.emit(state)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i(TAG, "Successfully discovered services")
            Log.i(TAG, "onServicesDiscovered: " + gatt?.device?.address)
            localScopeStatus.launch {
                _deviceConnectionStatus.emit(SDeviceStatus.READY)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Log.d(TAG, "📖 onCharacteristicRead: ${characteristic?.uuid}")
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.i(TAG, "🖊 ️onCharacteristicWrite: ${characteristic?.uuid}")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.i(TAG, "onCharacteristicChanged: ${characteristic?.uuid}")
        }

        @SuppressLint("MissingPermission")
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.i(TAG, "onMtuChanged: $mtu")
            Log.i(TAG, "onMtuChanged: MTU = $mtu, status = $status")
        }
    }

    //region Sealed Classes
    sealed class SDeviceStatus {
        object UNKNOWN : SDeviceStatus() //initial state
        object CONNECTED : SDeviceStatus() //connected to the device
        object READY : SDeviceStatus() //ready to communicate, services discovered
        object DISCONNECTED : SDeviceStatus() //disconnected from the device
    }
    //endregion

    //region Enums
    enum class ESwitch(val value: Short) {
        OFF(0x0),
        ON(0x1)
    }
    //endregion

    //region Permissions
    /**
     * Check if the app has the necessary permissions to scan for iBeacon devices.
     */
    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.hasPermissions(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            true
        }
    }
    //endregion

    private fun Context.hasPermissions(vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}