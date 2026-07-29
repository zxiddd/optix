package com.zaddy.optix.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

enum class PricingTypeStrategy {
    FIXED,
    WEIGHT,
    VARIABLE,
    MARKET
}

@Composable
fun AddProductFormScreen(
    onSaveProduct: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var productName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Bakery") }
    var price by remember { mutableStateOf("") }
    var pricingStrategy by remember { mutableStateOf(PricingTypeStrategy.FIXED) }
    var barcode by remember { mutableStateOf("") }
    var stockCount by remember { mutableStateOf("50") }
    var selectedPrinterCategory by remember { mutableStateOf("Kitchen KDS Printer") }

    val categories = listOf("Bakery", "Beverages", "Meals", "Desserts")
    val printerCategories = listOf("Kitchen KDS Printer", "Bar Printer", "Counter POS Printer")

    val scrollState = rememberScrollState()

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
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OptixTextPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Product", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = OptixSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OptixButton(
                        text = "SAVE PRODUCT TO CATALOG",
                        onClick = onSaveProduct,
                        icon = Icons.Default.Save
                    )
                }
            }
        },
        containerColor = OptixDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Product Image Upload Card Box
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Product Image (Optional)", color = OptixTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(OptixSurface)
                            .border(1.dp, OptixCardBorder, RoundedCornerShape(16.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload", tint = OptixOrange, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("TAP TO UPLOAD IMAGE", color = OptixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Identity Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Product Details", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Product Name
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Product Name", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Fastfood, contentDescription = null, tint = OptixOrange) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OptixOrange,
                            unfocusedBorderColor = OptixCardBorder,
                            focusedTextColor = OptixTextPrimary,
                            unfocusedTextColor = OptixTextPrimary,
                            focusedContainerColor = OptixSurface,
                            unfocusedContainerColor = OptixSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Selector Chips
                    Text("Category", color = OptixTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            OptixChip(
                                text = cat,
                                isSelected = selectedCategory == cat,
                                onClick = { selectedCategory = cat }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing & Pricing Strategy Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Pricing Strategy & Rate", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Pricing Type Strategy Chips: Fixed, Weight, Variable, Market
                    Text("Pricing Strategy Type", color = OptixTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PricingTypeStrategy.values().forEach { strategy ->
                            OptixChip(
                                text = strategy.name,
                                isSelected = pricingStrategy == strategy,
                                onClick = { pricingStrategy = strategy },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price Input Field
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Selling Price ($)", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = OptixOrange) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OptixOrange,
                            unfocusedBorderColor = OptixCardBorder,
                            focusedTextColor = OptixTextPrimary,
                            unfocusedTextColor = OptixTextPrimary,
                            focusedContainerColor = OptixSurface,
                            unfocusedContainerColor = OptixSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inventory & Hardware Routing Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Inventory & Printer Routing", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Barcode Input Field
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode / SKU Number", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = OptixOrange) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OptixOrange,
                            unfocusedBorderColor = OptixCardBorder,
                            focusedTextColor = OptixTextPrimary,
                            unfocusedTextColor = OptixTextPrimary,
                            focusedContainerColor = OptixSurface,
                            unfocusedContainerColor = OptixSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stock Count Input Field
                    OutlinedTextField(
                        value = stockCount,
                        onValueChange = { stockCount = it },
                        label = { Text("Available Inventory Stock", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null, tint = OptixOrange) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OptixOrange,
                            unfocusedBorderColor = OptixCardBorder,
                            focusedTextColor = OptixTextPrimary,
                            unfocusedTextColor = OptixTextPrimary,
                            focusedContainerColor = OptixSurface,
                            unfocusedContainerColor = OptixSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Printer Category Chips
                    Text("Printer Routing Category", color = OptixTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        printerCategories.forEach { printerCat ->
                            OptixChip(
                                text = printerCat,
                                isSelected = selectedPrinterCategory == printerCat,
                                onClick = { selectedPrinterCategory = printerCat }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
