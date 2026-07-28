package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class OrderHistoryUI(
    val billNumber: String,
    val timestamp: String,
    val totalAmount: String,
    val paymentMethod: String,
    val status: String
)

@Composable
fun HistoryScreen(
    onSelectBill: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Bills") }

    val bills = listOf(
        OrderHistoryUI("1094", "10:42 AM", "$24.50", "CASH", "PAID"),
        OrderHistoryUI("1093", "10:15 AM", "$12.00", "UPI QR", "PAID"),
        OrderHistoryUI("1092", "09:50 AM", "$48.80", "CARD", "PAID"),
        OrderHistoryUI("1091", "09:30 AM", "$15.50", "CASH", "PAID"),
        OrderHistoryUI("1090", "09:05 AM", "$32.00", "UPI QR", "PAID")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        OptixSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = "Search bill number or customer..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Bills", "Today", "Cash", "UPI").forEach { filter ->
                OptixChip(
                    text = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(bills) { bill ->
                OptixOrderCard(
                    orderId = bill.billNumber,
                    timestamp = bill.timestamp,
                    totalAmount = bill.totalAmount,
                    paymentMethod = bill.paymentMethod,
                    status = bill.status,
                    onViewReceipt = { onSelectBill(bill.billNumber) }
                )
            }
        }
    }
}
