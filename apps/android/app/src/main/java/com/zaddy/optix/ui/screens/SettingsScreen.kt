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

data class SettingTileItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun SettingsScreen() {
    val settingsTiles = listOf(
        SettingTileItem("Terminal Configuration", "Counter POS 1 • Multi-tenant Outlet #9022", Icons.Default.PointOfSale),
        SettingTileItem("ESC/POS Thermal Printers", "Network TCP 192.168.1.100 • Cash Drawer Solenoid", Icons.Default.Print),
        SettingTileItem("Payment Gateways & UPI", "Static UPI QR Code • PineLabs Card Terminal", Icons.Default.QrCodeScanner),
        SettingTileItem("Tax & Charge Rates", "GST 10.0% • Service Tax Excluded", Icons.Default.Receipt),
        SettingTileItem("Staff RBAC Permissions", "7 Active Roles • Security PIN Pad Overlay", Icons.Default.People),
        SettingTileItem("Cloud Sync & Outbox Queue", "WorkManager Background Push • LWW Conflict Resolver", Icons.Default.CloudSync),
        SettingTileItem("SaaS Subscription Status", "PRO POS Tier • 7-Day Offline Grace Active", Icons.Default.WorkspacePremium)
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
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(settingsTiles) { tile ->
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(OptixOrangeSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tile.icon,
                                    contentDescription = null,
                                    tint = OptixOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = tile.title,
                                    color = OptixTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = tile.subtitle,
                                    color = OptixTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = OptixTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
