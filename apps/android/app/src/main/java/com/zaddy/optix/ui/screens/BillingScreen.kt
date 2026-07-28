package com.zaddy.optix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class CartItemUI(
    val id: String,
    val title: String,
    val unitPrice: Double,
    var quantity: Int
)

@Composable
fun BillingScreen(
    onCheckoutSuccess: () -> Unit = {}
) {
    var selectedOrderType by remember { mutableStateOf("Dine-In (Table 1)") }
    var selectedTenderMethod by remember { mutableStateOf("CASH") }
    val cartItems = remember {
        mutableStateListOf(
            CartItemUI("1", "Classic Butter Croissant", 4.50, 2),
            CartItemUI("2", "Espresso Cappuccino Large", 5.00, 1),
            CartItemUI("3", "Blueberry Muffin Batch", 3.50, 3)
        )
    }

    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    val taxTotal = subtotal * 0.10
    val grandTotal = subtotal + taxTotal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground)
            .padding(16.dp)
    ) {
        // Mode Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OptixChip(
                text = "Dine-In (Table 1)",
                isSelected = selectedOrderType == "Dine-In (Table 1)",
                onClick = { selectedOrderType = "Dine-In (Table 1)" }
            )
            OptixChip(
                text = "Takeaway",
                isSelected = selectedOrderType == "Takeaway",
                onClick = { selectedOrderType = "Takeaway" }
            )
            OptixChip(
                text = "Delivery",
                isSelected = selectedOrderType == "Delivery",
                onClick = { selectedOrderType = "Delivery" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Cart Items List
        Text(
            text = "Active Order Items (${cartItems.sumOf { it.quantity }})",
            color = OptixTextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cartItems) { item ->
                OptixCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = OptixTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "$${String.format("%.2f", item.unitPrice)} each",
                                color = OptixTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Quantity Control Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(OptixSurface)
                                    .clickable {
                                        if (item.quantity > 1) {
                                            item.quantity--
                                        } else {
                                            cartItems.remove(item)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                    contentDescription = "Decrease",
                                    tint = OptixErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "${item.quantity}",
                                color = OptixTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(OptixOrange)
                                    .clickable { item.quantity++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = OptixTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live Total Card
        OptixCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = OptixSurface
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", color = OptixTextSecondary, fontSize = 14.sp)
                Text("$${String.format("%.2f", subtotal)}", color = OptixTextPrimary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("GST Tax (10%)", color = OptixTextSecondary, fontSize = 14.sp)
                Text("$${String.format("%.2f", taxTotal)}", color = OptixTextPrimary, fontSize = 14.sp)
            }
            Divider(color = OptixCardBorder, modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Grand Total", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "$${String.format("%.2f", grandTotal)}",
                    color = OptixOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tender Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("CASH", "UPI", "CARD").forEach { tender ->
                OptixChip(
                    text = tender,
                    isSelected = selectedTenderMethod == tender,
                    onClick = { selectedTenderMethod = tender },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Orange Checkout Button
        OptixButton(
            text = "COMPLETE CHECKOUT • $${String.format("%.2f", grandTotal)}",
            onClick = onCheckoutSuccess
        )
    }
}
