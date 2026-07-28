package com.zaddy.optix.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

@Composable
fun DeviceActivationScreen(
    onDeviceActivated: () -> Unit
) {
    var email by remember { mutableStateOf("owner@metrocafe.com") }
    var password by remember { mutableStateOf("") }
    var terminalName by remember { mutableStateOf("Counter POS 1") }
    var isRegistering by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        OptixCard(
            modifier = Modifier
                .width(420.dp)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(OptixOrangeSubtle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = null,
                        tint = OptixOrange,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "OPTIX BILLING",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OptixTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sign in to activate this register terminal.",
                    color = OptixTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Account Email", color = OptixTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OptixOrange,
                        unfocusedBorderColor = OptixCardBorder,
                        focusedTextColor = OptixTextPrimary,
                        unfocusedTextColor = OptixTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = OptixTextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OptixOrange,
                        unfocusedBorderColor = OptixCardBorder,
                        focusedTextColor = OptixTextPrimary,
                        unfocusedTextColor = OptixTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = terminalName,
                    onValueChange = { terminalName = it },
                    label = { Text("Terminal Name", color = OptixTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OptixOrange,
                        unfocusedBorderColor = OptixCardBorder,
                        focusedTextColor = OptixTextPrimary,
                        unfocusedTextColor = OptixTextPrimary
                    )
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = OptixErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OptixButton(
                    text = if (isRegistering) "ACTIVATING TERMINAL..." else "ACTIVATE REGISTER",
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Email and Password are required."
                            return@OptixButton
                        }
                        isRegistering = true
                        onDeviceActivated()
                    }
                )
            }
        }
    }
}
