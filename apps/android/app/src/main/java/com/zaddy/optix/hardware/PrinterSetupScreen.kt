package com.zaddy.optix.hardware

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class DiscoveredPrinterUI(
    val name: String,
    val macAddress: String,
    val type: String,
    val signalStrength: String
)

@Composable
fun PrinterSetupScreen(
    onBack: () -> Unit = {}
) {
    var isConnected by remember { mutableStateOf(true) }
    var selectedPaperSize by remember { mutableStateOf("80mm") }
    var autoReconnect by remember { mutableStateOf(true) }
    var autoPulseCashDrawer by remember { mutableStateOf(true) }
    var isScanning by remember { mutableStateOf(false) }

    val discoveredPrinters = remember {
        listOf(
            DiscoveredPrinterUI("Sunmi Embedded ESC/POS", "00:11:22:33:44:55", "Internal Serial", "100% (Direct)"),
            DiscoveredPrinterUI("Epson TM-T88VI Thermal", "192.168.1.100:9100", "Network TCP/IP", "94% (Strong)"),
            DiscoveredPrinterUI("Rongta RPP02N Mobile", "AA:BB:CC:DD:EE:FF", "Bluetooth LE", "78% (Good)")
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Surface(
                color = OptixSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OptixTextPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ESC/POS Printer Management", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        },
        containerColor = OptixDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Connected Active Printer Status Card
            OptixCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isConnected) OptixSuccessGreen else OptixErrorRed
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnected) OptixSuccessGreen else OptixErrorRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isConnected) "CONNECTED & READY" else "DISCONNECTED",
                                color = if (isConnected) OptixSuccessGreen else OptixErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Battery Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = "Battery", tint = OptixSuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("85%", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Epson TM-T88VI ESC/POS", color = OptixTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("TCP/IP 192.168.1.100:9100 • Signal 94% Strong", color = OptixTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Print Test Receipt Button
                        OptixButton(
                            text = "TEST PRINT",
                            onClick = { },
                            icon = Icons.Default.Print,
                            modifier = Modifier.weight(1f)
                        )

                        // Cash Drawer Solenoid Pulse Test Button
                        OptixButton(
                            text = "OPEN DRAWER",
                            onClick = { },
                            icon = Icons.Default.MeetingRoom,
                            isSecondary = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hardware Settings & Paper Size Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Hardware Configuration", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Paper Size Chips: 58mm vs 80mm
                    Text("Thermal Paper Roll Width", color = OptixTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("58mm", "80mm").forEach { size ->
                            OptixChip(
                                text = size,
                                isSelected = selectedPaperSize == size,
                                onClick = { selectedPaperSize = size },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto Reconnect Toggle Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto Reconnect Hardware", color = OptixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Automatically reconnect on signal drop", color = OptixTextSecondary, fontSize = 11.sp)
                        }
                        Switch(checked = autoReconnect, onCheckedChange = { autoReconnect = it }, colors = SwitchDefaults.colors(checkedTrackColor = OptixOrange))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cash Drawer Auto Pulse Toggle Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto Solenoid Pulse (RJ11)", color = OptixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Pulse cash drawer on cash checkout (0x1b 0x70)", color = OptixTextSecondary, fontSize = 11.sp)
                        }
                        Switch(checked = autoPulseCashDrawer, onCheckedChange = { autoPulseCashDrawer = it }, colors = SwitchDefaults.colors(checkedTrackColor = OptixOrange))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Discovered Hardware / Bluetooth Scanner Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Discovered Printers", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = if (isScanning) "SCANNING..." else "SCAN AGAIN",
                            color = OptixOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { isScanning = !isScanning }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    discoveredPrinters.forEach { printer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(OptixSurface)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(printer.name, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${printer.type} • ${printer.macAddress}", color = OptixTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("Signal: ${printer.signalStrength}", color = OptixSuccessGreen, fontSize = 11.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(OptixOrange)
                                    .clickable { isConnected = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("CONNECT", color = OptixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
