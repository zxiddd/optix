package com.zaddy.optix.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryItem(val id: String, val name: String, val colorHex: String = "#007AFF")
data class ProductItem(
    val id: String,
    val title: String,
    val price: Double,
    val pricingStrategy: String = "FIXED",
    val stock: Double = 20.0,
    val categoryId: String? = null
)

@Composable
fun ProductCatalogScreen(
    onProductSelected: (ProductItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("ALL") }

    val categories = remember {
        listOf(
            CategoryItem("ALL", "All Items"),
            CategoryItem("PASTRY", "Pastry & Cakes"),
            CategoryItem("BREAD", "Artisan Bread"),
            CategoryItem("COFFEE", "Espresso & Coffee")
        )
    }

    val sampleProducts = remember {
        listOf(
            ProductItem("p1", "Butter Croissant", 4.50, "FIXED", 42.0, "PASTRY"),
            ProductItem("p2", "Sourdough Loaf", 6.50, "FIXED", 18.0, "BREAD"),
            ProductItem("p3", "Iced Vanilla Latte", 5.00, "FIXED", 99.0, "COFFEE"),
            ProductItem("p4", "Almond Croissant", 5.20, "FIXED", 14.0, "PASTRY"),
            ProductItem("p5", "Baguette Traditional", 3.80, "FIXED", 25.0, "BREAD"),
            ProductItem("p6", "Bulk Coffee Beans", 18.00, "WEIGHT", 50.0, "COFFEE")
        )
    }

    val filteredProducts = sampleProducts.filter { product ->
        val matchesCategory = selectedCategoryId == "ALL" || product.categoryId == selectedCategoryId
        val matchesSearch = searchQuery.isBlank() || product.title.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name, SKU or scan barcode...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Tab Row
        ScrollableTabRow(
            selectedTabIndex = categories.indexOfFirst { it.id == selectedCategoryId }.coerceAtLeast(0),
            edgePadding = 0.dp
        ) {
            categories.forEach { category ->
                Tab(
                    selected = selectedCategoryId == category.id,
                    onClick = { selectedCategoryId = category.id },
                    text = { Text(category.name, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Responsive Product Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                ProductGridTile(product = product, onClick = { onProductSelected(product) })
            }
        }
    }
}

@Composable
fun ProductGridTile(product: ProductItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                Text(
                    text = "Stock: ${product.stock.toInt()} units",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (product.pricingStrategy == "WEIGHT") {
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text("WEIGHT", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
