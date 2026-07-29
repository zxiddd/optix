package com.zaddy.optix.billing

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*
import kotlin.random.Random

@Composable
fun CheckoutSuccessScreen(
    billTotal: String = "$26.95",
    tokenNumber: String = "#042",
    onStartNewBill: () -> Unit = {},
    onPrintAgain: () -> Unit = {}
) {
    var tickScale by remember { mutableStateOf(0.4f) }

    val animatedTickScale by animateFloatAsState(
        targetValue = tickScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tickScaleAnimation"
    )

    LaunchedEffect(Unit) {
        tickScale = 1.0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OptixDarkBackground.copy(alpha = 0.98f)),
        contentAlignment = Alignment.Center
    ) {
        // Confetti Particle Canvas Animation Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(Color(0xFFFF6B00), Color(0xFF22C55E), Color(0xFF3B82F6), Color(0xFFF59E0B))
            repeat(35) { index ->
                val x = Random.nextFloat() * size.width
                val y = Random.nextFloat() * size.height * 0.7f
                val radius = Random.nextFloat() * 8.dp.toPx() + 4.dp.toPx()
                val color = colors[index % colors.size]
                drawCircle(color = color.copy(alpha = 0.7f), radius = radius, center = Offset(x, y))
            }
        }

        // Main Success Dialog Card
        OptixCard(
            modifier = Modifier
                .width(380.dp)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Animated Green Tick Icon Badge
                Box(
                    modifier = Modifier
                        .size((90 * animatedTickScale).dp)
                        .clip(CircleShape)
                        .background(OptixSuccessGreen.copy(alpha = 0.2f))
                        .border(3.dp, OptixSuccessGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = OptixSuccessGreen,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PAYMENT SUCCESSFUL!",
                    color = OptixSuccessGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Token Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(OptixOrangeSubtle)
                        .border(1.dp, OptixOrange, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "TOKEN $tokenNumber",
                        color = OptixOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Total Paid", color = OptixTextSecondary, fontSize = 12.sp)
                Text(
                    text = billTotal,
                    color = OptixTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Action Buttons: Print Again & WhatsApp Receipt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Print Again Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(OptixSurface)
                            .border(1.dp, OptixCardBorder, RoundedCornerShape(14.dp))
                            .clickable { onPrintAgain() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Print, contentDescription = "Print", tint = OptixTextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PRINT AGAIN", color = OptixTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // WhatsApp Receipt Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(OptixSuccessGreen.copy(alpha = 0.2f))
                            .border(1.dp, OptixSuccessGreen, RoundedCornerShape(14.dp))
                            .clickable { }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = OptixSuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WHATSAPP", color = OptixSuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Action Button: Start New Bill
                OptixButton(
                    text = "START NEW BILL",
                    onClick = onStartNewBill,
                    icon = Icons.Default.AddShoppingCart
                )
            }
        }
    }
}
