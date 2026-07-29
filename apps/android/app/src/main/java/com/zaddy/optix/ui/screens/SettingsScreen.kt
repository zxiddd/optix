package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class SettingSectionRow(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun SettingsScreen() {
    val settingsRows = listOf(
        SettingSectionRow("Business Information", "Metro Cafe & Bakery • Outlet #9022", Icons.Default.Store),
        SettingSectionRow("Payment Accounts & Tender", "Static UPI QR Code • PineLabs Card Terminal", Icons.Default.QrCodeScanner),
        SettingSectionRow("Subscription & Licensing", "PRO POS Tier • 7-Day Offline Grace Active", Icons.Default.WorkspacePremium),
        SettingSectionRow("Receipt Header & Footer", "Thermal Paper Preview • Custom Business Logo", Icons.Default.ReceiptLong),
        SettingSectionRow("Staff & RBAC Permissions", "7 Active Staff Roles • Manager Security PIN", Icons.Default.People),
        SettingSectionRow("ESC/POS Thermal Printers", "Network TCP 192.168.1.100 • Cash Drawer Solenoid", Icons.Default.Print),
        SettingSectionRow("Cloud Sync & Outbox Queue", "WorkManager Background Push • LWW Conflict Resolver", Icons.Default.CloudSync),
        SettingSectionRow("Offline Mode & Storage", "Room Offline Database • Auto-Sync Catchup", Icons.Default.SignalCellularOff),
        SettingSectionRow("Notifications & Alerts", "Low Inventory Stock Warnings • Shift Summary Push", Icons.Default.Notifications),
        SettingSectionRow("Security & Audit Logs", "Biometric Lock • Terminal Audit Tracking", Icons.Default.Security),
        SettingSectionRow("Database Backup & Restore", "Export SQLite Room Database • Cloud Snapshot", Icons.Default.Backup),
        SettingSectionRow("About Optix POS", "Version v1.0.0 (Build 9022) • Legal & Support", Icons.Default.Info)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "System Settings",
            color = OptixTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp
        )
        Text(
            text = "Configure terminal hardware, printers, tax & security",
            color = OptixTextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(settingsRows) { row ->
                OptixCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(OptixOrangeSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = row.icon,
                                    contentDescription = null,
                                    tint = OptixOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = row.title,
                                    color = OptixTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = row.subtitle,
                                    color = OptixTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Navigate",
                            tint = OptixTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
