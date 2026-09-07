package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.DrabatecRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppSection(val title: String, val category: String) {
    DASHBOARD("Drabatec Service", "PRINCIPAL"),
    // GESTÃO
    OS_ORDERS("Registo (Entrada/Saída)", "GESTÃO"),
    STOCK("Estoque e Venda", "GESTÃO"),
    APPOINTMENTS("Agenda Serviços", "GESTÃO"),
    TEMPERATURE_MONITOR("Monitor Temperatura", "GESTÃO"),
    // FINANCEIRO
    FINANCIAL("Conta Doméstica", "FINANCEIRO"),
    CREDIT_SIMULATOR("Simulador de Crédito", "FINANCEIRO"),
    MEI_PAYMENT("Pagamento MEI", "FINANCEIRO"),
    // VEÍCULOS
    VEHICLE_MAINTENANCE("Preventivas Veiculares", "VEÍCULOS"),
    MAPS_GPS("Endereços Mapa GPS", "VEÍCULOS"),
    // EDUCACIONAL
    EDUCATIONAL("Links de Aulas & Estudos", "EDUCACIONAL"),
    // INVESTIMENTOS
    INVESTMENTS("Investimento_up", "INVESTIMENTOS"),
    // CONFIGURAÇÕES
    SPLASH_CONFIG("Configurar Splash", "CONFIGURAÇÕES")
}

data class CreditSimulationResult(
    val monthlyPayment: Double,
    val totalPaid: Double,
    val totalInterest: Double,
    val installments: List<InstallmentDetail>
)

data class InstallmentDetail(
    val number: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double
)

data class InvestmentSimulationResult(
    val totalInvested: Double,
    val grossTotal: Double,
    val netTotal: Double,
    val totalInterest: Double,
    val savingsComparison: Double
)

class DrabatecViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DrabatecRepository
    private val prefs = application.getSharedPreferences("drabatec_prefs", Context.MODE_PRIVATE)

    val allUsers: StateFlow<List<UserEntity>>
    val allOrders: StateFlow<List<ServiceOrderEntity>>
    val allStock: StateFlow<List<StockItemEntity>>
    val allAppointments: StateFlow<List<AppointmentEntity>>
    val allFinancialEntries: StateFlow<List<FinancialEntryEntity>>
    val allVehicleMaintenances: StateFlow<List<VehicleMaintenanceEntity>>

    // Splash State
    private val _splashProgress = MutableStateFlow(0f)
    val splashProgress: StateFlow<Float> = _splashProgress.asStateFlow()

    private val _splashStatus = MutableStateFlow("Inicializando sistema...")
    val splashStatus: StateFlow<String> = _splashStatus.asStateFlow()

    private val _isSplashFinished = MutableStateFlow(false)
    val isSplashFinished: StateFlow<Boolean> = _isSplashFinished.asStateFlow()

    // Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginUsername = MutableStateFlow("")
    val loginUsername: StateFlow<String> = _loginUsername.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isFastLoginMode = MutableStateFlow(false)
    val isFastLoginMode: StateFlow<Boolean> = _isFastLoginMode.asStateFlow()

    // Navigation State
    private val _currentSection = MutableStateFlow(AppSection.DASHBOARD)
    val currentSection: StateFlow<AppSection> = _currentSection.asStateFlow()

    private val _backStack = MutableStateFlow<List<AppSection>>(listOf(AppSection.DASHBOARD))
    val backStack: StateFlow<List<AppSection>> = _backStack.asStateFlow()

    // Dialogs
    val isSecurityVerifyOpen = MutableStateFlow(false)
    val isUserConfigOpen = MutableStateFlow(false)
    val isInfoDialogOpen = MutableStateFlow(false)
    val securityVerifyError = MutableStateFlow<String?>(null)

    // User Management Form
    val editingUserId = MutableStateFlow<String?>(null)
    val formUsername = MutableStateFlow("")
    val formPassword = MutableStateFlow("")
    val formConfirmPassword = MutableStateFlow("")
    val formPhoto = MutableStateFlow<String?>(null)
    val formMessage = MutableStateFlow<String?>(null)
    val formIsSuccess = MutableStateFlow(false)

    // Simulator States
    val creditLoanAmount = MutableStateFlow("10000.00")
    val creditInterestRate = MutableStateFlow("2.5") // % ao mês
    val creditInstallmentsCount = MutableStateFlow("12")
    val creditResult = MutableStateFlow<CreditSimulationResult?>(null)

    val investInitialAmount = MutableStateFlow("1000.00")
    val investMonthlyContribution = MutableStateFlow("200.00")
    val investAnnualRate = MutableStateFlow("12.5") // 100% CDI aprox
    val investPeriodMonths = MutableStateFlow("24")
    val investResult = MutableStateFlow<InvestmentSimulationResult?>(null)

    // Temperature Monitor Realtime telemetry values
    val benchTemp = MutableStateFlow(24.5f)
    val ovenTemp = MutableStateFlow(115.0f)
    val transformerTemp = MutableStateFlow(42.3f)
    val isTempAlarmActive = MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = DrabatecRepository(database.drabatecDao())

        allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allOrders = repository.allOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allStock = repository.allStock.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allAppointments = repository.allAppointments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allFinancialEntries = repository.allFinancialEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allVehicleMaintenances = repository.allVehicleMaintenances.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        _isFastLoginMode.value = prefs.getBoolean("fast_login_mode", false)
        val savedUser = prefs.getString("last_username", "admin") ?: "admin"
        _loginUsername.value = savedUser

        startSplashScreenSequence()
        calculateCreditSimulation()
        calculateInvestmentSimulation()
        startTemperatureTelemetry()
    }

    private fun startSplashScreenSequence() {
        viewModelScope.launch {
            _splashProgress.value = 0.15f
            _splashStatus.value = "Verificando conexão com banco local..."
            delay(500)

            _splashProgress.value = 0.45f
            _splashStatus.value = "Sincronizando dados dos módulos Drabatec..."
            delay(500)

            _splashProgress.value = 0.80f
            _splashStatus.value = "Carregando credenciais e configurações..."
            delay(400)

            _splashProgress.value = 1.0f
            _splashStatus.value = "Sistema verificado com sucesso!"
            delay(400)

            _isSplashFinished.value = true
        }
    }

    private fun startTemperatureTelemetry() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                // Small dynamic variations for real feel
                benchTemp.value = (benchTemp.value + (-0.3f + Math.random().toFloat() * 0.6f)).coerceIn(20f, 35f)
                ovenTemp.value = (ovenTemp.value + (-0.8f + Math.random().toFloat() * 1.6f)).coerceIn(90f, 130f)
                transformerTemp.value = (transformerTemp.value + (-0.4f + Math.random().toFloat() * 0.8f)).coerceIn(35f, 65f)
                isTempAlarmActive.value = ovenTemp.value > 125f || transformerTemp.value > 60f
            }
        }
    }

    fun setLoginUsername(username: String) {
        _loginUsername.value = username
        _loginError.value = null
    }

    fun setLoginPassword(password: String) {
        _loginPassword.value = password
        _loginError.value = null
    }

    fun toggleFastLoginMode(enabled: Boolean) {
        _isFastLoginMode.value = enabled
        prefs.edit().putBoolean("fast_login_mode", enabled).apply()
    }

    fun performLogin() {
        viewModelScope.launch {
            val username = _loginUsername.value.trim()
            val password = _loginPassword.value.trim()

            if (username.isEmpty()) {
                _loginError.value = "Selecione ou digite um usuário."
                return@launch
            }
            if (password.isEmpty()) {
                _loginError.value = "Digite a senha!"
                return@launch
            }

            val user = repository.getUserByUsername(username)
            if (user != null && user.passwordHash == password) {
                _currentUser.value = user
                _isLoggedIn.value = true
                _loginError.value = null
                _loginPassword.value = ""
                prefs.edit().putString("last_username", username).apply()
            } else {
                _loginError.value = "Usuário ou senha incorretos!"
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _loginPassword.value = ""
        _currentSection.value = AppSection.DASHBOARD
        _backStack.value = listOf(AppSection.DASHBOARD)
    }

    fun navigateTo(section: AppSection) {
        if (_currentSection.value != section) {
            _currentSection.value = section
            _backStack.value = _backStack.value + section
        }
    }

    fun navigateBack(): Boolean {
        val stack = _backStack.value
        return if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            _backStack.value = newStack
            _currentSection.value = newStack.last()
            true
        } else {
            false
        }
    }

    fun verifySecurityPassword(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value ?: repository.getUserByUsername(_loginUsername.value)
            if (user != null && user.passwordHash == password) {
                securityVerifyError.value = null
                isSecurityVerifyOpen.value = false
                onSuccess()
            } else {
                securityVerifyError.value = "Senha de segurança incorreta!"
            }
        }
    }

    // User Management CRUD
    fun prepareNewUserForm() {
        editingUserId.value = null
        formUsername.value = ""
        formPassword.value = ""
        formConfirmPassword.value = ""
        formPhoto.value = null
        formMessage.value = null
        formIsSuccess.value = false
    }

    fun prepareEditUser(user: UserEntity) {
        editingUserId.value = user.id
        formUsername.value = user.username
        formPassword.value = ""
        formConfirmPassword.value = ""
        formPhoto.value = user.photoUri
        formMessage.value = null
        formIsSuccess.value = false
    }

    fun saveUserForm() {
        viewModelScope.launch {
            val username = formUsername.value.trim()
            val password = formPassword.value.trim()
            val confirm = formConfirmPassword.value.trim()

            if (username.isEmpty()) {
                formMessage.value = "Preencha o nome de usuário!"
                formIsSuccess.value = false
                return@launch
            }

            if (editingUserId.value == null && password.isEmpty()) {
                formMessage.value = "Digite uma senha para o novo usuário!"
                formIsSuccess.value = false
                return@launch
            }

            if (password.isNotEmpty()) {
                if (password != confirm) {
                    formMessage.value = "As senhas não coincidem!"
                    formIsSuccess.value = false
                    return@launch
                }
                if (password.length < 4) {
                    formMessage.value = "A senha deve ter no mínimo 4 caracteres!"
                    formIsSuccess.value = false
                    return@launch
                }
            }

            val editId = editingUserId.value
            if (editId != null) {
                val existing = repository.getUserById(editId)
                if (existing != null) {
                    val newPw = if (password.isNotEmpty()) password else existing.passwordHash
                    val updated = existing.copy(
                        username = username,
                        passwordHash = newPw,
                        photoUri = formPhoto.value ?: existing.photoUri
                    )
                    repository.updateUser(updated)
                    formMessage.value = "Usuário atualizado com sucesso!"
                    formIsSuccess.value = true
                    if (_currentUser.value?.id == editId) {
                        _currentUser.value = updated
                    }
                }
            } else {
                val checkExisting = repository.getUserByUsername(username)
                if (checkExisting != null) {
                    formMessage.value = "Já existe um usuário com este nome!"
                    formIsSuccess.value = false
                    return@launch
                }
                val newUser = UserEntity(
                    id = "user_${System.currentTimeMillis()}",
                    username = username,
                    passwordHash = password,
                    photoUri = formPhoto.value
                )
                repository.insertUser(newUser)
                formMessage.value = "Usuário adicionado com sucesso!"
                formIsSuccess.value = true
                prepareNewUserForm()
            }
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            val user = repository.getUserById(id)
            if (user?.username == "admin") {
                formMessage.value = "Não é permitido excluir o usuário administrador padrão."
                formIsSuccess.value = false
                return@launch
            }
            repository.deleteUserById(id)
            formMessage.value = "Usuário removido com sucesso."
            formIsSuccess.value = true
        }
    }

    // Service Orders CRUD
    fun saveServiceOrder(order: ServiceOrderEntity) {
        viewModelScope.launch {
            repository.insertOrder(order)
        }
    }

    fun updateOrderStatus(order: ServiceOrderEntity, newStatus: String) {
        viewModelScope.launch {
            val exit = if (newStatus == "Entregue" || newStatus == "Concluído") System.currentTimeMillis() else order.exitDate
            repository.updateOrder(order.copy(status = newStatus, exitDate = exit))
        }
    }

    fun deleteOrder(id: String) {
        viewModelScope.launch {
            repository.deleteOrder(id)
        }
    }

    // Stock Items CRUD
    fun saveStockItem(item: StockItemEntity) {
        viewModelScope.launch {
            repository.insertStockItem(item)
        }
    }

    fun adjustStockQuantity(item: StockItemEntity, delta: Int) {
        viewModelScope.launch {
            val newQty = (item.quantity + delta).coerceAtLeast(0)
            repository.updateStockItem(item.copy(quantity = newQty))
        }
    }

    fun deleteStockItem(id: String) {
        viewModelScope.launch {
            repository.deleteStockItem(id)
        }
    }

    // Appointments CRUD
    fun saveAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.insertAppointment(appointment)
        }
    }

    fun toggleAppointmentStatus(appointment: AppointmentEntity) {
        viewModelScope.launch {
            val nextStatus = when (appointment.status) {
                "Agendado" -> "Em Andamento"
                "Em Andamento" -> "Realizado"
                else -> "Agendado"
            }
            repository.updateAppointment(appointment.copy(status = nextStatus))
        }
    }

    fun deleteAppointment(id: String) {
        viewModelScope.launch {
            repository.deleteAppointment(id)
        }
    }

    // Financial Entries CRUD
    fun saveFinancialEntry(entry: FinancialEntryEntity) {
        viewModelScope.launch {
            repository.insertFinancialEntry(entry)
        }
    }

    fun toggleFinancialPaid(entry: FinancialEntryEntity) {
        viewModelScope.launch {
            repository.updateFinancialEntry(entry.copy(isPaid = !entry.isPaid))
        }
    }

    fun deleteFinancialEntry(id: String) {
        viewModelScope.launch {
            repository.deleteFinancialEntry(id)
        }
    }

    // Vehicle Maintenances CRUD
    fun saveVehicleMaintenance(maintenance: VehicleMaintenanceEntity) {
        viewModelScope.launch {
            repository.insertVehicleMaintenance(maintenance)
        }
    }

    fun deleteVehicleMaintenance(id: String) {
        viewModelScope.launch {
            repository.deleteVehicleMaintenance(id)
        }
    }

    // Simulator Calculators
    fun calculateCreditSimulation() {
        val amount = creditLoanAmount.value.toDoubleOrNull() ?: 10000.0
        val ratePercent = creditInterestRate.value.toDoubleOrNull() ?: 2.5
        val n = creditInstallmentsCount.value.toIntOrNull() ?: 12

        val r = ratePercent / 100.0
        val pmt = if (r > 0) {
            val factor = Math.pow(1.0 + r, n.toDouble())
            amount * (r * factor) / (factor - 1.0)
        } else {
            amount / n
        }

        var balance = amount
        val list = mutableListOf<InstallmentDetail>()
        for (i in 1..n) {
            val interestPart = balance * r
            val principalPart = pmt - interestPart
            balance = (balance - principalPart).coerceAtLeast(0.0)
            list.add(
                InstallmentDetail(
                    number = i,
                    payment = pmt,
                    principal = principalPart,
                    interest = interestPart,
                    balance = balance
                )
            )
        }
        val totalPaid = pmt * n
        val totalInterest = totalPaid - amount

        creditResult.value = CreditSimulationResult(
            monthlyPayment = pmt,
            totalPaid = totalPaid,
            totalInterest = totalInterest,
            installments = list
        )
    }

    fun calculateInvestmentSimulation() {
        val initial = investInitialAmount.value.toDoubleOrNull() ?: 1000.0
        val monthly = investMonthlyContribution.value.toDoubleOrNull() ?: 200.0
        val annualRate = investAnnualRate.value.toDoubleOrNull() ?: 12.5
        val months = investPeriodMonths.value.toIntOrNull() ?: 24

        val monthlyRate = Math.pow(1.0 + annualRate / 100.0, 1.0 / 12.0) - 1.0
        val savingsMonthlyRate = 0.005 // 0.5% ao mês aprox poupança

        var currentTotal = initial
        var savingsTotal = initial
        var totalInvested = initial

        for (m in 1..months) {
            currentTotal = (currentTotal + monthly) * (1.0 + monthlyRate)
            savingsTotal = (savingsTotal + monthly) * (1.0 + savingsMonthlyRate)
            totalInvested += monthly
        }

        val totalInterest = (currentTotal - totalInvested).coerceAtLeast(0.0)
        val netTotal = totalInvested + (totalInterest * 0.85) // Imposto de renda 15% retido

        investResult.value = InvestmentSimulationResult(
            totalInvested = totalInvested,
            grossTotal = currentTotal,
            netTotal = netTotal,
            totalInterest = totalInterest,
            savingsComparison = savingsTotal
        )
    }
}
