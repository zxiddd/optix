package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode
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

data class PaymentAccountItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isActive: Boolean
)

@Composable
fun PaymentAccountsScreen() {
    val accounts = listOf(
        PaymentAccountItem("Cash Register Drawer", "Auto Solenoid Pulse (RJ11 0x1b 0x70)", Icons.Default.Money, true),
        PaymentAccountItem("Static UPI QR Code", "merchant@upi • Dynamic Amount QR Engine", Icons.Default.QrCode, true),
        PaymentAccountItem("PineLabs POS Terminal", "Bluetooth / IP Terminal Integration", Icons.Default.CreditCard, true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Payment Tender Accounts",
            color = OptixTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(accounts) { acc ->
                OptixCard(modifier = Modifier.fillMaxWidth()) {
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
                                    imageVector = acc.icon,
                                    contentDescription = null,
                                    tint = OptixOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = acc.title,
                                    color = OptixTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = acc.subtitle,
                                    color = OptixTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = acc.isActive,
                            onCheckedChange = { },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OptixTextPrimary,
                                checkedTrackColor = OptixOrange
                            )
                        )
                    }
                }
            }
        }
    }
}
