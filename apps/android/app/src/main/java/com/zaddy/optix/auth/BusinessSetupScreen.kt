package com.zaddy.optix.auth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

@Composable
fun BusinessSetupScreen(
    onSetupComplete: () -> Unit
) {
    var businessName by remember { mutableStateOf("Metro Cafe & Bakery") }
    var selectedBusinessType by remember { mutableStateOf("Restaurant & Cafe") }
    var phone by remember { mutableStateOf("+1 555 019 2831") }
    var address by remember { mutableStateOf("123 Main Street, Suite 400, New York, NY") }
    var gstNumber by remember { mutableStateOf("22AAAAA0000A1Z5") }
    var selectedCurrency by remember { mutableStateOf("USD ($)") }
    var receiptHeader by remember { mutableStateOf("Thank you for visiting Metro Cafe!") }
    var receiptFooter by remember { mutableStateOf("Visit us online at www.metrocafe.com") }

    val businessTypes = listOf("Restaurant & Cafe", "Retail & Apparel", "Grocery & Supermarket", "Pharmacy", "Chicken & Fresh Produce")
    val currencies = listOf("USD ($)", "INR (₹)", "EUR (€)", "GBP (£)", "AED (AED)")

    val scrollState = rememberScrollState()

    Scaffold(
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
                        text = "SAVE & CONTINUE TO REGISTER",
                        onClick = onSetupComplete
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
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header Section
            Text(
                text = "BUSINESS SETUP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = OptixTextPrimary
            )
            Text(
                text = "Configure your store details & receipt header",
                color = OptixTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Business Logo Upload Card Box
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Store Logo (Optional)",
                        color = OptixTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(OptixSurface)
                            .border(2.dp, OptixOrange, CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Upload Logo",
                                tint = OptixOrange,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "UPLOAD", color = OptixTextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Business Form Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "General Information",
                        color = OptixTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Business Name
                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business Name", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = OptixOrange) },
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

                    // Business Type Selector Chips
                    Text(text = "Business Type", color = OptixTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        businessTypes.take(3).forEach { type ->
                            OptixChip(
                                text = type,
                                isSelected = selectedBusinessType == type,
                                onClick = { selectedBusinessType = type }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Business Phone", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = OptixOrange) },
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

                    // Business Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Business Address", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = OptixOrange) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
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

            // Tax & Receipt Details Card
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Receipt & Tax Configuration",
                        color = OptixTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // GST / Tax ID (Optional)
                    OutlinedTextField(
                        value = gstNumber,
                        onValueChange = { gstNumber = it },
                        label = { Text("GST / Tax Registration ID (Optional)", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = OptixOrange) },
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

                    // Currency Selector Chips
                    Text(text = "Base Currency", color = OptixTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.take(3).forEach { curr ->
                            OptixChip(
                                text = curr,
                                isSelected = selectedCurrency == curr,
                                onClick = { selectedCurrency = curr }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Receipt Header Message
                    OutlinedTextField(
                        value = receiptHeader,
                        onValueChange = { receiptHeader = it },
                        label = { Text("Receipt Header Note", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = OptixOrange) },
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

                    // Receipt Footer Message
                    OutlinedTextField(
                        value = receiptFooter,
                        onValueChange = { receiptFooter = it },
                        label = { Text("Receipt Footer Note", color = OptixTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = OptixOrange) },
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
