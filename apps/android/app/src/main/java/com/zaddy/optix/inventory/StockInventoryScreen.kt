package com.zaddy.optix.inventory

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

data class StockInventoryUI(
    val id: String,
    val title: String,
    val sku: String,
    val category: String,
    var quantity: Int,
    val minThreshold: Int,
    val unitCost: String,
    val supplier: String
)

@Composable
fun StockInventoryScreen(
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val stockItems = remember {
        mutableStateListOf(
            StockInventoryUI("1", "Organic Espresso Coffee Beans (1kg)", "SKU-BEANS-90", "Beverages", 4, 10, "$18.50", "Supplier: Golden Grain Wholesalers"),
            StockInventoryUI("2", "French Croissant Butter Batch", "SKU-BUTTER-12", "Bakery", 42, 15, "$2.10", "Supplier: Metro Bakery Supplies"),
            StockInventoryUI("3", "Oat Milk Carton 1L", "SKU-OAT-44", "Beverages", 8, 12, "$3.20", "Supplier: Dairy Pure Distributors"),
            StockInventoryUI("4", "Avocado Fresh Case", "SKU-AVO-09", "Meals", 3, 8, "$24.00", "Supplier: Organic Farms Direct"),
            StockInventoryUI("5", "Blueberry Muffin Premix 5kg", "SKU-MUFF-01", "Bakery", 25, 5, "$14.00", "Supplier: Metro Bakery Supplies")
        )
    }

    val filteredItems = stockItems.filter { item ->
        (selectedCategory == "All" || item.category == selectedCategory) &&
        (searchQuery.isEmpty() || item.title.contains(searchQuery, ignoreCase = true) || item.sku.contains(searchQuery, ignoreCase = true))
    }

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
                        Text("Inventory & Stock Control", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OptixOrange)
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = OptixTextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ADD ITEM", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            OptixSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Search item title, SKU or barcode..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Bakery", "Beverages", "Meals").forEach { cat ->
                    OptixChip(
                        text = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stock Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { stock ->
                    val isLowStock = stock.quantity <= stock.minThreshold

                    OptixCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (isLowStock) OptixOrange else OptixCardBorder
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
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (isLowStock) OptixOrangeSubtle else OptixSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isLowStock) Icons.Default.Warning else Icons.Default.Inventory2,
                                            contentDescription = null,
                                            tint = if (isLowStock) OptixOrange else OptixTextPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(stock.title, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${stock.sku} • ${stock.supplier}", color = OptixTextSecondary, fontSize = 11.sp)
                                    }
                                }

                                // Orange Low Stock Warning Badge
                                if (isLowStock) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(OptixOrange.copy(alpha = 0.2f))
                                            .border(1.dp, OptixOrange, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("LOW STOCK: ${stock.quantity}", color = OptixOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Unit Cost Price", color = OptixTextSecondary, fontSize = 11.sp)
                                    Text(stock.unitCost, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Current Quantity", color = OptixTextSecondary, fontSize = 11.sp)
                                    Text(
                                        text = "${stock.quantity} units",
                                        color = if (isLowStock) OptixOrange else OptixSuccessGreen,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Buttons: Adjust Stock & History
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(OptixOrange)
                                        .clickable { stock.quantity += 10 }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("ADJUST STOCK", color = OptixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(OptixSurface)
                                        .border(1.dp, OptixCardBorder, RoundedCornerShape(10.dp))
                                        .clickable { }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("STOCK HISTORY", color = OptixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
