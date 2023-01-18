package com.appersiano.smartledapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appersiano.smartledapp.ui.theme.SmartLedAppTheme
import com.appersiano.smartledapp.viewmodels.CradleClientViewModel
import com.appersiano.smartledapp.viewmodels.ScannerViewModel
import com.appersiano.smartledapp.views.DetailScreen
import com.appersiano.smartledapp.views.MainScreen
import com.appersiano.smartledapp.views.RemoteControlScreen

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
                        startDestination = "test",
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
                                val bleClient: CradleClientViewModel = viewModel()
                                DetailScreen(
                                    macAddress = macAddress,
                                    onConnect = {
                                        bleClient.connect(macAddress)
                                        scannerViewModel.stopScan()
                                    },
                                    onDisconnect = {
                                        bleClient.disconnect()
                                    },
                                    status = bleClient.bleDeviceStatus.collectAsState().value,
                                    viewModel = bleClient
                                )
                            }
                        }

                        composable(route = "test") {
                            Surface(
                                color = Color(0xFF1E1E1E),
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                RemoteControlScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Boolean.toInt() = if (this) 1 else 0
