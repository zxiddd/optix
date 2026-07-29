package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class DetailedOrderHistoryUI(
    val billNumber: String,
    val tokenNumber: String,
    val timestamp: String,
    val cashierName: String,
    val customerName: String,
    val itemsSummary: String,
    val totalAmount: String,
    val paymentMethod: String,
    val status: String
)

@Composable
fun HistoryScreen(
    onSelectBill: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf("Today") }
    var selectedPaymentFilter by remember { mutableStateOf("All Tenders") }

    val bills = remember {
        listOf(
            DetailedOrderHistoryUI("1094", "Token #042", "10:42 AM", "Cashier: John", "Customer: Walk-in", "3 items: Croissant, Cappuccino, Muffin", "$24.50", "CASH", "PAID"),
            DetailedOrderHistoryUI("1093", "Token #041", "10:15 AM", "Cashier: John", "Customer: Alex Smith", "1 item: Espresso Cappuccino", "$12.00", "UPI QR", "PAID"),
            DetailedOrderHistoryUI("1092", "Token #040", "09:50 AM", "Cashier: Sarah", "Customer: Corporate Order", "5 items: Avocado Toast, Lattes", "$48.80", "CARD", "PAID"),
            DetailedOrderHistoryUI("1091", "Token #039", "09:30 AM", "Cashier: Sarah", "Customer: Walk-in", "2 items: Blueberry Muffin", "$15.50", "CASH", "PAID"),
            DetailedOrderHistoryUI("1090", "Token #038", "09:05 AM", "Cashier: John", "Customer: David Miller", "4 items: Croissants, Iced Lattes", "$32.00", "UPI QR", "PAID")
        )
    }

    val filteredBills = bills.filter { bill ->
        (selectedPaymentFilter == "All Tenders" || bill.paymentMethod.contains(selectedPaymentFilter, ignoreCase = true)) &&
        (searchQuery.isEmpty() || bill.billNumber.contains(searchQuery) || bill.customerName.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Order Register History",
            color = OptixTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OptixSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = "Search bill #, token, or customer name..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Today", "Yesterday", "This Week", "Custom Date").forEach { dateFilter ->
                OptixChip(
                    text = dateFilter,
                    isSelected = selectedDateFilter == dateFilter,
                    onClick = { selectedDateFilter = dateFilter }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Payment Method Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Tenders", "CASH", "UPI", "CARD").forEach { payFilter ->
                OptixChip(
                    text = payFilter,
                    isSelected = selectedPaymentFilter == payFilter,
                    onClick = { selectedPaymentFilter = payFilter }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Detailed Order Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredBills) { bill ->
                OptixCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Top Header: Bill #, Token Badge, Payment Method
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(OptixOrangeSubtle),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = null,
                                        tint = OptixOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Bill #${bill.billNumber}",
                                        color = OptixTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${bill.timestamp} • ${bill.cashierName}",
                                        color = OptixTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Token Number Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(OptixOrangeSubtle)
                                    .border(1.dp, OptixOrange, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = bill.tokenNumber,
                                    color = OptixOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Customer & Items Summary
                        Text(
                            text = bill.customerName,
                            color = OptixTextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Text(
                            text = bill.itemsSummary,
                            color = OptixTextSecondary,
                            fontSize = 12.sp
                        )

                        Divider(color = OptixCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                        // Bottom Row: Total Amount & Quick Action Buttons (View, Print, Refund)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Paid (${bill.paymentMethod})", color = OptixTextSecondary, fontSize = 11.sp)
                                Text(
                                    text = bill.totalAmount,
                                    color = OptixOrange,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // View Receipt Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OptixSurface)
                                        .border(1.dp, OptixCardBorder, RoundedCornerShape(12.dp))
                                        .clickable { onSelectBill(bill.billNumber) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Visibility, contentDescription = "View", tint = OptixTextPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("View", color = OptixTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Print Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OptixOrange)
                                        .clickable { onSelectBill(bill.billNumber) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Print, contentDescription = "Print", tint = OptixTextPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Print", color = OptixTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Refund Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OptixErrorRed.copy(alpha = 0.2f))
                                        .border(1.dp, OptixErrorRed, RoundedCornerShape(12.dp))
                                        .clickable { }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text("Refund", color = OptixErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
