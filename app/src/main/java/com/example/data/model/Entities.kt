package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val passwordHash: String,
    val photoUri: String? = null,
    val isTemp: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_orders")
data class ServiceOrderEntity(
    @PrimaryKey val id: String,
    val equipment: String,
    val clientName: String,
    val clientPhone: String,
    val reportedFault: String,
    val technicalDiagnosis: String,
    val status: String, // "Aberto", "Em Análise", "Aguardando Peça", "Concluído", "Entregue"
    val totalValue: Double,
    val entryDate: Long = System.currentTimeMillis(),
    val exitDate: Long? = null
)

@Entity(tableName = "stock_items")
data class StockItemEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val category: String,
    val quantity: Int,
    val minQuantity: Int,
    val costPrice: Double,
    val sellPrice: Double,
    val unit: String = "UN"
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val clientName: String,
    val serviceType: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val address: String,
    val status: String = "Agendado", // "Agendado", "Em Andamento", "Realizado", "Cancelado"
    val notes: String = ""
)

@Entity(tableName = "financial_entries")
data class FinancialEntryEntity(
    @PrimaryKey val id: String,
    val description: String,
    val category: String,
    val amount: Double,
    val type: String, // "RECEITA" or "DESPESA"
    val dueDate: String,
    val isPaid: Boolean = false,
    val paymentMethod: String = "PIX"
)

@Entity(tableName = "vehicle_maintenances")
data class VehicleMaintenanceEntity(
    @PrimaryKey val id: String,
    val vehicleModel: String,
    val licensePlate: String,
    val currentKm: Int,
    val serviceType: String, // "Troca de Óleo", "Freios", "Correia Dentada", "Arrefecimento/Água", "Filtros", "Suspensão"
    val nextKm: Int,
    val maintenanceDate: String,
    val status: String, // "Em Dia", "Próximo", "Vencido"
    val cost: Double,
    val notes: String = ""
)
