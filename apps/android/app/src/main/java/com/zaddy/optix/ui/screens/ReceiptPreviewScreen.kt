package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Thermal Receipt Canvas (Simulated Paper)
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("METRO BAKERY & CAFE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                    Text("123 Main Street, Suite 400", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("GSTIN: 22AAAAA0000A1Z5", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("Bill #: $billNumber  Date: 2026-07-29", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("Time: 10:42:15 AM  Cashier: John", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

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
                        Text("GST (10%)", color = Color.DarkGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Text("$2.23", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GRAND TOTAL", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text("$24.50", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    }
                    Text("--------------------------------", color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("Tender: CASH ($24.50)", color = Color.Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Thank you for dining with us!", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OptixButton(
                text = "PRINT RECEIPT (ESC/POS)",
                onClick = onPrint,
                icon = Icons.Default.Print,
                modifier = Modifier.width(320.dp)
            )
        }
    }
}
