package com.zaddy.optix.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

@Composable
fun StatsAnalyticsScreen() {
    var selectedTimeframe by remember { mutableStateOf("Weekly") }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoaded = true
    }

    val scrollState = rememberScrollState()

    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(400))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OptixDarkBackground)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Executive Analytics",
                    color = OptixTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )

                // Timeframe Filters: Weekly, Monthly, Yearly
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Weekly", "Monthly", "Yearly").forEach { period ->
                        OptixChip(
                            text = period,
                            isSelected = selectedTimeframe == period,
                            onClick = { selectedTimeframe = period }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Revenue Highlight Card
            OptixStatCard(
                title = "TOTAL REVENUE ($selectedTimeframe)",
                value = "$14,825.00",
                subtitle = "+24.8% growth vs last period",
                icon = Icons.Default.AttachMoney,
                isHighlight = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of 4 Key Financial Metrics: Orders, Avg Order, Net Profit, Expenses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OptixStatCard(
                    title = "Total Orders",
                    value = "482",
                    subtitle = "100% completed",
                    icon = Icons.Default.ReceiptLong,
                    modifier = Modifier.weight(1f)
                )
                OptixStatCard(
                    title = "Average Order",
                    value = "$30.75",
                    subtitle = "Per bill average",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OptixStatCard(
                    title = "Estimated Net Profit",
                    value = "$9,842.00",
                    subtitle = "66.4% margin",
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f)
                )
                OptixStatCard(
                    title = "Operating Expenses",
                    value = "$2,100.00",
                    subtitle = "Rent, Power, Staff",
                    icon = Icons.Default.MoneyOff,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Electric Orange Custom Canvas Chart Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sales Velocity Chart", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Revenue Trend ($)", color = OptixOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Orange Bar Chart Rendered on Compose Canvas
                    val barHeights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.85f, 1.0f)
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            val barWidth = width / (barHeights.size * 2)

                            barHeights.forEachIndexed { index, ratio ->
                                val barHeight = height * ratio
                                val x = (index * 2 + 0.5f) * barWidth

                                drawRoundRect(
                                    color = OptixOrange,
                                    topLeft = Offset(x, height - barHeight),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8.dp.toPx())
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEach { day ->
                            Text(text = day, color = OptixTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Top Products Ranking List
            Text(
                text = "Top Ranking Products",
                color = OptixTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            val topItems = listOf(
                Triple("Classic Butter Croissant", "642 units sold", "$2,889.00"),
                Triple("Espresso Cappuccino Large", "480 units sold", "$2,400.00"),
                Triple("Iced Vanilla Latte", "320 units sold", "$1,760.00"),
                Triple("Blueberry Muffin Batch", "280 units sold", "$980.00"),
                Triple("Avocado Toast Special", "150 units sold", "$1,275.00")
            )

            topItems.forEach { (name, qty, total) ->
                OptixCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
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
                                Icon(Icons.Default.Star, contentDescription = null, tint = OptixOrange, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = name, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = qty, color = OptixTextSecondary, fontSize = 12.sp)
                            }
                        }
                        Text(text = total, color = OptixOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
