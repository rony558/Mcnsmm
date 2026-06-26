package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SmmViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = SmmRepository(db)

    // Auth States
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.observeUser(email) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // User Data Flows
    val userOrders: StateFlow<List<OrderEntity>> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.observeUserOrders(email) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userDeposits: StateFlow<List<PendingDepositEntity>> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.observeUserDeposits(email) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRentals: StateFlow<List<ChildPanelRentalEntity>> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.observeUserRentals(email) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referredUsers: StateFlow<List<UserEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.observeReferredUsers(user.referralCode) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Data Flows
    val activeServices: StateFlow<List<SmmServiceEntity>> = repository.activeServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServicesForAdmin: StateFlow<List<SmmServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminDeposits: StateFlow<List<PendingDepositEntity>> = repository.allDeposits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminRentals: StateFlow<List<ChildPanelRentalEntity>> = repository.allRentals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminConfig: StateFlow<AdminConfigEntity?> = repository.adminConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Local UI control states
    private val _isSplashCompleted = MutableStateFlow(false)
    val isSplashCompleted: StateFlow<Boolean> = _isSplashCompleted.asStateFlow()

    private val _isPopupNoticeDismissed = MutableStateFlow(false)
    val isPopupNoticeDismissed: StateFlow<Boolean> = _isPopupNoticeDismissed.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // For local in-app push notification banner when order changes
    private val _inAppNotification = MutableStateFlow<String?>(null)
    val inAppNotification: StateFlow<String?> = _inAppNotification.asStateFlow()

    // SMM Provider API Live Tracking states
    private val _providerApiBalance = MutableStateFlow<SmmBalanceResponse?>(null)
    val providerApiBalance: StateFlow<SmmBalanceResponse?> = _providerApiBalance.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            fetchProviderApiBalance()
        }
        createNotificationChannel()
    }

    fun completeSplash() {
        _isSplashCompleted.value = true
    }

    fun dismissPopupNotice() {
        _isPopupNoticeDismissed.value = true
    }

    fun setAdminMode(active: Boolean) {
        _isAdminMode.value = active
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun clearInAppNotification() {
        _inAppNotification.value = null
    }

    // AUTH ACTION METHODS
    fun register(
        email: String,
        fullName: String,
        whatsappNumber: String,
        passwordHash: String,
        referredByCode: String?,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank() || fullName.isBlank() || whatsappNumber.isBlank() || passwordHash.isBlank()) {
            _uiMessage.value = "Please fill in all registration fields."
            return
        }
        if (passwordHash.length < 8) {
            _uiMessage.value = "Password must be at least 8 characters/digits."
            return
        }

        viewModelScope.launch {
            val result = repository.registerUser(
                email = email.trim(),
                fullName = fullName.trim(),
                whatsappNumber = whatsappNumber.trim(),
                passwordHash = passwordHash,
                referredByCode = referredByCode?.trim()
            )
            result.onSuccess {
                _currentUserEmail.value = it.email
                _isPopupNoticeDismissed.value = false // reset alert popup for new session
                _uiMessage.value = "Registered successfully! Wallet credited with ₹${if (it.referredBy != null) "50.0" else "0.0"} bonus."
                onSuccess()
            }.onFailure {
                _uiMessage.value = it.message ?: "Registration failed."
            }
        }
    }

    fun login(email: String, passwordHash: String, onSuccess: () -> Unit) {
        if (email.isBlank() || passwordHash.isBlank()) {
            _uiMessage.value = "Please enter email and password."
            return
        }
        viewModelScope.launch {
            val result = repository.loginUser(email.trim(), passwordHash)
            result.onSuccess {
                _currentUserEmail.value = it.email
                _isPopupNoticeDismissed.value = false // Show pop-up on new login
                _uiMessage.value = "Logged in successfully as ${it.fullName}."
                onSuccess()
            }.onFailure {
                _uiMessage.value = it.message ?: "Authentication failed."
            }
        }
    }

    fun registerOrLoginWithGoogle(email: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.registerWithGoogle(email, name)
            result.onSuccess {
                _currentUserEmail.value = it.email
                _isPopupNoticeDismissed.value = false
                _uiMessage.value = "Authenticated with Google as ${it.fullName}."
                onSuccess()
            }.onFailure {
                _uiMessage.value = it.message ?: "Google Auth failed."
            }
        }
    }

    fun logout() {
        _currentUserEmail.value = null
        _isAdminMode.value = false
        _uiMessage.value = "Logged out successfully."
    }

    // NEW ORDER FLOW
    fun placeOrder(serviceId: Int, targetLink: String, quantityString: String, onSuccess: () -> Unit) {
        val email = _currentUserEmail.value
        if (email == null) {
            _uiMessage.value = "Session expired. Please log in again."
            return
        }
        if (targetLink.isBlank()) {
            _uiMessage.value = "Please enter the target link."
            return
        }
        val quantity = quantityString.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            _uiMessage.value = "Please enter a valid positive quantity."
            return
        }

        viewModelScope.launch {
            val result = repository.placeNewOrder(email, serviceId, targetLink, quantity)
            result.onSuccess {
                _uiMessage.value = "Order placed successfully! ID: #${it.id}"
                onSuccess()
            }.onFailure {
                _uiMessage.value = it.message ?: "Order failed."
            }
        }
    }

    // ADD FUNDS / DEPOSIT
    fun submitDeposit(amountString: String, utrId: String, onSuccess: () -> Unit) {
        val email = _currentUserEmail.value
        if (email == null) {
            _uiMessage.value = "Session expired."
            return
        }
        val amount = amountString.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiMessage.value = "Please enter a valid deposit amount."
            return
        }
        if (utrId.length != 12 || !utrId.all { it.isDigit() }) {
            _uiMessage.value = "Error: UPI UTR must be an exact 12-digit number."
            return
        }

        viewModelScope.launch {
            val result = repository.requestDeposit(email, amount, utrId)
            result.onSuccess {
                _uiMessage.value = "UTR submitted for verification! Admin will approve shortly."
                onSuccess()
            }.onFailure {
                _uiMessage.value = it.message ?: "Deposit request failed."
            }
        }
    }

    // RENTAL SUBSCRIPTION
    fun rentChildPanel(customDomain: String, onSuccess: () -> Unit) {
        val email = _currentUserEmail.value
        if (email == null) {
            _uiMessage.value = "Session expired."
            return
        }
        if (customDomain.isBlank()) {
            _uiMessage.value = "Please enter a custom domain name."
            return
        }

        viewModelScope.launch {
            val result = repository.rentChildPanel(email, customDomain)
            result.onSuccess {
                _uiMessage.value = "Child panel requested! ৳399 deducted. Configuration pending."
                onSuccess()
            }.onFailure {
                _uiMessage.value = it.message ?: "Rental failed."
            }
        }
    }

    // ADMIN ACTIONS
    fun adminToggleService(id: Int, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleServiceStatus(id, isActive)
        }
    }

    fun adminUpdateServiceMarkup(id: Int, markup: Double) {
        viewModelScope.launch {
            repository.updateServiceMarkup(id, markup)
        }
    }

    fun adminUpdateGlobalConfig(globalMargin: Double, popupNoticeText: String, isPopupNoticeActive: Boolean, upiId: String) {
        viewModelScope.launch {
            repository.updateAdminConfig(globalMargin, popupNoticeText, isPopupNoticeActive, upiId)
            _uiMessage.value = "Admin configuration updated."
        }
    }

    fun adminApproveDeposit(utrId: String) {
        viewModelScope.launch {
            val success = repository.approveDeposit(utrId)
            if (success) {
                _uiMessage.value = "Deposit Approved successfully."
            } else {
                _uiMessage.value = "Failed to approve deposit."
            }
        }
    }

    fun adminRejectDeposit(utrId: String) {
        viewModelScope.launch {
            val success = repository.rejectDeposit(utrId)
            if (success) {
                _uiMessage.value = "Deposit Rejected."
            } else {
                _uiMessage.value = "Failed to reject deposit."
            }
        }
    }

    fun adminUpdateOrderStatus(orderId: Long, currentStatus: String, newStatus: String) {
        viewModelScope.launch {
            val success = repository.updateOrderStatus(orderId, newStatus)
            if (success) {
                _uiMessage.value = "Order status updated to $newStatus."
                
                // PUSH NOTIFICATION SIMULATION
                // When order status changes from 'In Progress' to 'Completed', send a system push notification!
                if (currentStatus == "In Progress" && newStatus == "Completed") {
                    triggerPushNotification(orderId)
                }
            } else {
                _uiMessage.value = "Failed to update order status."
            }
        }
    }

    fun adminUpdateRentalStatus(rentalId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateRentalStatus(rentalId, newStatus)
            _uiMessage.value = "Child panel rental status updated to $newStatus."
        }
    }

    // LOCAL SYSTEM PUSH NOTIFICATION
    private fun triggerPushNotification(orderId: Long) {
        val title = "Order #$orderId Completed! ✅"
        val message = "Your MCNSMM order has been successfully processed and marked as Completed. Check your dashboard."
        
        // Update in-app banner notifier
        _inAppNotification.value = "$title: $message"

        // Fire true system notification
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val builder = NotificationCompat.Builder(context, "order_updates")
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(orderId.toInt(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = getApplication<Application>().applicationContext
            val name = "Order Status Updates"
            val descriptionText = "Notifications sent when your order status changes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("order_updates", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Live SMM Provider integration triggers
    fun fetchProviderApiBalance() {
        viewModelScope.launch {
            repository.getProviderApiBalance()
                .onSuccess {
                    _providerApiBalance.value = it
                }
                .onFailure {
                    _providerApiBalance.value = SmmBalanceResponse(error = it.localizedMessage)
                }
        }
    }

    fun syncServicesWithMoreThanPanelApi() {
        _uiMessage.value = "Syncing services with MoreThanPanel API..."
        viewModelScope.launch {
            repository.syncServicesWithApi()
                .onSuccess {
                    _uiMessage.value = "Successfully synced services with MoreThanPanel!"
                    fetchProviderApiBalance() // also refresh balance
                }
                .onFailure {
                    _uiMessage.value = "Sync failed: ${it.message}"
                }
        }
    }
}

class SmmViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmmViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
