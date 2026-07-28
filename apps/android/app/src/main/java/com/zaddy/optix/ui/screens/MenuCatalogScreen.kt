package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class ProductCatalogItemUI(
    val id: String,
    val title: String,
    val price: String,
    val stockCount: Int,
    val category: String
)

@Composable
fun MenuCatalogScreen(
    onAddToCart: (ProductCatalogItemUI) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val products = listOf(
        ProductCatalogItemUI("1", "Butter Croissant", "$4.50", 42, "Bakery"),
        ProductCatalogItemUI("2", "Cappuccino Large", "$5.00", 100, "Beverages"),
        ProductCatalogItemUI("3", "Blueberry Muffin", "$3.50", 25, "Bakery"),
        ProductCatalogItemUI("4", "Iced Vanilla Latte", "$5.50", 80, "Beverages"),
        ProductCatalogItemUI("5", "Avocado Toast", "$8.50", 15, "Meals"),
        ProductCatalogItemUI("6", "Chocolate Lava Cake", "$6.00", 20, "Desserts")
    )

    val filteredProducts = products.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        OptixSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = "Search items by name or SKU..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Bakery", "Beverages", "Meals", "Desserts").forEach { category ->
                OptixChip(
                    text = category,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { item ->
                OptixProductCard(
                    title = item.title,
                    price = item.price,
                    stockCount = item.stockCount,
                    onAddToCart = { onAddToCart(item) }
                )
            }
        }
    }
}
