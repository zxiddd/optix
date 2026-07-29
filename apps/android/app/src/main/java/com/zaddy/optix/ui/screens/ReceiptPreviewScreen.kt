package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
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

@Composable
fun ReceiptPreviewScreen(
    billNumber: String = "1094",
    onPrint: () -> Unit = {}
) {
    var selectedWidth by remember { mutableStateOf("80mm (Standard)") }
    var selectedCopyType by remember { mutableStateOf("Customer Copy") }
    var showLogo by remember { mutableStateOf(true) }
    var showQrCode by remember { mutableStateOf(true) }
    var isDuplicate by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ESC/POS Thermal Receipt Preview",
            color = OptixTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Configuration Control Panel Card
        OptixCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("Print Configuration", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Paper Width Selector Chips
                Text("Thermal Roll Width", color = OptixTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("80mm (Standard)", "58mm (Mobile)").forEach { width ->
                        OptixChip(
                            text = width,
                            isSelected = selectedWidth == width,
                            onClick = { selectedWidth = width },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Copy Type Selector Chips
                Text("Receipt Copy Type", color = OptixTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Customer Copy", "Merchant Copy").forEach { copy ->
                        OptixChip(
                            text = copy,
                            isSelected = selectedCopyType == copy,
                            onClick = { selectedCopyType = copy },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle Switches
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Print Business Logo Header", color = OptixTextPrimary, fontSize = 13.sp)
                    Switch(checked = showLogo, onCheckedChange = { showLogo = it }, colors = SwitchDefaults.colors(checkedTrackColor = OptixOrange))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Include Dynamic UPI QR Badge", color = OptixTextPrimary, fontSize = 13.sp)
                    Switch(checked = showQrCode, onCheckedChange = { showQrCode = it }, colors = SwitchDefaults.colors(checkedTrackColor = OptixOrange))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Mark as Duplicate Receipt", color = OptixTextPrimary, fontSize = 13.sp)
                    Switch(checked = isDuplicate, onCheckedChange = { isDuplicate = it }, colors = SwitchDefaults.colors(checkedTrackColor = OptixOrange))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Actual Physical Thermal Receipt Canvas
        Box(
            modifier = Modifier
                .width(if (selectedWidth.contains("58mm")) 270.dp else 330.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFAFAFA))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isDuplicate) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("*** DUPLICATE COPY ***", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (showLogo) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("O", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text("METRO BAKERY & CAFE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Text("123 Main Street, Suite 400", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("GSTIN: 22AAAAA0000A1Z5", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("Ph: +1 555 019 2831", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

                Text("Bill #: $billNumber  Token: #042", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("Date: 2026-07-29  10:42 AM", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("Type: ${selectedCopyType.uppercase()}", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

                // Itemized Breakdown
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("2x Croissant", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("$9.00", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("1x Cappuccino", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("$5.00", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("3x Muffin", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("$10.50", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }

                Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = Color.DarkGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("$22.27", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GST Tax (10%)", color = Color.DarkGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("$2.23", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GRAND TOTAL", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text("$24.50", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                }

                Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

                if (showQrCode) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = "UPI QR", tint = Color.White, modifier = Modifier.size(54.dp))
                    }
                    Text("Scan to pay via UPI QR", color = Color.DarkGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text("Thank you for visiting Metro Cafe!", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("Visit www.metrocafe.com", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary Orange Action Button
        OptixButton(
            text = "PRINT THERMAL RECEIPT (ESC/POS)",
            onClick = onPrint,
            icon = Icons.Default.Print,
            modifier = Modifier.width(330.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
