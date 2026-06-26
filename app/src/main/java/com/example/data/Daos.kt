package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    fun observeUserByEmail(email: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET walletBalance = :balance WHERE email = :email")
    suspend fun updateWalletBalance(email: String, balance: Double)

    @Query("UPDATE users SET accountStatus = :status WHERE email = :email")
    suspend fun updateAccountStatus(email: String, status: String)

    @Query("SELECT * FROM users WHERE referredBy = :code")
    fun getReferredUsers(code: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?
}

@Dao
interface SmmServiceDao {
    @Query("SELECT * FROM smm_services")
    fun observeAllServices(): Flow<List<SmmServiceEntity>>

    @Query("SELECT * FROM smm_services WHERE isActive = 1")
    fun observeActiveServices(): Flow<List<SmmServiceEntity>>

    @Query("SELECT * FROM smm_services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Int): SmmServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<SmmServiceEntity>)

    @Update
    suspend fun updateService(service: SmmServiceEntity)

    @Query("UPDATE smm_services SET isActive = :isActive WHERE id = :id")
    suspend fun toggleServiceActive(id: Int, isActive: Boolean)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userEmail = :email ORDER BY timestamp DESC")
    fun observeUserOrders(email: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun observeAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String)
}

@Dao
interface PendingDepositDao {
    @Query("SELECT * FROM pending_deposits WHERE userEmail = :email ORDER BY timestamp DESC")
    fun observeUserDeposits(email: String): Flow<List<PendingDepositEntity>>

    @Query("SELECT * FROM pending_deposits ORDER BY timestamp DESC")
    fun observeAllDeposits(): Flow<List<PendingDepositEntity>>

    @Query("SELECT * FROM pending_deposits WHERE utrId = :utrId LIMIT 1")
    suspend fun getDepositByUtr(utrId: String): PendingDepositEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeposit(deposit: PendingDepositEntity): Long

    @Query("UPDATE pending_deposits SET status = :status WHERE utrId = :utrId")
    suspend fun updateDepositStatus(utrId: String, status: String)
}

@Dao
interface ChildPanelRentalDao {
    @Query("SELECT * FROM child_panel_rentals WHERE userEmail = :email ORDER BY rentalDate DESC")
    fun observeUserRentals(email: String): Flow<List<ChildPanelRentalEntity>>

    @Query("SELECT * FROM child_panel_rentals ORDER BY rentalDate DESC")
    fun observeAllRentals(): Flow<List<ChildPanelRentalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: ChildPanelRentalEntity)

    @Query("UPDATE child_panel_rentals SET status = :status WHERE id = :id")
    suspend fun updateRentalStatus(id: Long, status: String)

    @Query("UPDATE child_panel_rentals SET expiryDate = :expiryDate WHERE id = :id")
    suspend fun updateRentalExpiry(id: Long, expiryDate: Long)
}

@Dao
interface AdminConfigDao {
    @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
    fun observeConfig(): Flow<AdminConfigEntity?>

    @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): AdminConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: AdminConfigEntity)
}
