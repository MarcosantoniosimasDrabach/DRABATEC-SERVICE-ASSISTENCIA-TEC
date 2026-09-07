package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VehicleMaintenanceEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleMaintenanceScreen(
    maintenances: List<VehicleMaintenanceEntity>,
    onSaveMaintenance: (VehicleMaintenanceEntity) -> Unit,
    onDeleteMaintenance: (String) -> Unit
) {
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = DrabatecPurplePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Manutenção")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Vehicle Fleet Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = DrabatecPurplePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Fiat Fiorino 1.4 EVO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Placa: DRB-7859 • Assistência Móvel",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DrabatecPurpleLight
                            ) {
                                Text(
                                    text = "87.400 km",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DrabatecPurpleDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Checklist de Manutenção Preventiva: Manômetro de arrefecimento, nível de óleo, correia dentada e pastilhas de freio.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Itens Preventivos Cadastrados (${maintenances.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            items(maintenances, key = { it.id }) { item ->
                VehicleMaintenanceCard(
                    item = item,
                    currencyFormat = currencyFormat,
                    onDelete = { itemToDelete = item.id }
                )
            }
        }
    }

    if (isAddDialogOpen) {
        VehicleFormDialog(
            onDismiss = { isAddDialogOpen = false },
            onSave = { saved ->
                onSaveMaintenance(saved)
                isAddDialogOpen = false
            }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = DrabatecError) },
            title = { Text("Excluir Manutenção") },
            text = { Text("Deseja remover este item de manutenção preventiva?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteMaintenance(it) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DrabatecError)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun VehicleMaintenanceCard(
    item: VehicleMaintenanceEntity,
    currencyFormat: NumberFormat,
    onDelete: () -> Unit
) {
    val (statusColor, statusBg) = when (item.status) {
        "Em Dia" -> DrabatecGreenSuccess to DrabatecGreenSuccess.copy(alpha = 0.15f)
        "Próximo" -> DrabatecWarning to DrabatecWarning.copy(alpha = 0.15f)
        else -> DrabatecError to DrabatecError.copy(alpha = 0.15f)
    }

    val kmDiff = item.nextKm - item.currentKm
    val kmText = if (kmDiff > 0) "Faltam $kmDiff km" else "Vencido por ${-kmDiff} km"

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
                    text = item.serviceType,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = item.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Última revisão: ${item.maintenanceDate} • Próxima troca aos: ${item.nextKm} km",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar toward next Km
            LinearProgressIndicator(
                progress = {
                    val progressValue = (item.currentKm.toFloat() / item.nextKm.toFloat()).coerceIn(0f, 1f)
                    progressValue
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$kmText • Custo: ${currencyFormat.format(item.cost)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = DrabatecError)
                }
            }
        }
    }
}

@Composable
fun VehicleFormDialog(
    onDismiss: () -> Unit,
    onSave: (VehicleMaintenanceEntity) -> Unit
) {
    var vehicleModel by remember { mutableStateOf("Fiat Fiorino 1.4 EVO Assistência") }
    var licensePlate by remember { mutableStateOf("DRB-7859") }
    var currentKm by remember { mutableStateOf("87400") }
    var serviceType by remember { mutableStateOf("Troca de Óleo e Filtros") }
    var nextKm by remember { mutableStateOf("92400") }
    var maintenanceDate by remember { mutableStateOf("Hoje") }
    var status by remember { mutableStateOf("Em Dia") }
    var cost by remember { mutableStateOf("250.00") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Registrar Manutenção Preventiva",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DrabatecPurpleDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Tipo de Manutenção *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentKm,
                        onValueChange = { currentKm = it },
                        label = { Text("Km Atual") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = nextKm,
                        onValueChange = { nextKm = it },
                        label = { Text("Próxima Troca (Km)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Custo (R$)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maintenanceDate,
                        onValueChange = { maintenanceDate = it },
                        label = { Text("Data da Troca") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val id = "VEI-${System.currentTimeMillis() % 100000}"
                            onSave(
                                VehicleMaintenanceEntity(
                                    id = id,
                                    vehicleModel = vehicleModel,
                                    licensePlate = licensePlate,
                                    currentKm = currentKm.toIntOrNull() ?: 87400,
                                    serviceType = serviceType.ifBlank { "Revisão Geral" },
                                    nextKm = nextKm.toIntOrNull() ?: 90000,
                                    maintenanceDate = maintenanceDate,
                                    status = status,
                                    cost = cost.toDoubleOrNull() ?: 0.0,
                                    notes = notes
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
