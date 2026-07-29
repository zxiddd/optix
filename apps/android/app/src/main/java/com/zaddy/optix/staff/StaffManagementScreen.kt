package com.zaddy.optix.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaddy.optix.ui.components.*
import com.zaddy.optix.ui.theme.*

data class StaffMemberUI(
    val id: String,
    val name: String,
    val role: String,
    val pinMasked: String,
    val shiftStatus: String,
    val permissions: List<String>,
    val isActive: Boolean
)

@Composable
fun StaffManagementScreen(
    onBack: () -> Unit = {}
) {
    val staffMembers = remember {
        listOf(
            StaffMemberUI("1", "John Doe", "OWNER", "****", "Active Shift", listOf("BILL_CREATE", "BILL_VOID", "SHIFT_OPEN_CLOSE", "REPORTS_VIEW", "KHATA_REPAY"), true),
            StaffMemberUI("2", "Sarah Jenkins", "MANAGER", "****", "Active Shift", listOf("BILL_CREATE", "BILL_VOID", "SHIFT_OPEN_CLOSE", "REPORTS_VIEW"), true),
            StaffMemberUI("3", "David Miller", "CASHIER", "****", "Shift Closed", listOf("BILL_CREATE"), true),
            StaffMemberUI("4", "Alex Smith", "KITCHEN_STAFF", "****", "Active Shift", listOf("KDS_VIEW"), true)
        )
    }

    val scrollState = rememberScrollState()

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
                        Text("Staff & RBAC Permissions", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OptixOrange)
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Invite", tint = OptixTextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("INVITE", color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = OptixOrange,
                contentColor = OptixTextPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Staff", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = OptixDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "7-Role Permission Control",
                color = OptixTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Manage staff security PINs, shift status & role permissions",
                color = OptixTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Staff Cards List
            staffMembers.forEach { staff ->
                OptixCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar Photo Badge
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(OptixOrangeSubtle)
                                        .border(1.dp, OptixOrange, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = staff.name.take(1),
                                        color = OptixOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(staff.name, color = OptixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Active Status Dot
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (staff.isActive) OptixSuccessGreen else OptixTextMuted)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(OptixOrange.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(staff.role, color = OptixOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("PIN: ${staff.pinMasked}", color = OptixTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            // Shift Status Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (staff.shiftStatus == "Active Shift") OptixSuccessGreen.copy(alpha = 0.2f) else OptixCardBorder
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = staff.shiftStatus,
                                    color = if (staff.shiftStatus == "Active Shift") OptixSuccessGreen else OptixTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Permission Chips
                        Text("Granted Permissions", color = OptixTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            staff.permissions.take(3).forEach { perm ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OptixSurface)
                                        .border(1.dp, OptixCardBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(perm, color = OptixTextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                            if (staff.permissions.size > 3) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OptixSurface)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+${staff.permissions.size - 3} more", color = OptixTextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
