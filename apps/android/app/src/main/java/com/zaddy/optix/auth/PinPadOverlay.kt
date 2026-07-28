package com.zaddy.optix.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun PinPadOverlay(
    onPinSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val keys = remember { listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        OptixCard(
            modifier = Modifier
                .width(360.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Staff Security PIN",
                    color = OptixTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your 4-digit staff PIN to unlock terminal.",
                    color = OptixTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(4) { index ->
                        val isFilled = index < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) OptixOrange else OptixCardBorder)
                                .border(1.dp, if (isFilled) OptixOrange else OptixTextMuted, CircleShape)
                        )
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = it, color = OptixErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                val chunkedKeys = keys.chunked(3)
                chunkedKeys.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowKeys.forEach { key ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(if (key == "OK") OptixOrange else OptixSurface)
                                    .border(1.dp, OptixCardBorder, CircleShape)
                                    .clickable {
                                        when (key) {
                                            "C" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                            "OK" -> {
                                                if (enteredPin == "1234") {
                                                    onPinSuccess()
                                                } else {
                                                    errorMessage = "Incorrect PIN. Try 1234."
                                                    enteredPin = ""
                                                }
                                            }
                                            else -> {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += key
                                                    errorMessage = null
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    color = OptixTextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
