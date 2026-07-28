package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

@Composable
fun StatsAnalyticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Business Performance",
            color = OptixTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // High contrast Orange Total Revenue Card
        OptixStatCard(
            title = "TOTAL REVENUE TODAY",
            value = "$1,482.50",
            subtitle = "+18.4% vs yesterday",
            icon = Icons.Default.AttachMoney,
            isHighlight = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptixStatCard(
                title = "Total Orders",
                value = "48",
                subtitle = "Avg $30.88/bill",
                icon = Icons.Default.ReceiptLong,
                modifier = Modifier.weight(1f)
            )
            OptixStatCard(
                title = "Net GST Tax",
                value = "$148.25",
                subtitle = "State Audit Ready",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Top Selling Items Today",
            color = OptixTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val topItems = listOf(
            Triple("Classic Butter Croissant", "64 units", "$288.00"),
            Triple("Espresso Cappuccino Large", "48 units", "$240.00"),
            Triple("Iced Vanilla Latte", "32 units", "$176.00"),
            Triple("Blueberry Muffin Batch", "28 units", "$98.00")
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topItems) { (name, qty, total) ->
                OptixCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = name, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = qty, color = OptixTextSecondary, fontSize = 12.sp)
                        }
                        Text(text = total, color = OptixOrange, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
