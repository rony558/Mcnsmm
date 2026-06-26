package com.example.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SmmViewModel
import com.example.ui.theme.*

@Composable
fun AdminPanelView(
    viewModel: SmmViewModel,
    onBackToUserDashboard: () -> Unit
) {
    var adminTab by remember { mutableStateOf("config") } // "config", "services", "deposits", "orders", "rentals"
    val uiMessage by viewModel.uiMessage.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate800)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackToUserDashboard) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate100)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MCNSMM Admin Console",
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate100,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicIndigo),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "SYSTEM ADMIN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Slate800,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = adminTab == "config",
                    onClick = { adminTab = "config" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                    label = { Text("App Config") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        indicatorColor = Slate700,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
                NavigationBarItem(
                    selected = adminTab == "services",
                    onClick = { adminTab = "services" },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Services") },
                    label = { Text("Services") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        indicatorColor = Slate700,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
                NavigationBarItem(
                    selected = adminTab == "deposits",
                    onClick = { adminTab = "deposits" },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Deposits") },
                    label = { Text("Deposits") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        indicatorColor = Slate700,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
                NavigationBarItem(
                    selected = adminTab == "orders",
                    onClick = { adminTab = "orders" },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Orders") },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        indicatorColor = Slate700,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
                NavigationBarItem(
                    selected = adminTab == "rentals",
                    onClick = { adminTab = "rentals" },
                    icon = { Icon(Icons.Default.Dns, contentDescription = "Rentals") },
                    label = { Text("Rentals") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicBlue,
                        selectedTextColor = CosmicBlue,
                        indicatorColor = Slate700,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
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
            // Toast / SnackBar Banner
            if (uiMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicIndigo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(uiMessage ?: "", color = Slate100, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearUiMessage() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate100, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            when (adminTab) {
                "config" -> AdminConfigTab(viewModel)
                "services" -> AdminServicesTab(viewModel)
                "deposits" -> AdminDepositsTab(viewModel)
                "orders" -> AdminOrdersTab(viewModel)
                "rentals" -> AdminRentalsTab(viewModel)
            }
        }
    }
}

@Composable
fun AdminConfigTab(viewModel: SmmViewModel) {
    val config by viewModel.adminConfig.collectAsState()

    var globalMarginString by remember { mutableStateOf("") }
    var popupText by remember { mutableStateOf("") }
    var popupActive by remember { mutableStateOf(false) }
    var upiIdString by remember { mutableStateOf("") }

    LaunchedEffect(config) {
        config?.let {
            globalMarginString = it.globalMargin.toString()
            popupText = it.popupNoticeText
            popupActive = it.isPopupNoticeActive
            upiIdString = it.upiId
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Emergency Notice & App Settings", style = MaterialTheme.typography.titleMedium, color = Slate100, fontWeight = FontWeight.Bold)
        }

        item {
            val providerBalance by viewModel.providerApiBalance.collectAsState()
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicIndigo.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("admin_smm_provider_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("MORETHANPANEL SMM PROVIDER API", style = MaterialTheme.typography.bodySmall, color = CosmicBlue, fontWeight = FontWeight.Bold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("API Endpoint", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            Text("morethanpanel.com/api/v2", style = MaterialTheme.typography.bodyMedium, color = Slate100, fontWeight = FontWeight.Bold)
                        }
                        
                        val isSuccess = providerBalance != null && providerBalance?.error == null
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSuccess) CosmicEmerald.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (isSuccess) "CONNECTED" else "DISCONNECTED",
                                color = if (isSuccess) CosmicEmerald else Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Slate700))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Provider API Wallet Balance", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            if (providerBalance?.error != null) {
                                Text("Error: ${providerBalance?.error}", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                            } else {
                                val bal = providerBalance?.balance ?: "..."
                                val cur = providerBalance?.currency ?: "USD"
                                Text("$bal $cur", style = MaterialTheme.typography.titleMedium, color = CosmicEmerald, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        IconButton(onClick = { viewModel.fetchProviderApiBalance() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Balance", tint = CosmicBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.syncServicesWithMoreThanPanelApi()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_sync_services_button")
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SYNC SERVICES WITH PROVIDER API", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("GLOBAL CALCULATIONS", style = MaterialTheme.typography.bodySmall, color = CosmicIndigo, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = globalMarginString,
                        onValueChange = { globalMarginString = it },
                        label = { Text("Global Profit Margin (%)") },
                        placeholder = { Text("e.g., 10.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate100, unfocusedTextColor = Slate300, focusedBorderColor = CosmicBlue, unfocusedBorderColor = Slate700),
                        modifier = Modifier.fillMaxWidth().testTag("admin_global_margin_input")
                    )

                    OutlinedTextField(
                        value = upiIdString,
                        onValueChange = { upiIdString = it },
                        label = { Text("Admin Payment UPI ID") },
                        placeholder = { Text("mcnsmm@upi") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate100, unfocusedTextColor = Slate300, focusedBorderColor = CosmicBlue, unfocusedBorderColor = Slate700),
                        modifier = Modifier.fillMaxWidth().testTag("admin_upi_id_input")
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("EMERGENCY POPUP BROADCAST", style = MaterialTheme.typography.bodySmall, color = CosmicIndigo, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = popupActive,
                            onCheckedChange = { popupActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CosmicBlue, checkedTrackColor = CosmicBlue.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("admin_popup_toggle")
                        )
                    }

                    OutlinedTextField(
                        value = popupText,
                        onValueChange = { popupText = it },
                        label = { Text("Popup Broadcast Notice Message") },
                        placeholder = { Text("Notice text displayed instantly on home login...") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate100, unfocusedTextColor = Slate300, focusedBorderColor = CosmicBlue, unfocusedBorderColor = Slate700),
                        modifier = Modifier.fillMaxWidth().testTag("admin_popup_text_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val margin = globalMarginString.toDoubleOrNull() ?: 10.0
                            viewModel.adminUpdateGlobalConfig(margin, popupText, popupActive, upiIdString)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_save_config_button")
                    ) {
                        Text("UPDATE GLOBAL SYSTEM SETTINGS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminServicesTab(viewModel: SmmViewModel) {
    val services by viewModel.allServicesForAdmin.collectAsState()
    val config by viewModel.adminConfig.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Admin SMM Services Controller", style = MaterialTheme.typography.titleMedium, color = Slate100, fontWeight = FontWeight.Bold)
            Text("Configure app pricing markups and toggles.", color = Slate400, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
        }

        items(services) { service ->
            var overrideMarkupString by remember { mutableStateOf(service.individualMarkup.toString()) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.name, color = Slate100, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Category: ${service.category} | Base Price: ₹${service.basePrice}/1k", color = Slate400, fontSize = 12.sp)
                        }
                        
                        Switch(
                            checked = service.isActive,
                            onCheckedChange = { viewModel.adminToggleService(service.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CosmicEmerald, checkedTrackColor = CosmicEmerald.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Individual override markup
                        OutlinedTextField(
                            value = overrideMarkupString,
                            onValueChange = { overrideMarkupString = it },
                            label = { Text("Override Markup (%)") },
                            placeholder = { Text("0.0 = Use Global") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate100, unfocusedTextColor = Slate300, focusedBorderColor = CosmicBlue, unfocusedBorderColor = Slate700),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                val markup = overrideMarkupString.toDoubleOrNull() ?: 0.0
                                viewModel.adminUpdateServiceMarkup(service.id, markup)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicIndigo),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Save Override")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Final Calculated Price Demo Card
                    val activeMarkup = if (service.individualMarkup > 0.0) service.individualMarkup else (config?.globalMargin ?: 10.0)
                    val finalPrice = service.basePrice + (service.basePrice * (activeMarkup / 100.0))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate900, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Markup: $activeMarkup%", color = Slate400, fontSize = 12.sp)
                        Text("User Final Rate: ₹${String.format("%.2f", finalPrice)}/1k", color = CosmicEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDepositsTab(viewModel: SmmViewModel) {
    val deposits by viewModel.adminDeposits.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("UPI Deposit requests manager", style = MaterialTheme.typography.titleMedium, color = Slate100, fontWeight = FontWeight.Bold)
        }

        // THIRD-PARTY UPI AUTOMATION VERIFICATION OUTLINE INFO CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicIndigo.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(1.dp, CosmicIndigo, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("💡 Automated UPI verification Architecture", color = CosmicIndigo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To achieve 100% automated deposits verification, implement a webhook/polling service with a third-party gateway (e.g. UPI Gateway, Easebuzz, or Razorpay Pay-by-UPI intent API). Upon user submission, the backend triggers an automated GET request:\n" +
                               "GET /api/v1/verify_upi?utr={utrId}&amount={amount}\n" +
                               "If verified = true, the server instantly executes the Room DAO user's wallet increment, eliminating manual approval delays.",
                        color = Slate300,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        if (deposits.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No deposit verification requests.", color = Slate400)
                }
            }
        } else {
            items(deposits) { dep ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("User: ${dep.userEmail}", color = Slate300, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            
                            val statusColor = when (dep.status) {
                                "Approved" -> CosmicEmerald
                                "Rejected" -> CosmicCoral
                                else -> CosmicAmber
                            }
                            Text(dep.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("UTR REFERENCE ID", color = Slate400, fontSize = 11.sp)
                                Text(dep.utrId, color = Slate100, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("AMOUNT PAID", color = Slate400, fontSize = 11.sp)
                                Text("₹${dep.amount}", color = CosmicEmerald, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }

                        if (dep.status == "Pending") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.adminApproveDeposit(dep.utrId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CosmicEmerald),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("approve_deposit_${dep.utrId}")
                                ) {
                                    Text("APPROVE", color = Slate900, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.adminRejectDeposit(dep.utrId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCoral),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("reject_deposit_${dep.utrId}")
                                ) {
                                    Text("REJECT", color = Color.White, fontWeight = FontWeight.Bold)
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
fun AdminOrdersTab(viewModel: SmmViewModel) {
    val orders by viewModel.adminOrders.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("SMM Order Status Manager", style = MaterialTheme.typography.titleMedium, color = Slate100, fontWeight = FontWeight.Bold)
            Text("Simulate order tracking status updates below. Setting an order from 'In Progress' to 'Completed' triggers local Push Notifications!", color = Slate400, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
        }

        if (orders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No customer orders placed yet.", color = Slate400)
                }
            }
        } else {
            items(orders) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("OrderID: #${order.id}", color = Slate300, fontWeight = FontWeight.Bold)
                            Text("Customer: ${order.userEmail}", color = Slate400, fontSize = 11.sp)
                        }

                        Text(order.serviceName, color = Slate100, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Link: ${order.targetLink}", color = CosmicBlue, fontSize = 12.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Quantity: ${order.quantity}", color = Slate300, fontSize = 12.sp)
                            Text("Charge: ₹${order.totalCharge}", color = CosmicEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        HorizontalDivider(color = Slate700)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current: ${order.status}", color = CosmicAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (order.status == "Pending") {
                                    Button(
                                        onClick = { viewModel.adminUpdateOrderStatus(order.id, order.status, "In Progress") },
                                        colors = ButtonDefaults.buttonColors(containerColor = CosmicBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("START IN PROGRESS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (order.status == "In Progress") {
                                    Button(
                                        onClick = { viewModel.adminUpdateOrderStatus(order.id, order.status, "Completed") },
                                        colors = ButtonDefaults.buttonColors(containerColor = CosmicEmerald),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp).testTag("admin_complete_order_${order.id}")
                                    ) {
                                        Text("COMPLETE ORDER ✅", fontSize = 10.sp, color = Slate900, fontWeight = FontWeight.Bold)
                                    }
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
fun AdminRentalsTab(viewModel: SmmViewModel) {
    val rentals by viewModel.adminRentals.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Child Panel Rental controller", style = MaterialTheme.typography.titleMedium, color = Slate100, fontWeight = FontWeight.Bold)
        }

        // AUTOMATION OUTLINE FOR CRON AUTO-RENEWAL DEDUCTION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicIndigo.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(1.dp, CosmicIndigo, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("⏰ Cron Job Auto-Renewal Architecture Outline", color = CosmicIndigo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To handle automatic monthly renewal and balance deduction, configure a server-side cron scheduler (such as node-cron, WorkManager, or Cloud Scheduler triggering an API endpoint every 24 hours):\n" +
                               "1. Query database for active rentals whose (expiryDate - System.currentTimeMillis() <= 0L).\n" +
                               "2. Attempt to deduct 399 BDT/INR from the corresponding user's wallet.\n" +
                               "3. If successful, extend the rental expiryDate by 30 days.\n" +
                               "4. If wallet balance is insufficient, suspend the rental state immediately and send a notification.",
                        color = Slate300,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        if (rentals.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No child panel rentals requested.", color = Slate400)
                }
            }
        } else {
            items(rentals) { rent ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Domain: ${rent.customDomain}", color = Slate100, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("User: ${rent.userEmail}", color = Slate400, fontSize = 11.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rental Date: " + java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(rent.rentalDate)), color = Slate300, fontSize = 12.sp)
                            Text("Expiry Date: " + java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(rent.expiryDate)), color = Slate300, fontSize = 12.sp)
                        }

                        Text("Status: " + rent.status, color = CosmicAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        HorizontalDivider(color = Slate700)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.adminUpdateRentalStatus(rent.id, "Active") },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ACTIVATE", color = Slate900, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.adminUpdateRentalStatus(rent.id, "Suspended") },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicCoral),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("SUSPEND", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.adminUpdateRentalStatus(rent.id, "Expired") },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("EXPIRE", color = Slate100, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
