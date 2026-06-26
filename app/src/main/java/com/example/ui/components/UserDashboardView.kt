package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.SmmViewModel
import com.example.ui.theme.*

@Composable
fun UserDashboardView(
    viewModel: SmmViewModel,
    onBackToAuth: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isPopupNoticeDismissed by viewModel.isPopupNoticeDismissed.collectAsState()

    var activeTab by remember { mutableStateOf("order") } // "order", "orders", "funds", "rentals", "referrals"

    val uiMessage by viewModel.uiMessage.collectAsState()

    // Automatic Emergency Broadcast Popup Notice Check
    if (adminConfig?.isPopupNoticeActive == true && !isPopupNoticeDismissed) {
        Dialog(onDismissRequest = { viewModel.dismissPopupNotice() }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, CosmicIndigo, RoundedCornerShape(24.dp))
                    .testTag("emergency_notice_popup")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(CosmicAmber.copy(alpha = 0.2f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Alert",
                            tint = CosmicAmber,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "EMERGENCY BROADCAST",
                        style = MaterialTheme.typography.titleMedium,
                        color = CosmicAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = adminConfig?.popupNoticeText ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate100,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.dismissPopupNotice() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_popup_button")
                    ) {
                        Text("OK / DISMISS", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Slate800,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = activeTab == "order",
                    onClick = { activeTab = "order" },
                    icon = { Icon(Icons.Default.AddShoppingCart, contentDescription = "Order") },
                    label = { Text("New Order") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate700
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "orders",
                    onClick = { activeTab = "orders" },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("My Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate700
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "funds",
                    onClick = { activeTab = "funds" },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Funds") },
                    label = { Text("Add Funds") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate700
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "rentals",
                    onClick = { activeTab = "rentals" },
                    icon = { Icon(Icons.Default.Dns, contentDescription = "Rentals") },
                    label = { Text("Rent") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate700
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "referrals",
                    onClick = { activeTab = "referrals" },
                    icon = { Icon(Icons.Default.GroupAdd, contentDescription = "Referrals") },
                    label = { Text("Refer") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate700
                    )
                )
            }
        },
        containerColor = Slate900
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate800)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = user?.fullName ?: "Guest User",
                        style = MaterialTheme.typography.titleLarge,
                        color = Slate100,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CosmicEmerald, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Account: ${user?.accountStatus ?: "Active"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CosmicEmerald,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Wallet Balance
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate700),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = CosmicEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${String.format("%.2f", user?.walletBalance ?: 0.0)}",
                                color = Slate100,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.testTag("dashboard_wallet_balance")
                            )
                        }
                    }

                    // Admin/User Switch & Log Out Menu
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Slate100)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Slate800)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Toggle Admin Panel", color = Slate100) },
                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = CosmicBlue) },
                                onClick = {
                                    showMenu = false
                                    viewModel.setAdminMode(true)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Log Out", color = CosmicCoral) },
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = "Logout", tint = CosmicCoral) },
                                onClick = {
                                    showMenu = false
                                    viewModel.logout()
                                    onBackToAuth()
                                }
                            )
                        }
                    }
                }
            }

            // SnackBar message simulation
            if (uiMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicIndigo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate100,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearUiMessage() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Slate100, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Render active tab content
            when (activeTab) {
                "order" -> NewOrderTab(viewModel = viewModel, userEmail = user?.email ?: "")
                "orders" -> OrdersHistoryTab(viewModel = viewModel)
                "funds" -> AddFundsTab(viewModel = viewModel, upiId = adminConfig?.upiId ?: "mcnsmm@upi")
                "rentals" -> ChildPanelRentTab(viewModel = viewModel, walletBalance = user?.walletBalance ?: 0.0)
                "referrals" -> ReferralsTab(viewModel = viewModel, user = user)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderTab(viewModel: SmmViewModel, userEmail: String) {
    val activeServices by viewModel.activeServices.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val referredUsers by viewModel.referredUsers.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var selectedService by remember { mutableStateOf<SmmServiceEntity?>(null) }
    var targetLink by remember { mutableStateOf("") }
    var quantityString by remember { mutableStateOf("") }

    var expandedDropdown by remember { mutableStateOf(false) }

    // Side calculations
    val finalPricePerThousand = if (selectedService != null && adminConfig != null) {
        val margin = if (selectedService!!.individualMarkup > 0.0) selectedService!!.individualMarkup else adminConfig!!.globalMargin
        selectedService!!.basePrice + (selectedService!!.basePrice * (margin / 100.0))
    } else 0.0

    val quantity = quantityString.toIntOrNull() ?: 0
    val totalCharge = if (quantity > 0) (finalPricePerThousand * quantity) / 1000.0 else 0.0

    // Set default selected service once loaded
    if (selectedService == null && activeServices.isNotEmpty()) {
        selectedService = activeServices.first()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReferralBonusDashboardCard(referredUsers = referredUsers, referralCode = user?.referralCode)
        }

        item {
            Text(
                text = "Place New Social Order",
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Service Dropdown Selector
                    Text(
                        text = "SELECT SMM SERVICE",
                        style = MaterialTheme.typography.bodySmall,
                        color = CosmicIndigo,
                        fontWeight = FontWeight.Bold
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedService?.name ?: "Loading premium services...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100,
                                focusedBorderColor = CosmicBlue,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("order_service_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.background(Slate800)
                        ) {
                            activeServices.forEach { service ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(service.name, color = Slate100, fontWeight = FontWeight.Bold)
                                            val tempMargin = if (service.individualMarkup > 0.0) service.individualMarkup else (adminConfig?.globalMargin ?: 10.0)
                                            val calculatedRate = service.basePrice + (service.basePrice * (tempMargin / 100.0))
                                            Text("Category: ${service.category} | ₹${String.format("%.2f", calculatedRate)} per 1k", color = Slate400, fontSize = 12.sp)
                                        }
                                    },
                                    onClick = {
                                        selectedService = service
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Target Link Input
                    Text(
                        text = "TARGET SOCIAL MEDIA LINK",
                        style = MaterialTheme.typography.bodySmall,
                        color = CosmicIndigo,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = targetLink,
                        onValueChange = { targetLink = it },
                        placeholder = { Text("e.g., https://instagram.com/myprofile") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_target_link_input")
                    )

                    // Quantity Input
                    Text(
                        text = "QUANTITY (Minimum 100)",
                        style = MaterialTheme.typography.bodySmall,
                        color = CosmicIndigo,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = quantityString,
                        onValueChange = { quantityString = it },
                        placeholder = { Text("e.g. 1000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_quantity_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calculation Panel
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Service Base Rate:", color = Slate400, fontSize = 13.sp)
                                Text("₹${selectedService?.basePrice ?: 0.0} / 1000", color = Slate300, fontSize = 13.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Active markup applied:", color = Slate400, fontSize = 13.sp)
                                val currentMarkup = if (selectedService?.individualMarkup ?: 0.0 > 0.0) selectedService!!.individualMarkup else (adminConfig?.globalMargin ?: 10.0)
                                Text("$currentMarkup%", color = CosmicEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Final Rate per 1000:", color = Slate400, fontSize = 13.sp)
                                Text("₹${String.format("%.2f", finalPricePerThousand)}", color = Slate100, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = Slate700, modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL CHARGE:", color = Slate100, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = "₹${String.format("%.2f", totalCharge)}",
                                    color = CosmicEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.testTag("order_total_charge_display")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Order Button
                    Button(
                        onClick = {
                            if (selectedService == null) return@Button
                            viewModel.placeOrder(
                                serviceId = selectedService!!.id,
                                targetLink = targetLink,
                                quantityString = quantityString,
                                onSuccess = {
                                    targetLink = ""
                                    quantityString = ""
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_order_button")
                    ) {
                        Text("PLACE SECURE ORDER NOW", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersHistoryTab(viewModel: SmmViewModel) {
    val orders by viewModel.userOrders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your SMM Order History",
            style = MaterialTheme.typography.titleMedium,
            color = Slate100,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inbox, contentDescription = "Empty", tint = Slate400, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No orders placed yet.", color = Slate400, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders) { order ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.id}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate300,
                                    fontWeight = FontWeight.Bold
                                )

                                val (statusColor, bg) = when (order.status) {
                                    "Completed" -> Pair(CosmicEmerald, CosmicEmerald.copy(alpha = 0.15f))
                                    "In Progress" -> Pair(CosmicBlue, CosmicBlue.copy(alpha = 0.15f))
                                    else -> Pair(CosmicAmber, CosmicAmber.copy(alpha = 0.15f))
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = bg),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = order.serviceName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Slate100,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Link: ${order.targetLink}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("QUANTITY", color = Slate400, fontSize = 11.sp)
                                    Text("${order.quantity} units", color = Slate100, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("CHARGE", color = Slate400, fontSize = 11.sp)
                                    Text("₹${String.format("%.2f", order.totalCharge)}", color = CosmicEmerald, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddFundsTab(viewModel: SmmViewModel, upiId: String) {
    var amountString by remember { mutableStateOf("") }
    var utrId by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val deposits by viewModel.userDeposits.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Add Funds securely via UPI",
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        }

        // UPI Pay Card (displays UPI ID and a beautiful interactive custom QR code vector)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan QR or click UPI to Pay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate300,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Draw an active, beautiful, vector mockup QR Code using Canvas!
                    Canvas(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        val size1 = size.width
                        val boxSize = size1 / 8f
                        
                        // QR Code Outer Position Detectors
                        // Top Left box
                        drawRect(Color.Black, Offset(0f, 0f), Size(boxSize * 3f, boxSize * 3f))
                        drawRect(Color.White, Offset(boxSize, boxSize), Size(boxSize, boxSize))
                        
                        // Top Right box
                        drawRect(Color.Black, Offset(boxSize * 5f, 0f), Size(boxSize * 3f, boxSize * 3f))
                        drawRect(Color.White, Offset(boxSize * 6f, boxSize), Size(boxSize, boxSize))

                        // Bottom Left box
                        drawRect(Color.Black, Offset(0f, boxSize * 5f), Size(boxSize * 3f, boxSize * 3f))
                        drawRect(Color.White, Offset(boxSize, boxSize * 6f), Size(boxSize, boxSize))

                        // Some mock random matrix dots for the rest
                        for (i in 0..7) {
                            for (j in 0..7) {
                                if ((i in 0..2 && j in 0..2) || (i in 5..7 && j in 0..2) || (i in 0..2 && j in 5..7)) {
                                    continue
                                }
                                if ((i * j + (i + j) % 2) % 3 == 0) {
                                    drawRect(
                                        Color.Black,
                                        Offset(i * boxSize, j * boxSize),
                                        Size(boxSize, boxSize)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate900)
                            .clickable { clipboardManager.setText(AnnotatedString(upiId)) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(upiId, color = CosmicBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CosmicBlue, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Scan QR using PhonePe, GPay or Paytm, complete the transaction, then paste your 12-digit UTR ID below.",
                        color = Slate400,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // UTR Verification Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("VERIFY TRANSACTION & CREDITS", style = MaterialTheme.typography.bodySmall, color = CosmicIndigo, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = amountString,
                        onValueChange = { amountString = it },
                        label = { Text("Amount Paid (₹)") },
                        placeholder = { Text("e.g. 500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deposit_amount_input")
                    )

                    OutlinedTextField(
                        value = utrId,
                        onValueChange = { if (it.length <= 12 && it.all { char -> char.isDigit() }) utrId = it },
                        label = { Text("12-Digit UTR ID / Reference Number") },
                        placeholder = { Text("Enter exact 12 digits") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deposit_utr_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.submitDeposit(amountString, utrId) {
                                amountString = ""
                                utrId = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_deposit_button")
                    ) {
                        Text("SUBMIT TRANSACTION VERIFICATION", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Deposit History
        item {
            Text("Your Verification Requests", style = MaterialTheme.typography.titleSmall, color = Slate100, fontWeight = FontWeight.Bold)
        }

        if (deposits.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No deposit requests submitted yet.", color = Slate400, fontSize = 13.sp)
                }
            }
        } else {
            items(deposits) { dep ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("UTR: ${dep.utrId}", color = Slate100, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Submitted: ${java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(dep.timestamp))}", color = Slate400, fontSize = 11.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${String.format("%.2f", dep.amount)}", color = CosmicEmerald, fontWeight = FontWeight.Bold)
                            
                            val statusColor = when (dep.status) {
                                "Approved" -> CosmicEmerald
                                "Rejected" -> CosmicCoral
                                else -> CosmicAmber
                            }
                            Text(dep.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildPanelRentTab(viewModel: SmmViewModel, walletBalance: Double) {
    var domainName by remember { mutableStateOf("") }
    val rentals by viewModel.userRentals.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Child Panel Subscription",
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        }

        // Benefits explanation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Why Rent a Child SMM Panel?",
                        color = CosmicIndigo,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    val benefits = listOf(
                        "✅ Complete independent SMM website on your own Domain",
                        "✅ Automatically imports and syncs our 15-20 premium services",
                        "✅ Set your own custom profit margin ratios to resell",
                        "✅ Full panel control dashboard with user management keys",
                        "✅ Fast automated server setup in less than 2 hours"
                    )

                    benefits.forEach { benefit ->
                        Text(benefit, color = Slate100, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Subscription Price:", color = Slate400, fontSize = 13.sp)
                        Text("৳399 / Month", color = CosmicEmerald, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Order input field
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("REQUEST NEW SUBSCRIPTION", style = MaterialTheme.typography.bodySmall, color = CosmicIndigo, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = domainName,
                        onValueChange = { domainName = it },
                        label = { Text("Your Custom Domain Name") },
                        placeholder = { Text("e.g. www.mysmmpanel.com") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rental_domain_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.rentChildPanel(domainName) {
                                domainName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("rent_now_button")
                    ) {
                        Text("PAY ৳399 & RENT NOW", fontWeight = FontWeight.Bold, color = Slate900, fontSize = 14.sp)
                    }
                }
            }
        }

        // Historic requests
        item {
            Text("Your Rented Panels", style = MaterialTheme.typography.titleSmall, color = Slate100, fontWeight = FontWeight.Bold)
        }

        if (rentals.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No rented child panels requested yet.", color = Slate400, fontSize = 13.sp)
                }
            }
        } else {
            items(rentals) { rent ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rent.customDomain, color = Slate100, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            
                            val badgeColor = when (rent.status) {
                                "Active" -> CosmicEmerald
                                "Suspended" -> CosmicCoral
                                "Expired" -> Slate400
                                else -> CosmicAmber
                            }
                            Card(colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.15f))) {
                                Text(
                                    text = rent.status,
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Rental Date", color = Slate400, fontSize = 11.sp)
                                Text(java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(rent.rentalDate)), color = Slate300, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Expiry Date", color = Slate400, fontSize = 11.sp)
                                Text(java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(rent.expiryDate)), color = Slate300, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralsTab(viewModel: SmmViewModel, user: UserEntity?) {
    val referredUsers by viewModel.referredUsers.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Referral Bonus Program",
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        }

        // Referral Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(CosmicIndigo.copy(alpha = 0.2f), RoundedCornerShape(32.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Referral Gift", tint = CosmicIndigo, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Invite friends to SMM Panel",
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate100,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Both you and your referred friend will receive ₹50.0 bonus directly into your wallets immediately when they register using your code!",
                        color = Slate400,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "YOUR REFERRAL CODE",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate900)
                            .clickable { clipboardManager.setText(AnnotatedString(user?.referralCode ?: "")) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user?.referralCode ?: "LOADING",
                            color = CosmicEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            modifier = Modifier.testTag("referral_code_display")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CosmicEmerald, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Tracking stats
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Successful Referrals", color = Slate400, fontSize = 12.sp)
                        Text("${referredUsers.size} friends registered", color = Slate100, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.testTag("successful_referrals_count"))
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicEmerald.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "Earned: ₹${String.format("%.2f", referredUsers.size * 50.0)}",
                            color = CosmicEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // List of referred users
        item {
            Text(
                text = "Track referred friends",
                style = MaterialTheme.typography.titleSmall,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        }

        if (referredUsers.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No referrals yet. Invite friends to start earning!", color = Slate400, fontSize = 13.sp)
                }
            }
        } else {
            items(referredUsers) { friend ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(friend.fullName, color = Slate100, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(friend.email, color = Slate400, fontSize = 11.sp)
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = CosmicEmerald.copy(alpha = 0.15f))) {
                            Text(
                                text = "Verified +₹50.0",
                                color = CosmicEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralBonusDashboardCard(
    referredUsers: List<UserEntity>,
    referralCode: String?
) {
    var isExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CosmicIndigo.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("referral_bonus_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CosmicIndigo.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Referrals",
                            tint = CosmicBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Referral Program & Earnings",
                            style = MaterialTheme.typography.titleSmall,
                            color = Slate100,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Invite friends, both get ₹50.0 bonus!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }

                // Total Bonus Display
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicEmerald.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Earned: ₹${String.format("%.2f", referredUsers.size * 50.0)}",
                        color = CosmicEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "YOUR REFERRAL CODE",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate800)
                            .clickable {
                                if (referralCode != null) {
                                    clipboardManager.setText(AnnotatedString(referralCode))
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = referralCode ?: "N/A",
                            color = CosmicEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = CosmicEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SUCCESSFUL REFERRALS",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${referredUsers.size} Friends Joined",
                        color = Slate100,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Expandable Success Referrals List trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide Referred Friends List" else "View Referred Friends List (${referredUsers.size})",
                    color = CosmicBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle list",
                    tint = CosmicBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isExpanded) {
                if (referredUsers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No successful referrals yet. Share your code to start earning!",
                            color = Slate400,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        referredUsers.forEach { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate900, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = friend.fullName,
                                        color = Slate100,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = friend.email,
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CosmicEmerald.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+₹50.00",
                                        color = CosmicEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
