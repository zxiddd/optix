package com.zaddy.optix.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

enum class AuthMode {
    LOGIN,
    REGISTER
}

enum class LoginRoleTab {
    ADMIN_LOGIN,
    STAFF_PIN
}

@Composable
fun DeviceActivationScreen(
    onDeviceActivated: () -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var loginRoleTab by remember { mutableStateOf(LoginRoleTab.ADMIN_LOGIN) }

    // Login Form State
    var email by remember { mutableStateOf("owner@metrocafe.com") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regBusinessName by remember { mutableStateOf("") }
    var regOwnerName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }

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
            // Bigger Optix Logo Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(OptixOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "O",
                    color = OptixTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Larger Heading
            Text(
                text = if (authMode == AuthMode.LOGIN) "OPTIX BILLING" else "REGISTER BUSINESS",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = OptixTextPrimary
            )

            Text(
                text = if (authMode == AuthMode.LOGIN)
                    "Business Operating System Terminal Sign In"
                else
                    "Create a new multi-tenant Optix POS account",
                color = OptixTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Card Container with 22.dp Rounded Corners
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(targetState = authMode, label = "authCrossfade") { mode ->
                    if (mode == AuthMode.LOGIN) {
                        Column {
                            // Admin / Staff Segmented Control
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(OptixSurface)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (loginRoleTab == LoginRoleTab.ADMIN_LOGIN) OptixOrange else Color.Transparent)
                                        .clickable { loginRoleTab = LoginRoleTab.ADMIN_LOGIN },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Admin Login",
                                        color = OptixTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (loginRoleTab == LoginRoleTab.STAFF_PIN) OptixOrange else Color.Transparent)
                                        .clickable { loginRoleTab = LoginRoleTab.STAFF_PIN },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Staff PIN",
                                        color = OptixTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (loginRoleTab == LoginRoleTab.ADMIN_LOGIN) {
                                // Premium Email Field
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Account Email", color = OptixTextSecondary) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = OptixOrange)
                                    },
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

                                // Premium Password Field with Eye Toggle
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password", color = OptixTextSecondary) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = OptixOrange)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle Password Visibility",
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

                                errorMessage?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = it, color = OptixErrorRed, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Primary Action Sign In Button
                                OptixButton(
                                    text = if (isSubmitting) "ACTIVATING..." else "SIGN IN & ACTIVATE",
                                    onClick = {
                                        if (email.isBlank()) {
                                            errorMessage = "Email is required."
                                            return@OptixButton
                                        }
                                        isSubmitting = true
                                        onDeviceActivated()
                                    }
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Google Sign In Button
                                Button(
                                    onClick = { onDeviceActivated() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OptixSurface,
                                        contentColor = OptixTextPrimary
                                    )
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Google",
                                            tint = OptixTextPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Sign in with Google", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Register Link
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "Don't have an account? ", color = OptixTextSecondary, fontSize = 13.sp)
                                    Text(
                                        text = "Register Business",
                                        color = OptixOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.clickable {
                                            errorMessage = null
                                            authMode = AuthMode.REGISTER
                                        }
                                    )
                                }
                            } else {
                                // Staff PIN Quick Unlock Instructions
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Pin,
                                        contentDescription = null,
                                        tint = OptixOrange,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Staff Quick PIN Unlock",
                                        color = OptixTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Terminal already registered. Use PIN Pad to sign in as cashier.",
                                        color = OptixTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    OptixButton(
                                        text = "OPEN PIN PAD",
                                        onClick = onDeviceActivated
                                    )
                                }
                            }
                        }
                    } else {
                        // Registration Form View
                        Column {
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
                                value = regOwnerName,
                                onValueChange = { regOwnerName = it },
                                label = { Text("Owner Name", color = OptixTextSecondary) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = OptixOrange) },
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
                                value = regPhone,
                                onValueChange = { regPhone = it },
                                label = { Text("Phone Number", color = OptixTextSecondary) },
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

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it },
                                label = { Text("Password", color = OptixTextSecondary) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = OptixOrange) },
                                trailingIcon = {
                                    IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isRegPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Password Visibility",
                                            tint = OptixTextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                                value = regConfirmPassword,
                                onValueChange = { regConfirmPassword = it },
                                label = { Text("Confirm Password", color = OptixTextSecondary) },
                                leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = OptixOrange) },
                                visualTransformation = PasswordVisualTransformation(),
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

                            errorMessage?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = it, color = OptixErrorRed, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            OptixButton(
                                text = "REGISTER & CONTINUE",
                                onClick = {
                                    if (regBusinessName.isBlank() || regEmail.isBlank()) {
                                        errorMessage = "Business Name and Email are required."
                                        return@OptixButton
                                    }
                                    if (regPassword != regConfirmPassword) {
                                        errorMessage = "Passwords do not match."
                                        return@OptixButton
                                    }
                                    onDeviceActivated()
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "Already have an account? ", color = OptixTextSecondary, fontSize = 13.sp)
                                Text(
                                    text = "Back to Sign In",
                                    color = OptixOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.clickable {
                                        errorMessage = null
                                        authMode = AuthMode.LOGIN
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
