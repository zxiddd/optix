package com.zaddy.optix.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PinPadOverlay(
    onPinSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    // Shake animation trigger on wrong PIN
    fun triggerWrongPinAnimation() {
        coroutineScope.launch {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -10f at 250
                    10f at 300
                    0f at 400
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground.copy(alpha = 0.96f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset(x = shakeOffset.value.dp)
                .width(360.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glass Card Container
            OptixCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(OptixOrangeSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = OptixOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "MANAGER UNLOCK",
                        color = OptixTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Enter 4-digit PIN or use Biometrics",
                        color = OptixTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large Circular PIN Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        repeat(4) { index ->
                            val isFilled = index < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isFilled) OptixOrange else OptixSurface)
                                    .border(2.dp, if (isFilled) OptixOrange else OptixCardBorder, CircleShape)
                            )
                        }
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = it, color = OptixErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Keypad Grid with Biometrics Button
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("BIO", "0", "DEL")
                    )

                    keys.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowKeys.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (key) {
                                                "BIO" -> OptixOrangeSubtle
                                                "DEL" -> OptixSurface
                                                else -> OptixSurface
                                            }
                                        )
                                        .border(1.dp, OptixCardBorder, CircleShape)
                                        .clickable {
                                            when (key) {
                                                "BIO" -> {
                                                    // Quick Biometric Unlock
                                                    onPinSuccess()
                                                }
                                                "DEL" -> {
                                                    if (enteredPin.isNotEmpty()) {
                                                        enteredPin = enteredPin.dropLast(1)
                                                        errorMessage = null
                                                    }
                                                }
                                                else -> {
                                                    if (enteredPin.length < 4) {
                                                        enteredPin += key
                                                        errorMessage = null
                                                        if (enteredPin.length == 4) {
                                                            if (enteredPin == "1234") {
                                                                onPinSuccess()
                                                            } else {
                                                                errorMessage = "Incorrect PIN. Try 1234."
                                                                triggerWrongPinAnimation()
                                                                enteredPin = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (key) {
                                        "BIO" -> Icon(Icons.Default.Fingerprint, contentDescription = "Biometric", tint = OptixOrange, modifier = Modifier.size(28.dp))
                                        "DEL" -> Icon(Icons.Default.Backspace, contentDescription = "Backspace", tint = OptixTextSecondary, modifier = Modifier.size(22.dp))
                                        else -> Text(text = key, color = OptixTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}
