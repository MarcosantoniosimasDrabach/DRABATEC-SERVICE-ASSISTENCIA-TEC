package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ServiceOrderEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOrdersScreen(
    orders: List<ServiceOrderEntity>,
    onSaveOrder: (ServiceOrderEntity) -> Unit,
    onUpdateStatus: (ServiceOrderEntity, String) -> Unit,
    onDeleteOrder: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("Todos") }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var orderToEdit by remember { mutableStateOf<ServiceOrderEntity?>(null) }
    var orderToDelete by remember { mutableStateOf<String?>(null) }

    val statusOptions = listOf("Todos", "Aberto", "Em Análise", "Aguardando Peça", "Concluído", "Entregue")
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    val filteredOrders = orders.filter { order ->
        val matchesSearch = order.equipment.contains(searchQuery, ignoreCase = true) ||
                order.clientName.contains(searchQuery, ignoreCase = true) ||
                order.id.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatusFilter == "Todos") true else order.status == selectedStatusFilter
        matchesSearch && matchesStatus
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    orderToEdit = null
                    isAddDialogOpen = true
                },
                containerColor = DrabatecPurplePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Ordem de Serviço")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por equipamento, cliente ou OS...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DrabatecPurplePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statusOptions) { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DrabatecPurplePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Orders List
            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Nenhuma ordem de serviço encontrada", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        ServiceOrderCard(
                            order = order,
                            currencyFormat = currencyFormat,
                            dateFormat = dateFormat,
                            onEdit = {
                                orderToEdit = order
                                isAddDialogOpen = true
                            },
                            onStatusChange = { newStatus -> onUpdateStatus(order, newStatus) },
                            onDelete = { orderToDelete = order.id }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Order Dialog
    if (isAddDialogOpen) {
        OrderFormDialog(
            initialOrder = orderToEdit,
            onDismiss = { isAddDialogOpen = false },
            onSave = { savedOrder ->
                onSaveOrder(savedOrder)
                isAddDialogOpen = false
            }
        )
    }

    // Delete Confirmation
    if (orderToDelete != null) {
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = DrabatecError) },
            title = { Text("Excluir Ordem de Serviço") },
            text = { Text("Deseja realmente excluir este registro?") },
            confirmButton = {
                Button(
                    onClick = {
                        orderToDelete?.let { onDeleteOrder(it) }
                        orderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DrabatecError)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { orderToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ServiceOrderCard(
    order: ServiceOrderEntity,
    currencyFormat: NumberFormat,
    dateFormat: SimpleDateFormat,
    onEdit: () -> Unit,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val (statusBg, statusFg) = when (order.status) {
        "Aberto" -> DrabatecBlueAccent.copy(alpha = 0.15f) to DrabatecBlueAccent
        "Em Análise" -> DrabatecWarning.copy(alpha = 0.15f) to DrabatecWarning
        "Aguardando Peça" -> Color(0xFFE67E22).copy(alpha = 0.15f) to Color(0xFFE67E22)
        "Concluído" -> DrabatecGreenSuccess.copy(alpha = 0.15f) to DrabatecGreenSuccess
        "Entregue" -> DrabatecPurplePrimary.copy(alpha = 0.15f) to DrabatecPurplePrimary
        else -> Color.LightGray to Color.DarkGray
    }

    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DrabatecPurpleDark
                )

                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusBg,
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = order.status,
                                color = statusFg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = statusFg,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("Aberto", "Em Análise", "Aguardando Peça", "Concluído", "Entregue").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    onStatusChange(st)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.equipment,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = DrabatecPurplePrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${order.clientName} • ${order.clientPhone}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Defeito: ${order.reportedFault}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (order.technicalDiagnosis.isNotEmpty()) {
                Text(
                    text = "Laudo: ${order.technicalDiagnosis}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Valor do Serviço:",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = currencyFormat.format(order.totalValue),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DrabatecGreenSuccess
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = DrabatecPurplePrimary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = DrabatecError)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderFormDialog(
    initialOrder: ServiceOrderEntity?,
    onDismiss: () -> Unit,
    onSave: (ServiceOrderEntity) -> Unit
) {
    var equipment by remember { mutableStateOf(initialOrder?.equipment ?: "") }
    var clientName by remember { mutableStateOf(initialOrder?.clientName ?: "") }
    var clientPhone by remember { mutableStateOf(initialOrder?.clientPhone ?: "") }
    var reportedFault by remember { mutableStateOf(initialOrder?.reportedFault ?: "") }
    var technicalDiagnosis by remember { mutableStateOf(initialOrder?.technicalDiagnosis ?: "") }
    var status by remember { mutableStateOf(initialOrder?.status ?: "Aberto") }
    var totalValue by remember { mutableStateOf(initialOrder?.totalValue?.toString() ?: "0.00") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = if (initialOrder != null) "Editar Ordem de Serviço" else "Nova Ordem de Serviço",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DrabatecPurpleDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = equipment,
                    onValueChange = { equipment = it },
                    label = { Text("Equipamento *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Cliente *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { clientPhone = it },
                        label = { Text("Telefone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reportedFault,
                    onValueChange = { reportedFault = it },
                    label = { Text("Defeito Relatado") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = technicalDiagnosis,
                    onValueChange = { technicalDiagnosis = it },
                    label = { Text("Diagnóstico / Solução") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = totalValue,
                    onValueChange = { totalValue = it },
                    label = { Text("Valor Total (R$)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val id = initialOrder?.id ?: "OS-${SimpleDateFormat("yyyy-MMdd-HHmm", Locale.getDefault()).format(Date())}"
                            onSave(
                                ServiceOrderEntity(
                                    id = id,
                                    equipment = equipment.ifBlank { "Equipamento Sem Nome" },
                                    clientName = clientName.ifBlank { "Cliente Balcão" },
                                    clientPhone = clientPhone,
                                    reportedFault = reportedFault,
                                    technicalDiagnosis = technicalDiagnosis,
                                    status = status,
                                    totalValue = totalValue.toDoubleOrNull() ?: 0.0,
                                    entryDate = initialOrder?.entryDate ?: System.currentTimeMillis()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DrabatecPurplePrimary)
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
