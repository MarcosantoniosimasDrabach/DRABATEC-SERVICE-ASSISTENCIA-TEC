package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.DrabatecDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ServiceOrderEntity::class,
        StockItemEntity::class,
        AppointmentEntity::class,
        FinancialEntryEntity::class,
        VehicleMaintenanceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drabatecDao(): DrabatecDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drabatec_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.drabatecDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: DrabatecDao) {
                // Usuários Iniciais (compatível com o sistema original)
                dao.insertUser(
                    UserEntity(
                        id = "user_admin",
                        username = "admin",
                        passwordHash = "admin123",
                        isTemp = false,
                        photoUri = null
                    )
                )
                dao.insertUser(
                    UserEntity(
                        id = "user_tecnico",
                        username = "tecnico",
                        passwordHash = "1234",
                        isTemp = false,
                        photoUri = null
                    )
                )

                // Ordens de Serviço Iniciais (Assistência Técnica Drabatec)
                dao.insertOrder(
                    ServiceOrderEntity(
                        id = "OS-2026-001",
                        equipment = "Inversor de Frequência WEG CFW500",
                        clientName = "Metalúrgica São Jorge",
                        clientPhone = "(11) 98765-4321",
                        reportedFault = "Não liga / Erro E04 sobrecorrente",
                        technicalDiagnosis = "Módulo IGBT danificado e capacitor estufado",
                        status = "Em Análise",
                        totalValue = 850.0,
                        entryDate = System.currentTimeMillis() - 86400000L * 2
                    )
                )
                dao.insertOrder(
                    ServiceOrderEntity(
                        id = "OS-2026-002",
                        equipment = "Compressor de Ar Chiaperini 20HP",
                        clientName = "Auto Mecânica Paulista",
                        clientPhone = "(11) 97654-3210",
                        reportedFault = "Vazamento no cabeçote e baixa pressão",
                        technicalDiagnosis = "Troca de juntas, retífica de válvulas e substituição de óleo",
                        status = "Aguardando Peça",
                        totalValue = 1200.0,
                        entryDate = System.currentTimeMillis() - 86400000L * 4
                    )
                )
                dao.insertOrder(
                    ServiceOrderEntity(
                        id = "OS-2026-003",
                        equipment = "Bomba D'água Centrífuga Dancor 3CV",
                        clientName = "Condomínio Residencial Parque Real",
                        clientPhone = "(11) 99123-4567",
                        reportedFault = "Ruído excessivo e travamento intermitente",
                        technicalDiagnosis = "Rolamentos desgastados e selo mecânico com vazamento",
                        status = "Concluído",
                        totalValue = 480.0,
                        entryDate = System.currentTimeMillis() - 86400000L * 6,
                        exitDate = System.currentTimeMillis() - 86400000L * 1
                    )
                )
                dao.insertOrder(
                    ServiceOrderEntity(
                        id = "OS-2026-004",
                        equipment = "Gerador a Diesel Toyama 7kVA",
                        clientName = "Supermercado Nova Esperança",
                        clientPhone = "(11) 98877-6655",
                        reportedFault = "Instabilidade na tensão de saída (oscilando 180V-250V)",
                        technicalDiagnosis = "Regulador Automático de Voltagem (AVR) avariado",
                        status = "Aberto",
                        totalValue = 650.0,
                        entryDate = System.currentTimeMillis() - 86400000L * 1
                    )
                )

                // Itens de Estoque e Peças
                dao.insertStockItem(
                    StockItemEntity(
                        id = "PEC-001",
                        code = "IGBT-600V",
                        name = "Módulo IGBT 600V 50A",
                        category = "Eletrônica",
                        quantity = 8,
                        minQuantity = 3,
                        costPrice = 85.0,
                        sellPrice = 160.0
                    )
                )
                dao.insertStockItem(
                    StockItemEntity(
                        id = "PEC-002",
                        code = "ROL-6204",
                        name = "Rolamento Blindado 6204 DDU",
                        category = "Mecânica",
                        quantity = 24,
                        minQuantity = 10,
                        costPrice = 14.50,
                        sellPrice = 35.0
                    )
                )
                dao.insertStockItem(
                    StockItemEntity(
                        id = "PEC-003",
                        code = "CAP-450V",
                        name = "Capacitor Eletrolítico 470uF 450V",
                        category = "Eletrônica",
                        quantity = 2,
                        minQuantity = 5, // Alerta estoque baixo
                        costPrice = 18.0,
                        sellPrice = 45.0
                    )
                )
                dao.insertStockItem(
                    StockItemEntity(
                        id = "PEC-004",
                        code = "LUB-ISO68",
                        name = "Óleo Lubrificante Compressor ISO 68 (1L)",
                        category = "Fluidos",
                        quantity = 15,
                        minQuantity = 6,
                        costPrice = 32.0,
                        sellPrice = 65.0
                    )
                )
                dao.insertStockItem(
                    StockItemEntity(
                        id = "PEC-005",
                        code = "SELO-58",
                        name = "Selo Mecânico 5/8 Viton",
                        category = "Vedação",
                        quantity = 1,
                        minQuantity = 4, // Alerta estoque baixo
                        costPrice = 28.0,
                        sellPrice = 60.0
                    )
                )

                // Agendamentos de Serviços
                dao.insertAppointment(
                    AppointmentEntity(
                        id = "AG-001",
                        clientName = "Cerâmica Arte Viva",
                        serviceType = "Manutenção Preventiva Painel Elétrico",
                        scheduledDate = "Hoje",
                        scheduledTime = "14:30",
                        address = "Av. Industrial, 450 - Galpão 3",
                        status = "Agendado",
                        notes = "Levar multímetro TrueRMS e câmera termográfica"
                    )
                )
                dao.insertAppointment(
                    AppointmentEntity(
                        id = "AG-002",
                        clientName = "Padaria Central",
                        serviceType = "Reparo Forno Turbo e Esteira",
                        scheduledDate = "Amanhã",
                        scheduledTime = "09:00",
                        address = "Rua do Comércio, 128 - Centro",
                        status = "Agendado",
                        notes = "Verificar sensor termopar e contator principal"
                    )
                )

                // Finanças Integradas (Contas a pagar / receber)
                dao.insertFinancialEntry(
                    FinancialEntryEntity(
                        id = "FIN-001",
                        description = "Pagamento Guia DAS-MEI Competência Atual",
                        category = "Impostos MEI",
                        amount = 75.60,
                        type = "DESPESA",
                        dueDate = "20/09/2026",
                        isPaid = true,
                        paymentMethod = "PIX"
                    )
                )
                dao.insertFinancialEntry(
                    FinancialEntryEntity(
                        id = "FIN-002",
                        description = "Aluguel Bancada e Oficina Drabatec",
                        category = "Fixas",
                        amount = 850.00,
                        type = "DESPESA",
                        dueDate = "10/09/2026",
                        isPaid = false,
                        paymentMethod = "Boleto"
                    )
                )
                dao.insertFinancialEntry(
                    FinancialEntryEntity(
                        id = "FIN-003",
                        description = "Recebimento OS-2026-003 Bomba D'água",
                        category = "Serviços",
                        amount = 480.00,
                        type = "RECEITA",
                        dueDate = "06/09/2026",
                        isPaid = true,
                        paymentMethod = "PIX"
                    )
                )
                dao.insertFinancialEntry(
                    FinancialEntryEntity(
                        id = "FIN-004",
                        description = "Venda Peça Módulo IGBT e Rolamentos",
                        category = "Vendas",
                        amount = 355.00,
                        type = "RECEITA",
                        dueDate = "07/09/2026",
                        isPaid = true,
                        paymentMethod = "Cartão de Crédito"
                    )
                )

                // Manutenção Preventiva Veicular
                dao.insertVehicleMaintenance(
                    VehicleMaintenanceEntity(
                        id = "VEI-001",
                        vehicleModel = "Fiat Fiorino 1.4 EVO Assistência",
                        licensePlate = "DRB-7859",
                        currentKm = 87400,
                        serviceType = "Troca de Óleo 15W40 e Filtro",
                        nextKm = 90000,
                        maintenanceDate = "15/08/2026",
                        status = "Em Dia",
                        cost = 240.0,
                        notes = "Próxima troca em 90.000 km (faltam 2.600 km)"
                    )
                )
                dao.insertVehicleMaintenance(
                    VehicleMaintenanceEntity(
                        id = "VEI-002",
                        vehicleModel = "Fiat Fiorino 1.4 EVO Assistência",
                        licensePlate = "DRB-7859",
                        currentKm = 87400,
                        serviceType = "Pastilhas de Freio Dianteiras",
                        nextKm = 88000,
                        maintenanceDate = "10/06/2026",
                        status = "Próximo",
                        cost = 180.0,
                        notes = "Faltam apenas 600 km para revisão preventiva de freios"
                    )
                )
                dao.insertVehicleMaintenance(
                    VehicleMaintenanceEntity(
                        id = "VEI-003",
                        vehicleModel = "Fiat Fiorino 1.4 EVO Assistência",
                        licensePlate = "DRB-7859",
                        currentKm = 87400,
                        serviceType = "Líquido de Arrefecimento e Válvula Termostática",
                        nextKm = 85000,
                        maintenanceDate = "05/02/2026",
                        status = "Vencido",
                        cost = 160.0,
                        notes = "Revisão do manômetro e pressão de água vencida por km"
                    )
                )
            }
        }
    }
}
