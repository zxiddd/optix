package com.zaddy.optix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.zaddy.optix.auth.BusinessSetupScreen
import com.zaddy.optix.auth.DeviceActivationScreen
import com.zaddy.optix.auth.PinPadOverlay
import com.zaddy.optix.auth.SubscriptionPlanScreen
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.screens.*
import com.zaddy.optix.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OptixTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = OptixDarkBackground
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    Crossfade(
                        targetState = showSplash,
                        animationSpec = tween(durationMillis = 500),
                        label = "splashFade"
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        } else {
                            OptixMainAppNavigation()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptixMainAppNavigation() {
    var isDeviceActivated by remember { mutableStateOf(true) }
    var isBusinessConfigured by remember { mutableStateOf(true) }
    var isSubscriptionSelected by remember { mutableStateOf(true) }
    var isStaffAuthenticated by remember { mutableStateOf(true) }

    when {
        !isDeviceActivated -> {
            DeviceActivationScreen(
                onDeviceActivated = {
                    isDeviceActivated = true
                    isBusinessConfigured = false
                }
            )
        }
        !isBusinessConfigured -> {
            BusinessSetupScreen(
                onSetupComplete = {
                    isBusinessConfigured = true
                    isSubscriptionSelected = false
                }
            )
        }
        !isSubscriptionSelected -> {
            SubscriptionPlanScreen(
                onPlanSelected = {
                    isSubscriptionSelected = true
                }
            )
        }
        !isStaffAuthenticated -> {
            PinPadOverlay(
                onPinSuccess = { isStaffAuthenticated = true },
                onDismiss = { }
            )
        }
        else -> {
            OptixMainRegisterShell(
                onLockTerminal = { isStaffAuthenticated = false }
            )
        }
    }
}

@Composable
fun OptixMainRegisterShell(onLockTerminal: () -> Unit) {
    var selectedTab by remember { mutableStateOf(OptixNavTab.BILLING) }

    Scaffold(
        topBar = {
            OptixTopAppBar(
                terminalName = "Counter POS 1",
                cashierName = "John",
                onLockTerminal = onLockTerminal
            )
        },
        bottomBar = {
            OptixBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = OptixDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                OptixNavTab.BILLING -> BillingScreen()
                OptixNavTab.HISTORY -> HistoryScreen()
                OptixNavTab.MENU -> MenuCatalogScreen()
                OptixNavTab.STATS -> StatsAnalyticsScreen()
                OptixNavTab.SETTINGS -> SettingsScreen()
            }
        }
    }
}
