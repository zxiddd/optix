package com.optix.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.optix.pos.auth.DeviceActivationScreen
import com.optix.pos.auth.PinPadOverlay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OptixMainAppNavigation()
                }
            }
        }
    }
}

@Composable
fun OptixMainAppNavigation() {
    var isDeviceActivated by remember { mutableStateOf(false) }
    var isStaffAuthenticated by remember { mutableStateOf(false) }

    when {
        !isDeviceActivated -> {
            DeviceActivationScreen(
                onDeviceActivated = { isDeviceActivated = true }
            )
        }
        !isStaffAuthenticated -> {
            PinPadOverlay(
                onPinSuccess = { isStaffAuthenticated = true },
                onDismiss = { }
            )
        }
        else -> {
            OptixRegisterMainScreen(
                onLockTerminal = { isStaffAuthenticated = false }
            )
        }
    }
}

@Composable
fun OptixRegisterMainScreen(onLockTerminal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Optix POS Register Active",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Tenant: Metro Bakery & Cafe | User: Cashier John")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLockTerminal) {
            Text(text = "LOCK TERMINAL (PIN OVERLAY)")
        }
    }
}
