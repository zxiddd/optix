package com.zaddy.optix.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.zaddy.optix.billing.CheckoutSuccessScreen
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class RegisterProductUI(
    val id: String,
    val title: String,
    val price: Double,
    val stockCount: Int,
    val category: String
)

data class RegisterCartItemUI(
    val product: RegisterProductUI,
    var quantity: Int
)

@Composable
fun BillingScreen(
    onCheckoutSuccess: () -> Unit = {}
) {
    var isCheckoutComplete by remember { mutableStateOf(false) }
    var lastChargedTotal by remember { mutableStateOf("$0.00") }
    var lastTokenNumber by remember { mutableStateOf("042") }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTenderMethod by remember { mutableStateOf("CASH") }
    var tokenNumber by remember { mutableStateOf(42) }

    val products = remember {
        listOf(
            RegisterProductUI("1", "Butter Croissant", 4.50, 42, "Bakery"),
            RegisterProductUI("2", "Cappuccino Large", 5.00, 100, "Beverages"),
            RegisterProductUI("3", "Blueberry Muffin", 3.50, 25, "Bakery"),
            RegisterProductUI("4", "Iced Vanilla Latte", 5.50, 80, "Beverages"),
            RegisterProductUI("5", "Avocado Toast", 8.50, 15, "Meals"),
            RegisterProductUI("6", "Chocolate Lava Cake", 6.00, 20, "Desserts")
        )
    }

    val cartItems = remember { mutableStateListOf<RegisterCartItemUI>() }

    fun addProductToCart(product: RegisterProductUI) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(RegisterCartItemUI(product, 1))
        }
    }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val taxTotal = subtotal * 0.10
    val grandTotal = subtotal + taxTotal

    val filteredProducts = products.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
    }

    if (isCheckoutComplete) {
        CheckoutSuccessScreen(
            billTotal = lastChargedTotal,
            tokenNumber = lastTokenNumber,
            onStartNewBill = {
                cartItems.clear()
                isCheckoutComplete = false
            },
            onPrintAgain = { }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OptixDarkBackground)
                .padding(14.dp)
        ) {
            // Top Bar: Business Name, Token Number, Search Bar, Categories
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Metro Cafe & Bakery", color = OptixTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Fast Billing Register • SLA <50ms", color = OptixTextSecondary, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(OptixOrangeSubtle)
                        .border(1.dp, OptixOrange, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "TOKEN #${String.format("%03d", tokenNumber)}",
                        color = OptixOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OptixSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Search products, SKU or barcode..."
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Bakery", "Beverages", "Meals", "Desserts").forEach { category ->
                    OptixChip(
                        text = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Split Register Layout
            Column(modifier = Modifier.weight(1f)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    items(filteredProducts) { item ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(OptixCardBg)
                                .border(1.dp, OptixCardBorder, RoundedCornerShape(16.dp))
                                .clickable { addProductToCart(item) }
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(item.title, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Text("$${String.format("%.2f", item.price)}", color = OptixOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OptixCard(
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CURRENT BILL (${cartItems.sumOf { it.quantity }} items)",
                                color = OptixTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            if (cartItems.isNotEmpty()) {
                                Text(
                                    text = "CLEAR ALL",
                                    color = OptixErrorRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.clickable { cartItems.clear() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(cartItems, key = { it.product.id }) { cartItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OptixSurface)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(cartItem.product.title, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("$${String.format("%.2f", cartItem.product.price * cartItem.quantity)}", color = OptixOrange, fontSize = 12.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(OptixCardBg)
                                                .clickable {
                                                    if (cartItem.quantity > 1) {
                                                        cartItem.quantity--
                                                    } else {
                                                        cartItems.remove(cartItem)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (cartItem.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                                contentDescription = "Decrease",
                                                tint = OptixErrorRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        Text(
                                            text = "${cartItem.quantity}",
                                            color = OptixTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(OptixOrange)
                                                .clickable { cartItem.quantity++ },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = OptixTextPrimary, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = OptixTextSecondary, fontSize = 12.sp)
                            Text("$${String.format("%.2f", subtotal)}", color = OptixTextPrimary, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tax GST (10%)", color = OptixTextSecondary, fontSize = 12.sp)
                            Text("+$${String.format("%.2f", taxTotal)}", color = OptixTextPrimary, fontSize = 12.sp)
                        }

                        Divider(color = OptixCardBorder, modifier = Modifier.padding(vertical = 6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("GRAND TOTAL", color = OptixTextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text(
                                "$${String.format("%.2f", grandTotal)}",
                                color = OptixOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("CASH", "UPI", "CARD", "SPLIT").forEach { tender ->
                                OptixChip(
                                    text = tender,
                                    isSelected = selectedTenderMethod == tender,
                                    onClick = { selectedTenderMethod = tender },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OptixButton(
                            text = if (cartItems.isEmpty()) "SELECT ITEMS TO CHARGE" else "CHARGE • $${String.format("%.2f", grandTotal)}",
                            onClick = {
                                if (cartItems.isNotEmpty()) {
                                    lastChargedTotal = "$${String.format("%.2f", grandTotal)}"
                                    lastTokenNumber = "#${String.format("%03d", tokenNumber)}"
                                    tokenNumber++
                                    isCheckoutComplete = true
                                    onCheckoutSuccess()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
