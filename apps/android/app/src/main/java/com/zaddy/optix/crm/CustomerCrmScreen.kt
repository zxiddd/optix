package com.zaddy.optix.crm

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class CustomerCrmUI(
    val id: String,
    val name: String,
    val phone: String,
    val totalVisits: Int,
    val lifetimeSpend: String,
    val khataBalance: String,
    val notes: String
)

@Composable
fun CustomerCrmScreen(
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val customers = remember {
        listOf(
            CustomerCrmUI("1", "Alex Smith", "+1 555 019 2831", 24, "$1,480.50", "$0.00", "VIP Loyalty Member • Prefers Oat Milk Cappuccino"),
            CustomerCrmUI("2", "Sarah Miller", "+1 555 492 1083", 18, "$920.00", "$45.00", "Khata Credit Ledger Active • Weekly Office Order"),
            CustomerCrmUI("3", "David Johnson", "+1 555 832 9912", 12, "$540.00", "$0.00", "Regular Breakfast Customer"),
            CustomerCrmUI("4", "Emily Davis", "+1 555 204 7711", 8, "$310.00", "$15.00", "Gluten-Free Bakery Preference")
        )
    }

    val filteredCustomers = customers.filter { c ->
        searchQuery.isEmpty() || c.name.contains(searchQuery, ignoreCase = true) || c.phone.contains(searchQuery)
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
                        Text("Customer CRM & Khata Ledger", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OptixOrange)
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add", tint = OptixTextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADD CUSTOMER", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                placeholderText = "Search customer name or phone..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCustomers) { customer ->
                    OptixCard(
                        modifier = Modifier.fillMaxWidth()
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
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(OptixOrangeSubtle),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = customer.name.take(1),
                                            color = OptixOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(customer.name, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(customer.phone, color = OptixTextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(customer.lifetimeSpend, color = OptixOrange, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                    Text("${customer.totalVisits} Visits", color = OptixTextSecondary, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Khata Credit & Notes Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OptixSurface)
                                    .border(1.dp, OptixCardBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.StickyNote2, contentDescription = null, tint = OptixOrange, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(customer.notes, color = OptixTextSecondary, fontSize = 11.sp)
                                    }

                                    if (customer.khataBalance != "$0.00") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(OptixErrorRed.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Khata: ${customer.khataBalance}", color = OptixErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Purchase History & Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(OptixSurface)
                                        .border(1.dp, OptixCardBorder, RoundedCornerShape(10.dp))
                                        .clickable { }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.History, contentDescription = null, tint = OptixTextPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("PURCHASE HISTORY", color = OptixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
