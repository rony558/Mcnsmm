package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        SmmServiceEntity::class,
        OrderEntity::class,
        PendingDepositEntity::class,
        ChildPanelRentalEntity::class,
        AdminConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun smmServiceDao(): SmmServiceDao
    abstract fun orderDao(): OrderDao
    abstract fun pendingDepositDao(): PendingDepositDao
    abstract fun childPanelRentalDao(): ChildPanelRentalDao
    abstract fun adminConfigDao(): AdminConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mcnsmm_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
