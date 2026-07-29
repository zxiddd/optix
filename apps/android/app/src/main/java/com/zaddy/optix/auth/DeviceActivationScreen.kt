package com.zaddy.optix.auth

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

enum class DeviceAuthTab {
    ACTIVATION_CODE,
    ADMIN_LOGIN,
    REGISTER
}

@Composable
fun DeviceActivationScreen(
    onDeviceActivated: () -> Unit
) {
    var activeTab by remember { mutableStateOf(DeviceAuthTab.ACTIVATION_CODE) }

    // Code Activation State
    var activationCode by remember { mutableStateOf("OPTX-9022-8F3A") }
    
    // Login Form State
    var email by remember { mutableStateOf("owner@metrocafe.com") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regBusinessName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Illustration Icon Badge
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(OptixOrangeSubtle)
                    .border(2.dp, OptixOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PointOfSale,
                    contentDescription = null,
                    tint = OptixOrange,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "OPTIX TERMINAL",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = OptixTextPrimary
            )

            Text(
                text = "Activate register hardware or sign in to store account",
                color = OptixTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3-Way Segmented Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(OptixSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    DeviceAuthTab.ACTIVATION_CODE to "Code",
                    DeviceAuthTab.ADMIN_LOGIN to "Sign In",
                    DeviceAuthTab.REGISTER to "Register"
                ).forEach { (tab, title) ->
                    val isSelected = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) OptixOrange else Color.Transparent)
                            .clickable {
                                errorMessage = null
                                activeTab = tab
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = OptixTextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Dark Card Container
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(targetState = activeTab, label = "tabCrossfade") { tab ->
                    when (tab) {
                        DeviceAuthTab.ACTIVATION_CODE -> {
                            Column {
                                Text(
                                    text = "Enter Device Activation Code",
                                    color = OptixTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Enter code generated from your Optix Web Portal",
                                    color = OptixTextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Activation Code Input Field
                                OutlinedTextField(
                                    value = activationCode,
                                    onValueChange = { activationCode = it.uppercase() },
                                    label = { Text("Activation Code", color = OptixTextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = OptixOrange) },
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp,
                                        color = OptixTextPrimary
                                    ),
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

                                Spacer(modifier = Modifier.height(12.dp))

                                // QR Code Scanner Button
                                OptixButton(
                                    text = "SCAN QR CODE",
                                    onClick = { },
                                    isSecondary = true,
                                    icon = Icons.Default.QrCodeScanner
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Device Hardware Information Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(OptixSurface)
                                        .border(1.dp, OptixCardBorder, RoundedCornerShape(16.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ImportantDevices, contentDescription = null, tint = OptixOrange, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Hardware Diagnostics", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Model", color = OptixTextSecondary, fontSize = 11.sp)
                                            Text("Sunmi V2 Pro Terminal", color = OptixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Hardware ID", color = OptixTextSecondary, fontSize = 11.sp)
                                            Text("DEV-9022-8F3A", color = OptixOrange, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Network IP", color = OptixTextSecondary, fontSize = 11.sp)
                                            Text("192.168.1.105", color = OptixTextPrimary, fontSize = 11.sp)
                                        }
                                    }
                                }

                                errorMessage?.let {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = it, color = OptixErrorRed, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Register Device Primary Orange Button
                                OptixButton(
                                    text = if (isSubmitting) "ACTIVATING DEVICE..." else "REGISTER & ACTIVATE DEVICE",
                                    onClick = {
                                        if (activationCode.isBlank()) {
                                            errorMessage = "Activation Code is required."
                                            return@OptixButton
                                        }
                                        isSubmitting = true
                                        onDeviceActivated()
                                    }
                                )
                            }
                        }

                        DeviceAuthTab.ADMIN_LOGIN -> {
                            Column {
                                Text(text = "Admin Account Sign In", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Account Email", color = OptixTextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = OptixOrange) },
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

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password", color = OptixTextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = OptixOrange) },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = OptixTextSecondary
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

                                Spacer(modifier = Modifier.height(20.dp))

                                OptixButton(
                                    text = "SIGN IN & ACTIVATE",
                                    onClick = onDeviceActivated
                                )
                            }
                        }

                        DeviceAuthTab.REGISTER -> {
                            Column {
                                Text(text = "Create Business Account", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = regBusinessName,
                                    onValueChange = { regBusinessName = it },
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

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = regEmail,
                                    onValueChange = { regEmail = it },
                                    label = { Text("Email Address", color = OptixTextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = OptixOrange) },
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

                                Spacer(modifier = Modifier.height(20.dp))

                                OptixButton(
                                    text = "REGISTER BUSINESS",
                                    onClick = onDeviceActivated
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
