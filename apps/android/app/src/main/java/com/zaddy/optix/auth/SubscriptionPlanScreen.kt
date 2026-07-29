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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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

data class PricingPlanItem(
    val id: String,
    val durationTitle: String,
    val price: String,
    val savingsBadge: String?,
    val isYearlyHighlighted: Boolean = false
)

@Composable
fun SubscriptionPlanScreen(
    onPlanSelected: () -> Unit
) {
    var selectedPlanId by remember { mutableStateOf("1_year") }

    val plans = listOf(
        PricingPlanItem("monthly", "Monthly Plan", "$29 / month", null),
        PricingPlanItem("3_months", "3 Months Plan", "$79 ($26/mo)", "Save 10%"),
        PricingPlanItem("6_months", "6 Months Plan", "$149 ($24/mo)", "Save 15%"),
        PricingPlanItem("9_months", "9 Months Plan", "$219 ($24/mo)", "Save 20%"),
        PricingPlanItem("1_year", "1 Year Pro (Best Value)", "$269 ($22/mo)", "Save 25% • POPULAR", isYearlyHighlighted = true)
    )

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Surface(
                color = OptixSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OptixButton(
                        text = if (selectedPlanId == "free") "START FREE TRIAL (10 BILLS/DAY)" else "SUBSCRIBE NOW & UNLOCK UNLIMITED",
                        onClick = onPlanSelected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cancel or upgrade anytime • 7-day offline grace protection included",
                        color = OptixTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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

            // Premium Icon Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(OptixOrangeSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = OptixOrange,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Header Section
            Text(
                text = "CHOOSE YOUR PLAN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = OptixTextPrimary
            )
            Text(
                text = "Unlock unlimited register billing & cloud sync",
                color = OptixTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Free Trial Banner Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (selectedPlanId == "free") OptixOrangeSubtle else OptixCardBg)
                    .border(
                        1.dp,
                        if (selectedPlanId == "free") OptixOrange else OptixCardBorder,
                        RoundedCornerShape(22.dp)
                    )
                    .clickable { selectedPlanId = "free" }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Free Starter Trial",
                                color = OptixTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OptixSuccessGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("FREE", color = OptixSuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Limited to 10 Bills Per Day • Basic Features",
                            color = OptixTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    RadioButton(
                        selected = selectedPlanId == "free",
                        onClick = { selectedPlanId = "free" },
                        colors = RadioButtonDefaults.colors(selectedColor = OptixOrange, unselectedColor = OptixTextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Unlimited Pro Plans",
                color = OptixTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subscription Pricing Cards List
            plans.forEach { plan ->
                val isSelected = selectedPlanId == plan.id
                val isYearly = plan.isYearlyHighlighted

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            when {
                                isSelected -> OptixOrangeSubtle
                                isYearly -> OptixCardBg.copy(alpha = 0.9f)
                                else -> OptixCardBg
                            }
                        )
                        .border(
                            width = if (isSelected || isYearly) 2.dp else 1.dp,
                            color = when {
                                isSelected -> OptixOrange
                                isYearly -> OptixOrange.copy(alpha = 0.6f)
                                else -> OptixCardBorder
                            },
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable { selectedPlanId = plan.id }
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = plan.durationTitle,
                                    color = OptixTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (isYearly) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Popular",
                                        tint = OptixOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = plan.price,
                                color = OptixOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            plan.savingsBadge?.let { badge ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OptixOrange)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        color = OptixTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedPlanId = plan.id },
                            colors = RadioButtonDefaults.colors(selectedColor = OptixOrange, unselectedColor = OptixTextMuted)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
