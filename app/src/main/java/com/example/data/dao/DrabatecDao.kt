package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DrabatecDao {
    // Users
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    // Service Orders (Registo Entrada/Saída)
    @Query("SELECT * FROM service_orders ORDER BY entryDate DESC")
    fun getAllOrders(): Flow<List<ServiceOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: ServiceOrderEntity)

    @Update
    suspend fun updateOrder(order: ServiceOrderEntity)

    @Query("DELETE FROM service_orders WHERE id = :id")
    suspend fun deleteOrder(id: String)

    // Stock Items (Estoque e Venda)
    @Query("SELECT * FROM stock_items ORDER BY name ASC")
    fun getAllStock(): Flow<List<StockItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockItem(item: StockItemEntity)

    @Update
    suspend fun updateStockItem(item: StockItemEntity)

    @Query("DELETE FROM stock_items WHERE id = :id")
    suspend fun deleteStockItem(id: String)

    // Appointments (Agenda Serviços)
    @Query("SELECT * FROM appointments ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: String)

    // Financial Entries (Conta Doméstica / Controle Financeiro)
    @Query("SELECT * FROM financial_entries ORDER BY dueDate DESC")
    fun getAllFinancialEntries(): Flow<List<FinancialEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialEntry(entry: FinancialEntryEntity)

    @Update
    suspend fun updateFinancialEntry(entry: FinancialEntryEntity)

    @Query("DELETE FROM financial_entries WHERE id = :id")
    suspend fun deleteFinancialEntry(id: String)

    // Vehicle Maintenances (Preventivas Veiculares)
    @Query("SELECT * FROM vehicle_maintenances ORDER BY nextKm ASC")
    fun getAllVehicleMaintenances(): Flow<List<VehicleMaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicleMaintenance(maintenance: VehicleMaintenanceEntity)

    @Update
    suspend fun updateVehicleMaintenance(maintenance: VehicleMaintenanceEntity)

    @Query("DELETE FROM vehicle_maintenances WHERE id = :id")
    suspend fun deleteVehicleMaintenance(id: String)
}
