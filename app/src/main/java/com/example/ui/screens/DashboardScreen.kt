package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppSection
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    currentUsername: String,
    orders: List<ServiceOrderEntity>,
    stock: List<StockItemEntity>,
    appointments: List<AppointmentEntity>,
    financials: List<FinancialEntryEntity>,
    vehicleMaintenances: List<VehicleMaintenanceEntity>,
    onNavigateToSection: (AppSection) -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    val activeOrdersCount = orders.count { it.status != "Entregue" }
    val lowStockCount = stock.count { it.quantity <= it.minQuantity }
    val pendingAppointmentsCount = appointments.count { it.status == "Agendado" }

    val totalReceitas = financials.filter { it.type == "RECEITA" && it.isPaid }.sumOf { it.amount }
    val totalDespesas = financials.filter { it.type == "DESPESA" && it.isPaid }.sumOf { it.amount }
    val saldoLiquido = totalReceitas - totalDespesas

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(DrabatecPurpleDark, DrabatecPurplePrimary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Bem-vindo,",
                                    color = DrabatecPurpleLight,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = currentUsername,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(DrabatecGreenSuccess)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Online",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Sistema Integrado de Manutenção e Assistência Técnica Drabatec",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Os 4 Botões Rápidos em Destaque (Faithfully matching web version)
        item {
            Text(
                text = "Ações Rápidas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2x2 Grid of Quick Actions
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        title = "Registo (Entrada/Saída)",
                        icon = Icons.Default.Monitor,
                        badgeText = "$activeOrdersCount ativas",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection(AppSection.OS_ORDERS) }
                    )
                    QuickActionButton(
                        title = "Estoque e Venda",
                        icon = Icons.Default.SwapHoriz,
                        badgeText = if (lowStockCount > 0) "$lowStockCount repor" else "${stock.size} itens",
                        badgeColor = if (lowStockCount > 0) DrabatecError else DrabatecGreenSuccess,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection(AppSection.STOCK) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        title = "Agenda Serviços",
                        icon = Icons.Default.Event,
                        badgeText = "$pendingAppointmentsCount agendados",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection(AppSection.APPOINTMENTS) }
                    )
                    QuickActionButton(
                        title = "Preventivas Veiculares",
                        icon = Icons.Default.WaterDamage,
                        badgeText = "${vehicleMaintenances.size} itens",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection(AppSection.VEHICLE_MAINTENANCE) }
                    )
                }
            }
        }

        // Indicadores do Sistema (KPIs)
        item {
            Text(
                text = "Resumo Geral",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Saldo Caixa
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = DrabatecGreenSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Saldo em Caixa",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currencyFormat.format(saldoLiquido),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (saldoLiquido >= 0) DrabatecGreenSuccess else DrabatecError
                        )
                    }
                }

                // Ordens de Serviço
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                tint = DrabatecBlueAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Ordens",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${orders.size} cadastros",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Outros Módulos do Sistema
        item {
            Text(
                text = "Módulos Integrados",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModuleRowItem(
                    title = "Conta Doméstica & Financeiro",
                    subtitle = "Contas a pagar e receber da oficina",
                    icon = Icons.Default.AccountBalance,
                    category = "Financeiro",
                    onClick = { onNavigateToSection(AppSection.FINANCIAL) }
                )
                ModuleRowItem(
                    title = "Simulador de Crédito & Juros",
                    subtitle = "Cálculo de parcelas e financiamento Price",
                    icon = Icons.Default.Analytics,
                    category = "Financeiro",
                    onClick = { onNavigateToSection(AppSection.CREDIT_SIMULATOR) }
                )
                ModuleRowItem(
                    title = "Pagamento MEI (DAS)",
                    subtitle = "Guia mensal do microempreendedor individual",
                    icon = Icons.Default.Payments,
                    category = "Financeiro",
                    onClick = { onNavigateToSection(AppSection.MEI_PAYMENT) }
                )
                ModuleRowItem(
                    title = "Monitor de Temperatura",
                    subtitle = "Telemetria da bancada, estufa e transformador",
                    icon = Icons.Default.Thermostat,
                    category = "Gestão",
                    onClick = { onNavigateToSection(AppSection.TEMPERATURE_MONITOR) }
                )
                ModuleRowItem(
                    title = "Links de Aulas & Estudos",
                    subtitle = "Cursos técnicos e capacitação profissional",
                    icon = Icons.Default.School,
                    category = "Educacional",
                    onClick = { onNavigateToSection(AppSection.EDUCATIONAL) }
                )
                ModuleRowItem(
                    title = "Investimento_up",
                    subtitle = "Simulador e projeção de rendimentos",
                    icon = Icons.Default.TrendingUp,
                    category = "Investimentos",
                    onClick = { onNavigateToSection(AppSection.INVESTMENTS) }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    badgeText: String,
    badgeColor: Color = DrabatecPurpleDark,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DrabatecPurpleLight.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DrabatecPurplePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badgeText,
                fontSize = 10.sp,
                color = badgeColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModuleRowItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    category: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DrabatecPurpleLight.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DrabatecPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DrabatecPurpleLight.copy(alpha = 0.5f)
            ) {
                Text(
                    text = category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = DrabatecPurpleDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
