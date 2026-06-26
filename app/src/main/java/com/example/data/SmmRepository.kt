package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class SmmRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val smmServiceDao = db.smmServiceDao()
    private val orderDao = db.orderDao()
    private val pendingDepositDao = db.pendingDepositDao()
    private val childPanelRentalDao = db.childPanelRentalDao()
    private val adminConfigDao = db.adminConfigDao()

    // Observables
    val activeServices: Flow<List<SmmServiceEntity>> = smmServiceDao.observeActiveServices()
    val allServices: Flow<List<SmmServiceEntity>> = smmServiceDao.observeAllServices()
    val allOrders: Flow<List<OrderEntity>> = orderDao.observeAllOrders()
    val allDeposits: Flow<List<PendingDepositEntity>> = pendingDepositDao.observeAllDeposits()
    val allRentals: Flow<List<ChildPanelRentalEntity>> = childPanelRentalDao.observeAllRentals()
    val adminConfig: Flow<AdminConfigEntity?> = adminConfigDao.observeConfig()

    fun observeUser(email: String): Flow<UserEntity?> = userDao.observeUserByEmail(email)
    fun observeUserOrders(email: String): Flow<List<OrderEntity>> = orderDao.observeUserOrders(email)
    fun observeUserDeposits(email: String): Flow<List<PendingDepositEntity>> = pendingDepositDao.observeUserDeposits(email)
    fun observeUserRentals(email: String): Flow<List<ChildPanelRentalEntity>> = childPanelRentalDao.observeUserRentals(email)
    fun observeReferredUsers(referralCode: String): Flow<List<UserEntity>> = userDao.getReferredUsers(referralCode)

    suspend fun initializeDatabaseIfNeeded() {
        // Init Admin Config if not exists
        val currentConfig = adminConfigDao.getConfig()
        if (currentConfig == null) {
            adminConfigDao.insertOrUpdateConfig(
                AdminConfigEntity(
                    id = 1,
                    globalMargin = 10.0,
                    popupNoticeText = "🎉 Welcome to MCNSMM SMM Panel! Use the referral program to earn ₹50 bonus. Contact Support at +91 98765 43210 for queries.",
                    isPopupNoticeActive = true,
                    upiId = "mcnsmm@upi"
                )
            )
        }

        // Init default services if empty
        val servicesList = smmServiceDao.observeAllServices().firstOrNull() ?: emptyList()
        if (servicesList.isEmpty()) {
            val initialServices = listOf(
                SmmServiceEntity(1, "Instagram Followers (Real & Guaranteed)", "Instagram", 80.0, true, 0.0),
                SmmServiceEntity(2, "Instagram Likes (Super Instant)", "Instagram", 25.0, true, 0.0),
                SmmServiceEntity(3, "Instagram Video/Reels Views (Fast)", "Instagram", 10.0, true, 0.0),
                SmmServiceEntity(4, "Facebook Page Followers + Likes", "Facebook", 150.0, true, 0.0),
                SmmServiceEntity(5, "Facebook Post Likes (Active Users)", "Facebook", 35.0, true, 0.0),
                SmmServiceEntity(6, "YouTube Subscribers (No-Drop Premium)", "YouTube", 800.0, true, 0.0),
                SmmServiceEntity(7, "YouTube Watch Time (Guaranteed 4000h)", "YouTube", 1200.0, true, 0.0),
                SmmServiceEntity(8, "YouTube Real High-Retention Views", "YouTube", 180.0, true, 0.0),
                SmmServiceEntity(9, "TikTok Followers (Instant Start)", "TikTok", 110.0, true, 0.0),
                SmmServiceEntity(10, "TikTok Real Video Likes", "TikTok", 45.0, true, 0.0),
                SmmServiceEntity(11, "Telegram Channel Members (Silent)", "Telegram", 90.0, true, 0.0),
                SmmServiceEntity(12, "Twitter / X Followers (Organic Mix)", "Twitter/X", 250.0, true, 0.0),
                SmmServiceEntity(13, "Twitter / X High Quality Likes", "Twitter/X", 70.0, true, 0.0),
                SmmServiceEntity(14, "LinkedIn Company Page Followers", "LinkedIn", 450.0, true, 0.0),
                SmmServiceEntity(15, "Spotify Premium Album Plays", "Spotify", 120.0, true, 0.0),
                SmmServiceEntity(16, "Website Traffic (Direct Global)", "Website", 50.0, true, 0.0),
                SmmServiceEntity(17, "Threads App Followers (Instant)", "Threads", 60.0, true, 0.0),
                SmmServiceEntity(18, "WhatsApp Channel Members (Active)", "WhatsApp", 300.0, true, 0.0)
            )
            smmServiceDao.insertServices(initialServices)
        }

        // Try to perform an initial sync of real SMM services in background
        try {
            syncServicesWithApi()
        } catch (e: Exception) {
            // Safe fallback if offline or API credentials are not resolved at this specific second
        }
    }

    // AUTH ACTIONS
    suspend fun registerUser(
        email: String,
        fullName: String,
        whatsappNumber: String,
        passwordHash: String,
        referredByCode: String?
    ): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("Email is already registered!"))
        }

        // Generate clean referral code
        val cleanName = fullName.replace("\\s".toRegex(), "").take(4).uppercase()
        val randomSuffix = (1000..9999).random().toString()
        val referralCode = "$cleanName$randomSuffix"

        var referredByUser: UserEntity? = null
        if (!referredByCode.isNullOrBlank()) {
            referredByUser = userDao.getUserByReferralCode(referredByCode.trim().uppercase())
            if (referredByUser == null) {
                return Result.failure(Exception("Invalid referral code!"))
            }
        }

        // Standard referral reward: ₹50 for both!
        val bonus = 50.0
        val initialBalance = if (referredByUser != null) bonus else 0.0

        val newUser = UserEntity(
            email = email,
            fullName = fullName,
            whatsappNumber = whatsappNumber,
            passwordHash = passwordHash,
            walletBalance = initialBalance,
            referralCode = referralCode,
            referredBy = referredByUser?.email,
            accountStatus = "Active"
        )

        userDao.insertUser(newUser)

        // Give referrer reward if applied
        if (referredByUser != null) {
            val updatedReferrerBalance = referredByUser.walletBalance + bonus
            userDao.updateWalletBalance(referredByUser.email, updatedReferrerBalance)
        }

        return Result.success(newUser)
    }

    suspend fun loginUser(email: String, passwordHash: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email) ?: return Result.failure(Exception("User not found!"))
        if (user.passwordHash != passwordHash) {
            return Result.failure(Exception("Incorrect password!"))
        }
        if (user.accountStatus == "Suspended") {
            return Result.failure(Exception("Your account has been suspended! Please contact support."))
        }
        return Result.success(user)
    }

    suspend fun registerWithGoogle(email: String, name: String): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            if (existing.accountStatus == "Suspended") {
                return Result.failure(Exception("Your account has been suspended! Please contact support."))
            }
            return Result.success(existing)
        }

        val cleanName = name.replace("\\s".toRegex(), "").take(4).uppercase()
        val randomSuffix = (1000..9999).random().toString()
        val referralCode = "$cleanName$randomSuffix"

        val newUser = UserEntity(
            email = email,
            fullName = name,
            whatsappNumber = "",
            passwordHash = "GOOGLE_OAUTH_PWD",
            walletBalance = 0.0,
            referralCode = referralCode,
            referredBy = null,
            accountStatus = "Active"
        )
        userDao.insertUser(newUser)
        return Result.success(newUser)
    }

    // SERVICE AND ORDER CALCULATIONS
    suspend fun calculateFinalPricePerThousand(serviceId: Int): Double {
        val service = smmServiceDao.getServiceById(serviceId) ?: return 0.0
        val config = adminConfigDao.getConfig() ?: return service.basePrice
        val margin = if (service.individualMarkup > 0.0) service.individualMarkup else config.globalMargin
        // Price_final = Price_base + (Price_base * (Margin_percentage / 100))
        return service.basePrice + (service.basePrice * (margin / 100.0))
    }

    suspend fun placeNewOrder(
        userEmail: String,
        serviceId: Int,
        targetLink: String,
        quantity: Int
    ): Result<OrderEntity> {
        val user = userDao.getUserByEmail(userEmail) ?: return Result.failure(Exception("User not found!"))
        if (user.accountStatus == "Suspended") {
            return Result.failure(Exception("Your account is suspended!"))
        }

        val service = smmServiceDao.getServiceById(serviceId) ?: return Result.failure(Exception("Service not found!"))
        if (!service.isActive) {
            return Result.failure(Exception("Selected service is currently unavailable!"))
        }

        val finalPricePerK = calculateFinalPricePerThousand(serviceId)
        val totalCharge = (finalPricePerK * quantity) / 1000.0

        if (user.walletBalance < totalCharge) {
            return Result.failure(Exception("Insufficient wallet balance! Required: ₹${String.format("%.2f", totalCharge)}, Available: ₹${String.format("%.2f", user.walletBalance)}"))
        }

        // Deduct balance locally first
        val newBalance = user.walletBalance - totalCharge
        userDao.updateWalletBalance(userEmail, newBalance)

        // Submit order to live SMM Provider API (MoreThanPanel)
        return try {
            val response = RetrofitClient.instance.addOrder(
                key = Config.API_KEY,
                service = serviceId.toString(),
                link = targetLink,
                quantity = quantity.toString()
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.error == null) {
                    val smmOrderId = body?.order ?: "API_${System.currentTimeMillis().toString().takeLast(6)}"
                    val orderName = "${service.name} (SMM #$smmOrderId)"

                    val order = OrderEntity(
                        userEmail = userEmail,
                        serviceId = serviceId,
                        serviceName = orderName,
                        targetLink = targetLink,
                        quantity = quantity,
                        totalCharge = totalCharge,
                        status = "Pending"
                    )
                    orderDao.insertOrder(order)
                    Result.success(order)
                } else {
                    // Refund wallet balance due to provider API returning an error
                    userDao.updateWalletBalance(userEmail, user.walletBalance)
                    Result.failure(Exception("SMM Provider Refused: ${body.error} (Your funds have been refunded to your wallet)"))
                }
            } else {
                // Refund wallet balance on non-200 HTTP code
                userDao.updateWalletBalance(userEmail, user.walletBalance)
                Result.failure(Exception("Failed to submit to SMM Provider: HTTP ${response.code()} (Your funds have been refunded to your wallet)"))
            }
        } catch (e: Exception) {
            // Refund wallet balance on network exception
            userDao.updateWalletBalance(userEmail, user.walletBalance)
            Result.failure(Exception("Network error contacting SMM Provider: ${e.localizedMessage} (Your funds have been refunded to your wallet)"))
        }
    }

    // Live SMM Provider API Synchronization & Monitoring
    suspend fun syncServicesWithApi(): Result<Unit> {
        return try {
            val response = RetrofitClient.instance.getServices(Config.API_KEY)
            if (response.isSuccessful) {
                val apiServices = response.body()
                if (apiServices != null) {
                    val existingServices = smmServiceDao.observeAllServices().firstOrNull() ?: emptyList()
                    val existingMap = existingServices.associateBy { it.id }

                    val servicesToInsert = apiServices.map { apiService ->
                        val id = apiService.service.toIntOrNull() ?: apiService.hashCode()
                        val rate = apiService.rate.toDoubleOrNull() ?: 10.0

                        val existing = existingMap[id]
                        val markup = existing?.individualMarkup ?: 0.0
                        val active = existing?.isActive ?: true

                        SmmServiceEntity(
                            id = id,
                            name = apiService.name,
                            category = apiService.category,
                            basePrice = rate,
                            isActive = active,
                            individualMarkup = markup
                        )
                    }

                    if (servicesToInsert.isNotEmpty()) {
                        smmServiceDao.insertServices(servicesToInsert)
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty services body returned from MoreThanPanel API"))
                }
            } else {
                Result.failure(Exception("API returned error code ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProviderApiBalance(): Result<SmmBalanceResponse> {
        return try {
            val response = RetrofitClient.instance.getBalance(Config.API_KEY)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("SMM Provider balance body was null"))
                }
            } else {
                Result.failure(Exception("SMM Provider balance call failed: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DEPOSIT FUNDS REQUESTS
    suspend fun requestDeposit(userEmail: String, amount: Double, utrId: String): Result<PendingDepositEntity> {
        if (utrId.length != 12 || !utrId.all { it.isDigit() }) {
            return Result.failure(Exception("UTR ID must be an exact 12-digit UPI Transaction reference!"))
        }
        if (amount <= 0.0) {
            return Result.failure(Exception("Please enter a valid deposit amount!"))
        }

        val existing = pendingDepositDao.getDepositByUtr(utrId)
        if (existing != null) {
            return Result.failure(Exception("UTR ID has already been submitted for verification!"))
        }

        val deposit = PendingDepositEntity(
            utrId = utrId,
            userEmail = userEmail,
            amount = amount,
            status = "Pending"
        )
        pendingDepositDao.insertDeposit(deposit)
        return Result.success(deposit)
    }

    // RENTAL SYSTEM (৳399 / month)
    suspend fun rentChildPanel(userEmail: String, customDomain: String): Result<ChildPanelRentalEntity> {
        val user = userDao.getUserByEmail(userEmail) ?: return Result.failure(Exception("User not found!"))
        if (customDomain.isBlank()) {
            return Result.failure(Exception("Please enter a valid Custom Domain Name!"))
        }

        val price = 399.0 // ৳399 or ₹399
        if (user.walletBalance < price) {
            return Result.failure(Exception("Insufficient Balance in your wallet! ৳399 required."))
        }

        // Deduct price
        val newBalance = user.walletBalance - price
        userDao.updateWalletBalance(userEmail, newBalance)

        val rental = ChildPanelRentalEntity(
            userEmail = userEmail,
            customDomain = customDomain,
            price = price,
            status = "Pending Configuration"
        )
        childPanelRentalDao.insertRental(rental)
        return Result.success(rental)
    }

    // ADMIN LEVEL ACTIONS
    suspend fun approveDeposit(utrId: String): Boolean {
        val deposit = pendingDepositDao.getDepositByUtr(utrId) ?: return false
        if (deposit.status != "Pending") return false

        val user = userDao.getUserByEmail(deposit.userEmail) ?: return false
        val newBalance = user.walletBalance + deposit.amount

        // Update status to Approved and add balance
        pendingDepositDao.updateDepositStatus(utrId, "Approved")
        userDao.updateWalletBalance(deposit.userEmail, newBalance)
        return true
    }

    suspend fun rejectDeposit(utrId: String): Boolean {
        val deposit = pendingDepositDao.getDepositByUtr(utrId) ?: return false
        if (deposit.status != "Pending") return false

        pendingDepositDao.updateDepositStatus(utrId, "Rejected")
        return true
    }

    suspend fun updateOrderStatus(id: Long, newStatus: String): Boolean {
        val order = orderDao.getOrderById(id) ?: return false
        orderDao.updateOrderStatus(id, newStatus)
        return true
    }

    suspend fun toggleServiceStatus(id: Int, isActive: Boolean) {
        smmServiceDao.toggleServiceActive(id, isActive)
    }

    suspend fun updateServiceMarkup(id: Int, markup: Double) {
        val service = smmServiceDao.getServiceById(id) ?: return
        smmServiceDao.updateService(service.copy(individualMarkup = markup))
    }

    suspend fun updateAdminConfig(
        globalMargin: Double,
        popupNoticeText: String,
        isPopupNoticeActive: Boolean,
        upiId: String
    ) {
        val current = adminConfigDao.getConfig() ?: AdminConfigEntity()
        adminConfigDao.insertOrUpdateConfig(
            current.copy(
                globalMargin = globalMargin,
                popupNoticeText = popupNoticeText,
                isPopupNoticeActive = isPopupNoticeActive,
                upiId = upiId
            )
        )
    }

    suspend fun updateRentalStatus(id: Long, status: String) {
        childPanelRentalDao.updateRentalStatus(id, status)
    }

    suspend fun toggleUserStatus(email: String) {
        val user = userDao.getUserByEmail(email) ?: return
        val newStatus = if (user.accountStatus == "Active") "Suspended" else "Active"
        userDao.updateAccountStatus(email, newStatus)
    }
}
