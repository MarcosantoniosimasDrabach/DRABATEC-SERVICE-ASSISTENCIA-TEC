package com.example.data.repository

import com.example.data.dao.DrabatecDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class DrabatecRepository(private val dao: DrabatecDao) {
    // Users
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun getUserByUsername(username: String): UserEntity? = dao.getUserByUsername(username)
    suspend fun getUserById(id: String): UserEntity? = dao.getUserById(id)
    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun deleteUserById(id: String) = dao.deleteUserById(id)
    suspend fun getUsersCount(): Int = dao.getUsersCount()

    // Service Orders
    val allOrders: Flow<List<ServiceOrderEntity>> = dao.getAllOrders()
    suspend fun insertOrder(order: ServiceOrderEntity) = dao.insertOrder(order)
    suspend fun updateOrder(order: ServiceOrderEntity) = dao.updateOrder(order)
    suspend fun deleteOrder(id: String) = dao.deleteOrder(id)

    // Stock
    val allStock: Flow<List<StockItemEntity>> = dao.getAllStock()
    suspend fun insertStockItem(item: StockItemEntity) = dao.insertStockItem(item)
    suspend fun updateStockItem(item: StockItemEntity) = dao.updateStockItem(item)
    suspend fun deleteStockItem(id: String) = dao.deleteStockItem(id)

    // Appointments
    val allAppointments: Flow<List<AppointmentEntity>> = dao.getAllAppointments()
    suspend fun insertAppointment(appointment: AppointmentEntity) = dao.insertAppointment(appointment)
    suspend fun updateAppointment(appointment: AppointmentEntity) = dao.updateAppointment(appointment)
    suspend fun deleteAppointment(id: String) = dao.deleteAppointment(id)

    // Financial Entries
    val allFinancialEntries: Flow<List<FinancialEntryEntity>> = dao.getAllFinancialEntries()
    suspend fun insertFinancialEntry(entry: FinancialEntryEntity) = dao.insertFinancialEntry(entry)
    suspend fun updateFinancialEntry(entry: FinancialEntryEntity) = dao.updateFinancialEntry(entry)
    suspend fun deleteFinancialEntry(id: String) = dao.deleteFinancialEntry(id)

    // Vehicle Maintenances
    val allVehicleMaintenances: Flow<List<VehicleMaintenanceEntity>> = dao.getAllVehicleMaintenances()
    suspend fun insertVehicleMaintenance(maintenance: VehicleMaintenanceEntity) = dao.insertVehicleMaintenance(maintenance)
    suspend fun updateVehicleMaintenance(maintenance: VehicleMaintenanceEntity) = dao.updateVehicleMaintenance(maintenance)
    suspend fun deleteVehicleMaintenance(id: String) = dao.deleteVehicleMaintenance(id)
}
