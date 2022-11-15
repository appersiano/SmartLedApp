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
import kotlinx.coroutines.launch

/**
 * SmartLedBleClient let you communicate with the Smart Led Device
 */
private const val TAG = "SmartLedBleClient"

class SmartLedBleClient(private val context: Context) {

    private var mBluetoothGatt: BluetoothGatt? = null
    private val bleManager =
        context.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
    private var bluetoothDevice: BluetoothDevice? = null
    private val localScopeStatus = CoroutineScope(Dispatchers.IO)

    //region Mutable State Flow
    private val _deviceConnectionStatus = MutableStateFlow<SDeviceStatus>(SDeviceStatus.UNKNOWN)
//    val deviceConnectionStatus = _deviceConnectionStatus.asStateFlow()
//    private val _deviceInfoState = MutableStateFlow<DeviceInfo?>(null)
//    val deviceInfoState = _deviceInfoState.asStateFlow()
    //endregion

    private lateinit var mMacAddress: String

    //region Basic Connect/Disconnect
    /**
     * Connect to the safety cushion by macAddress
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

    @SuppressLint("MissingPermission")
    fun readDeviceInfo(): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.DeviceInformation.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.DeviceInformation.FWRevisionString.uuid)

        characteristic?.let {
            return mBluetoothGatt?.readCharacteristic(characteristic)
        } ?: kotlin.run {
            return false
        }
    }

    //endregion Read

    //region Write
    @SuppressLint("MissingPermission")
    fun switchLED(value: ESwitch): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.HardwareControlService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.HardwareControlService.LEDControl.uuid)

        val payload = byteArrayOf(value.value.toByte())
        return sendCommand(characteristic, payload)
    }

    @SuppressLint("MissingPermission")
    fun switchPIR(value: ESwitch): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.LEDService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.HardwareControlService.PIRControl.uuid)

        val payload = byteArrayOf(value.value.toByte())
        return sendCommand(characteristic, payload)
    }

    fun setLEDColor(
        @IntRange(from = 0L, to = 255L) red: Long,
        @IntRange(from = 0L, to = 255L) green: Long,
        @IntRange(from = 0L, to = 255L) blue: Long
    ): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.LEDService.uuid)
        val characteristic = service?.getCharacteristic(SmartLedUUID.LEDService.LEDColor.uuid)

        val payload = byteArrayOf(red.toByte(), green.toByte(), blue.toByte())
        return sendCommand(characteristic, payload)
    }

    fun setLEDBrightness(@IntRange(from = 0L, to = 100L) brightness: Long): Boolean? {
        val service = mBluetoothGatt?.getService(SmartLedUUID.LEDService.uuid)
        val characteristic = service?.getCharacteristic(SmartLedUUID.LEDService.LEDBrightness.uuid)

        val payload = byteArrayOf(brightness.toByte())
        return sendCommand(characteristic, payload)
    }

    fun setCurrentTime(
        @IntRange(from = 0L, to = 23L) hour: Int,
        @IntRange(from = 0L, to = 59) minute: Int,
        @IntRange(from = 0L, to = 59L) second: Int
    ): Boolean? {

        val service = mBluetoothGatt?.getService(SmartLedUUID.HardwareControlService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.HardwareControlService.CurrentTime.uuid)

        val payload = byteArrayOf(hour.toByte(), minute.toByte(), second.toByte())
        return sendCommand(characteristic, payload)
    }

    fun setTimer(
        addOrRemove: Int,
        @IntRange(from = 0L, to = 23L) turnOnHour: Int,
        @IntRange(from = 0L, to = 59) turnOnMinute: Int,
        @IntRange(from = 0L, to = 59L) turnOnSecond: Int,
        @IntRange(from = 0L, to = 23L) turnOffHour: Int,
        @IntRange(from = 0L, to = 59) turnOffMinute: Int,
        @IntRange(from = 0L, to = 59L) turnOffSecond: Int,
    ): Boolean? {

        val service = mBluetoothGatt?.getService(SmartLedUUID.LEDService.uuid)
        val characteristic =
            service?.getCharacteristic(SmartLedUUID.HardwareControlService.PIRControl.uuid)

        val payload = byteArrayOf(
            addOrRemove.toByte(),
            turnOnHour.toByte(),
            turnOnMinute.toByte(),
            turnOnSecond.toByte(),
            turnOffHour.toByte(),
            turnOffMinute.toByte(),
            turnOffSecond.toByte(),
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