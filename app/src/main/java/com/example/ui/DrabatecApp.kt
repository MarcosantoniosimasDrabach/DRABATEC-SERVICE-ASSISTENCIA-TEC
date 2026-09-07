package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppSection
import com.example.ui.viewmodel.DrabatecViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrabatecApp(viewModel: DrabatecViewModel) {
    val isSplashFinished by viewModel.isSplashFinished.collectAsStateWithLifecycle()
    val splashProgress by viewModel.splashProgress.collectAsStateWithLifecycle()
    val splashStatus by viewModel.splashStatus.collectAsStateWithLifecycle()

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val selectedUsername by viewModel.loginUsername.collectAsStateWithLifecycle()
    val passwordInput by viewModel.loginPassword.collectAsStateWithLifecycle()
    val loginErrorMessage by viewModel.loginError.collectAsStateWithLifecycle()
    val isFastLoginMode by viewModel.isFastLoginMode.collectAsStateWithLifecycle()

    // Data lists
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val stock by viewModel.allStock.collectAsStateWithLifecycle()
    val appointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val financials by viewModel.allFinancialEntries.collectAsStateWithLifecycle()
    val vehicleMaintenances by viewModel.allVehicleMaintenances.collectAsStateWithLifecycle()

    // Telemetry
    val benchTemp by viewModel.benchTemp.collectAsStateWithLifecycle()
    val ovenTemp by viewModel.ovenTemp.collectAsStateWithLifecycle()
    val transformerTemp by viewModel.transformerTemp.collectAsStateWithLifecycle()
    val isAlarmActive by viewModel.isTempAlarmActive.collectAsStateWithLifecycle()

    // User management modal
    val isUserConfigOpen by viewModel.isUserConfigOpen.collectAsStateWithLifecycle()
    val isSecurityVerifyOpen by viewModel.isSecurityVerifyOpen.collectAsStateWithLifecycle()
    val securityError by viewModel.securityVerifyError.collectAsStateWithLifecycle()
    val editingUserId by viewModel.editingUserId.collectAsStateWithLifecycle()
    val formUsername by viewModel.formUsername.collectAsStateWithLifecycle()
    val formPassword by viewModel.formPassword.collectAsStateWithLifecycle()
    val formConfirmPassword by viewModel.formConfirmPassword.collectAsStateWithLifecycle()
    val userStatusMessage by viewModel.formMessage.collectAsStateWithLifecycle()
    val userStatusIsSuccess by viewModel.formIsSuccess.collectAsStateWithLifecycle()

    // Credit & Investment Simulators
    val loanAmount by viewModel.creditLoanAmount.collectAsStateWithLifecycle()
    val interestRate by viewModel.creditInterestRate.collectAsStateWithLifecycle()
    val installmentsCount by viewModel.creditInstallmentsCount.collectAsStateWithLifecycle()
    val creditSimulationResult by viewModel.creditResult.collectAsStateWithLifecycle()

    val investInitial by viewModel.investInitialAmount.collectAsStateWithLifecycle()
    val investMonthly by viewModel.investMonthlyContribution.collectAsStateWithLifecycle()
    val investRate by viewModel.investAnnualRate.collectAsStateWithLifecycle()
    val investPeriod by viewModel.investPeriodMonths.collectAsStateWithLifecycle()
    val investResult by viewModel.investResult.collectAsStateWithLifecycle()

    var isSystemInfoOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Handle back button
    BackHandler(enabled = isLoggedIn && currentSection != AppSection.DASHBOARD) {
        viewModel.navigateBack()
    }

    // 1. Splash Screen
    if (!isSplashFinished) {
        SplashScreen(
            progress = splashProgress,
            statusText = splashStatus
        )
        return
    }

    // 2. Login Screen
    if (!isLoggedIn) {
        LoginScreen(
            users = users,
            selectedUsername = selectedUsername,
            passwordValue = passwordInput,
            errorMessage = loginErrorMessage,
            isFastLoginMode = isFastLoginMode,
            onUsernameSelected = { viewModel.setLoginUsername(it) },
            onPasswordChanged = { viewModel.setLoginPassword(it) },
            onFastLoginToggled = { viewModel.toggleFastLoginMode(it) },
            onLoginClick = { viewModel.performLogin() },
            onOpenUserConfigClick = { viewModel.isSecurityVerifyOpen.value = true }
        )

        // Security Verification Modal
        SecurityVerifyDialog(
            isOpen = isSecurityVerifyOpen,
            errorMessage = securityError,
            onDismiss = { viewModel.isSecurityVerifyOpen.value = false },
            onConfirm = { pass ->
                viewModel.verifySecurityPassword(pass) {
                    viewModel.prepareNewUserForm()
                    viewModel.isUserConfigOpen.value = true
                }
            }
        )

        // User Management Dialog
        UserManagementDialog(
            isOpen = isUserConfigOpen,
            users = users,
            editingUserId = editingUserId,
            usernameValue = formUsername,
            passwordValue = formPassword,
            confirmPasswordValue = formConfirmPassword,
            statusMessage = userStatusMessage,
            isSuccess = userStatusIsSuccess,
            onUsernameChange = { viewModel.formUsername.value = it },
            onPasswordChange = { viewModel.formPassword.value = it },
            onConfirmPasswordChange = { viewModel.formConfirmPassword.value = it },
            onNewUserClick = { viewModel.prepareNewUserForm() },
            onEditUserClick = { user -> viewModel.prepareEditUser(user) },
            onDeleteUserClick = { id -> viewModel.deleteUser(id) },
            onSaveClick = { viewModel.saveUserForm() },
            onDismiss = { viewModel.isUserConfigOpen.value = false }
        )
        return
    }

    // 3. Main Application with Navigation Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DrabatecPurplePrimary)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = DrabatecPurpleDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Drabatec Service",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Assistência Técnica",
                                    color = DrabatecPurpleLight,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Usuário: ${currentUser?.username ?: "Operador"}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Drawer Navigation Links (Faithfully mapped from menu.php)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    DrawerCategoryHeader("PRINCIPAL")
                    DrawerItem(
                        title = "Dashboard Geral",
                        icon = Icons.Default.Dashboard,
                        isSelected = currentSection == AppSection.DASHBOARD,
                        onClick = {
                            viewModel.navigateTo(AppSection.DASHBOARD)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerCategoryHeader("GESTÃO TÉCNICA")
                    DrawerItem(
                        title = "Registo (Entrada/Saída)",
                        icon = Icons.Default.Monitor,
                        badge = "${orders.count { it.status != "Entregue" }}",
                        isSelected = currentSection == AppSection.OS_ORDERS,
                        onClick = {
                            viewModel.navigateTo(AppSection.OS_ORDERS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Estoque e Venda",
                        icon = Icons.Default.SwapHoriz,
                        isSelected = currentSection == AppSection.STOCK,
                        onClick = {
                            viewModel.navigateTo(AppSection.STOCK)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Agenda Serviços",
                        icon = Icons.Default.Event,
                        isSelected = currentSection == AppSection.APPOINTMENTS,
                        onClick = {
                            viewModel.navigateTo(AppSection.APPOINTMENTS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Monitor de Temperatura",
                        icon = Icons.Default.Thermostat,
                        isSelected = currentSection == AppSection.TEMPERATURE_MONITOR,
                        onClick = {
                            viewModel.navigateTo(AppSection.TEMPERATURE_MONITOR)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerCategoryHeader("FINANCEIRO")
                    DrawerItem(
                        title = "Conta Doméstica & Oficina",
                        icon = Icons.Default.AccountBalance,
                        isSelected = currentSection == AppSection.FINANCIAL,
                        onClick = {
                            viewModel.navigateTo(AppSection.FINANCIAL)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Simulador de Crédito",
                        icon = Icons.Default.Analytics,
                        isSelected = currentSection == AppSection.CREDIT_SIMULATOR,
                        onClick = {
                            viewModel.navigateTo(AppSection.CREDIT_SIMULATOR)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Pagamento MEI (DAS)",
                        icon = Icons.Default.Payments,
                        isSelected = currentSection == AppSection.MEI_PAYMENT,
                        onClick = {
                            viewModel.navigateTo(AppSection.MEI_PAYMENT)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerCategoryHeader("VEÍCULOS & CAMPO")
                    DrawerItem(
                        title = "Preventivas Veiculares",
                        icon = Icons.Default.WaterDamage,
                        isSelected = currentSection == AppSection.VEHICLE_MAINTENANCE,
                        onClick = {
                            viewModel.navigateTo(AppSection.VEHICLE_MAINTENANCE)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Endereços & GPS Mapa",
                        icon = Icons.Default.Map,
                        isSelected = currentSection == AppSection.MAPS_GPS,
                        onClick = {
                            viewModel.navigateTo(AppSection.MAPS_GPS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerCategoryHeader("OUTROS")
                    DrawerItem(
                        title = "Links de Aulas & Estudos",
                        icon = Icons.Default.School,
                        isSelected = currentSection == AppSection.EDUCATIONAL,
                        onClick = {
                            viewModel.navigateTo(AppSection.EDUCATIONAL)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Investimento_up",
                        icon = Icons.Default.TrendingUp,
                        isSelected = currentSection == AppSection.INVESTMENTS,
                        onClick = {
                            viewModel.navigateTo(AppSection.INVESTMENTS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Configurar Splash",
                        icon = Icons.Default.Palette,
                        isSelected = currentSection == AppSection.SPLASH_CONFIG,
                        onClick = {
                            viewModel.navigateTo(AppSection.SPLASH_CONFIG)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(6.dp))

                    DrawerItem(
                        title = "Informações do Sistema",
                        icon = Icons.Default.Info,
                        isSelected = false,
                        onClick = {
                            isSystemInfoOpen = true
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Gerenciar Usuários",
                        icon = Icons.Default.ManageAccounts,
                        isSelected = false,
                        onClick = {
                            viewModel.isSecurityVerifyOpen.value = true
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        title = "Sair do Sistema",
                        icon = Icons.Default.Logout,
                        tint = DrabatecError,
                        isSelected = false,
                        onClick = {
                            viewModel.logout()
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentSection.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        if (currentSection == AppSection.DASHBOARD) {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu Principal", tint = Color.White)
                            }
                        } else {
                            IconButton(onClick = { viewModel.navigateBack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSystemInfoOpen = true }) {
                            Icon(Icons.Default.Info, contentDescription = "Sobre", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Sair", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DrabatecPurplePrimary)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentSection) {
                    AppSection.DASHBOARD -> DashboardScreen(
                        currentUsername = currentUser?.username ?: "Operador",
                        orders = orders,
                        stock = stock,
                        appointments = appointments,
                        financials = financials,
                        vehicleMaintenances = vehicleMaintenances,
                        onNavigateToSection = { section -> viewModel.navigateTo(section) }
                    )

                    AppSection.OS_ORDERS -> ServiceOrdersScreen(
                        orders = orders,
                        onSaveOrder = { order -> viewModel.saveServiceOrder(order) },
                        onUpdateStatus = { order, st -> viewModel.updateOrderStatus(order, st) },
                        onDeleteOrder = { id -> viewModel.deleteOrder(id) }
                    )

                    AppSection.STOCK -> StockInventoryScreen(
                        stockList = stock,
                        onSaveStockItem = { item -> viewModel.saveStockItem(item) },
                        onAdjustQuantity = { item, delta -> viewModel.adjustStockQuantity(item, delta) },
                        onDeleteStockItem = { id -> viewModel.deleteStockItem(id) }
                    )

                    AppSection.APPOINTMENTS -> AppointmentsScreen(
                        appointments = appointments,
                        onSaveAppointment = { apt -> viewModel.saveAppointment(apt) },
                        onToggleStatus = { apt -> viewModel.toggleAppointmentStatus(apt) },
                        onDeleteAppointment = { id -> viewModel.deleteAppointment(id) }
                    )

                    AppSection.FINANCIAL -> FinancialScreen(
                        entries = financials,
                        onSaveEntry = { entry -> viewModel.saveFinancialEntry(entry) },
                        onTogglePaid = { entry -> viewModel.toggleFinancialPaid(entry) },
                        onDeleteEntry = { id -> viewModel.deleteFinancialEntry(id) }
                    )

                    AppSection.CREDIT_SIMULATOR -> CreditSimulatorScreen(
                        loanAmount = loanAmount,
                        interestRate = interestRate,
                        installmentsCount = installmentsCount,
                        simulationResult = creditSimulationResult,
                        onLoanAmountChange = { viewModel.creditLoanAmount.value = it },
                        onInterestRateChange = { viewModel.creditInterestRate.value = it },
                        onInstallmentsCountChange = { viewModel.creditInstallmentsCount.value = it },
                        onCalculate = { viewModel.calculateCreditSimulation() }
                    )

                    AppSection.MEI_PAYMENT -> MeiPaymentScreen()

                    AppSection.VEHICLE_MAINTENANCE -> VehicleMaintenanceScreen(
                        maintenances = vehicleMaintenances,
                        onSaveMaintenance = { vm -> viewModel.saveVehicleMaintenance(vm) },
                        onDeleteMaintenance = { id -> viewModel.deleteVehicleMaintenance(id) }
                    )

                    AppSection.MAPS_GPS -> MapsGpsScreen(
                        orders = orders,
                        appointments = appointments
                    )

                    AppSection.TEMPERATURE_MONITOR -> TemperatureMonitorScreen(
                        benchTemp = benchTemp,
                        ovenTemp = ovenTemp,
                        transformerTemp = transformerTemp,
                        isAlarmActive = isAlarmActive
                    )

                    AppSection.EDUCATIONAL -> EducationalScreen()

                    AppSection.INVESTMENTS -> InvestmentSimulatorScreen(
                        initialAmount = investInitial,
                        monthlyContribution = investMonthly,
                        annualRate = investRate,
                        periodMonths = investPeriod,
                        result = investResult,
                        onInitialChange = { viewModel.investInitialAmount.value = it },
                        onMonthlyChange = { viewModel.investMonthlyContribution.value = it },
                        onRateChange = { viewModel.investAnnualRate.value = it },
                        onPeriodChange = { viewModel.investPeriodMonths.value = it },
                        onCalculate = { viewModel.calculateInvestmentSimulation() }
                    )

                    AppSection.SPLASH_CONFIG -> SplashConfigScreen(
                        onSaved = { viewModel.navigateTo(AppSection.DASHBOARD) }
                    )
                }
            }
        }
    }

    // System Information Dialog
    SystemInfoDialog(
        isOpen = isSystemInfoOpen,
        currentUser = currentUser,
        onDismiss = { isSystemInfoOpen = false }
    )

    // Security Verification Dialog
    SecurityVerifyDialog(
        isOpen = isSecurityVerifyOpen,
        errorMessage = securityError,
        onDismiss = { viewModel.isSecurityVerifyOpen.value = false },
        onConfirm = { pass ->
            viewModel.verifySecurityPassword(pass) {
                viewModel.prepareNewUserForm()
                viewModel.isUserConfigOpen.value = true
            }
        }
    )

    // User Management Modal
    UserManagementDialog(
        isOpen = isUserConfigOpen,
        users = users,
        editingUserId = editingUserId,
        usernameValue = formUsername,
        passwordValue = formPassword,
        confirmPasswordValue = formConfirmPassword,
        statusMessage = userStatusMessage,
        isSuccess = userStatusIsSuccess,
        onUsernameChange = { viewModel.formUsername.value = it },
        onPasswordChange = { viewModel.formPassword.value = it },
        onConfirmPasswordChange = { viewModel.formConfirmPassword.value = it },
        onNewUserClick = { viewModel.prepareNewUserForm() },
        onEditUserClick = { user -> viewModel.prepareEditUser(user) },
        onDeleteUserClick = { id -> viewModel.deleteUser(id) },
        onSaveClick = { viewModel.saveUserForm() },
        onDismiss = { viewModel.isUserConfigOpen.value = false }
    )
}

@Composable
fun DrawerCategoryHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = DrabatecPurpleDark,
        modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    badge: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DrabatecPurpleLight else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) DrabatecPurplePrimary else tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) DrabatecPurplePrimary else tint,
                modifier = Modifier.weight(1f)
            )

            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DrabatecPurplePrimary
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
