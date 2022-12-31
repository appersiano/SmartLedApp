package com.appersiano.smartledapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appersiano.smartledapp.ui.theme.SmartLedAppTheme
import com.appersiano.smartledapp.viewmodels.CradleClientViewModel
import com.appersiano.smartledapp.viewmodels.ScannerViewModel
import com.appersiano.smartledapp.views.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartLedAppTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val scannerViewModel: ScannerViewModel = viewModel()

                    val scanStatus = scannerViewModel.scanStatus.collectAsState()
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                    ) {
                        composable(route = "main") {
                            MainScreen(
                                navController,
                                onStartScan = {
                                    scannerViewModel.startScan(20, 5)
                                },
                                onStopScan = {
                                    scannerViewModel.stopScan()
                                },
                                scanStatus,
                                scannerViewModel.listDevices
                            )
                        }

                        composable(route = "detail/{macAddress}") { backStackEntry ->
                            val macAddress = backStackEntry.arguments?.getString("macAddress")
                            macAddress?.let {
//                                val padViewModel: CradleClientViewModel = viewModel()
//
//                                val padStatus = padViewModel.padDevicesStatus.collectAsState()
//                                val onBoardStatus = padViewModel.onBoardStatus.collectAsState()
//                                val otaStatusPad = padViewModel.otaStatus.collectAsState()
//                                val applicationFileName =
//                                    padViewModel.applicationFileName.collectAsState()
//                                val appLoaderFileName =
//                                    padViewModel.appLoaderFileName.collectAsState()
//                                val fwVersion = padViewModel.fwVersion.collectAsState()
//
//                                val resultApplication = remember { mutableStateOf<Uri?>(null) }
//                                val resultChooseFileApplication =
//                                    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//                                        padViewModel.setApplicationFileURI(
//                                            contentResolver,
//                                            cacheDir,
//                                            uri
//                                        )
//                                        resultApplication.value = uri
//                                    }
//
//                                val resultApploader = remember { mutableStateOf<Uri?>(null) }
//                                val resultChooseFileAppLoader =
//                                    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//                                        padViewModel.setApploaderFileURI(
//                                            contentResolver,
//                                            cacheDir,
//                                            uri
//                                        )
//                                        resultApploader.value = uri
//                                    }
//
//                                val _onClickSelectFile = OnClickApplication(
//                                    onClickSelectApplicationFile = {
//                                        resultChooseFileApplication.launch("*/*")
//                                    },
//                                    onClickSelectApploaderFile = {
//                                        resultChooseFileAppLoader.launch("*/*")
//                                    }
//                                )
//
//                                val onClickSelectFile =
//                                    remember { mutableStateOf(_onClickSelectFile) }
//
//
//                                DetailScreen(
//                                    macAddress,
//                                    onConnect = {
//                                        padViewModel.connect(macAddress)
//                                        scannerViewModel.stopScan()
//                                    },
//                                    onDisconnect = {
//                                        padViewModel.disconnect()
//                                    },
//                                    onWriteKey = {
//                                        padViewModel.writeKey("ChhR")
//                                    },
//                                    onEnableNotification = {
//                                        padViewModel.enableOnBoardNotification(true)
//                                    },
//                                    onDisableNotification = {
//                                        padViewModel.enableOnBoardNotification(false)
//                                    },
//                                    onBoardStatus = onBoardStatus.value,
//                                    status = padStatus.value,
//                                    onClickSelectFile = onClickSelectFile.value,
//                                    onStartOTA = {
//                                        padViewModel.startOTA()
//                                    },
//                                    applicationFileName = applicationFileName.value,
//                                    appLoaderFileName = appLoaderFileName.value,
//                                    otaStatus = otaStatusPad.value,
//                                    onReadFwVersion = {
//                                        padViewModel.readFwVersion()
//                                    },
//                                    currentFirmwareVersion = fwVersion.value
//                                )
                            }
                        }
                    }
                }
            }
        }
    }
}