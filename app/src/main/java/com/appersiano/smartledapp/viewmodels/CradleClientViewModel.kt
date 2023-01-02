package com.appersiano.smartledapp.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.appersiano.smartledapp.client.CradleLedBleClient

/**
 * PadClientViewModel is in charge to manage the communication with the safety cushion
 */
private const val TAG = "CradleClientViewModel"

class CradleClientViewModel(application: Application) : AndroidViewModel(application) {

    private var appPath: String? = null
    private var stackPath: String? = null

    private var otaApplicationFileURI: Uri? = null
    private var otaAppLoaderFileURI: Uri? = null


    private val padClient by lazy { CradleLedBleClient(application) }

    //region MutableStateFlow

    //endregion

    fun connect(macAddress: String) {
        padClient.connect(macAddress)
    }

    fun disconnect() {
        padClient.disconnect()
    }

    fun writeKey(value: String) {

    }

    fun enableOnBoardNotification(value: Boolean) {

    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared: ")
    }

}
