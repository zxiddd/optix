package com.zaddy.optix

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
import com.zaddy.optix.auth.DeviceActivationScreen
import com.zaddy.optix.auth.PinPadOverlay
import com.zaddy.optix.catalog.ProductCatalogScreen

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
        modifier = Modifier.fillMaxSize()
    ) {
        // POS Header Bar
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Metro Bakery & Cafe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Terminal: Counter POS 1 | Cashier: John",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = onLockTerminal,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("LOCK TERMINAL")
                }
            }
        }

        // Main Catalog Screen Layout
        ProductCatalogScreen()
    }
}
