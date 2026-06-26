package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val whatsappNumber: String,
    val passwordHash: String,
    val walletBalance: Double = 0.0,
    val referralCode: String,
    val referredBy: String? = null,
    val accountStatus: String = "Active" // "Active", "Suspended"
)

@Entity(tableName = "smm_services")
data class SmmServiceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val basePrice: Double, // Price per 1000
    val isActive: Boolean = true,
    val individualMarkup: Double = 0.0 // Percentage markup override. 0.0 means use global margin.
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val serviceId: Int,
    val serviceName: String,
    val targetLink: String,
    val quantity: Int,
    val totalCharge: Double,
    val status: String = "Pending", // "Pending", "In Progress", "Completed"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pending_deposits")
data class PendingDepositEntity(
    @PrimaryKey val utrId: String, // 12-digit UTR
    val userEmail: String,
    val amount: Double,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "child_panel_rentals")
data class ChildPanelRentalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val customDomain: String,
    val rentalDate: Long = System.currentTimeMillis(),
    val expiryDate: Long = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000, // 30 Days
    val price: Double = 399.0, // ৳399 or ₹399
    val status: String = "Pending Configuration" // "Pending Configuration", "Active", "Suspended", "Expired"
)

@Entity(tableName = "admin_config")
data class AdminConfigEntity(
    @PrimaryKey val id: Int = 1,
    val globalMargin: Double = 10.0, // 10%
    val popupNoticeText: String = "🎉 Welcome to MCNSMM SMM Panel! Enjoy instant processing and the best rates in the industry. For bulk support, contact admin on WhatsApp.",
    val isPopupNoticeActive: Boolean = true,
    val upiId: String = "mcnsmm@upi",
    val upiQrCodeUrl: String = "" // Placeholder for local QR drawable or web url
)
