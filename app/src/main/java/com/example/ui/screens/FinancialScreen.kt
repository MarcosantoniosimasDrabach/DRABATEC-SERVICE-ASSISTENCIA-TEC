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
import com.example.data.model.FinancialEntryEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    entries: List<FinancialEntryEntity>,
    onSaveEntry: (FinancialEntryEntity) -> Unit,
    onTogglePaid: (FinancialEntryEntity) -> Unit,
    onDeleteEntry: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    val totalReceitas = entries.filter { it.type == "RECEITA" && it.isPaid }.sumOf { it.amount }
    val totalDespesas = entries.filter { it.type == "DESPESA" && it.isPaid }.sumOf { it.amount }
    val saldoLiquido = totalReceitas - totalDespesas

    val filteredEntries = entries.filter {
        when (selectedFilter) {
            "Receitas" -> it.type == "RECEITA"
            "Despesas" -> it.type == "DESPESA"
            "Pendentes" -> !it.isPaid
            "Pagos" -> it.isPaid
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = DrabatecPurplePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Lançamento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Financial Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Receitas Pagas", fontSize = 11.sp, color = DrabatecGreenSuccess)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            currencyFormat.format(totalReceitas),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DrabatecGreenSuccess
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Despesas Pagas", fontSize = 11.sp, color = DrabatecError)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            currencyFormat.format(totalDespesas),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DrabatecError
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Saldo Caixa", fontSize = 11.sp, color = DrabatecPurpleDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            currencyFormat.format(saldoLiquido),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (saldoLiquido >= 0) DrabatecPurpleDark else DrabatecError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
            val filterOptions = listOf("Todos", "Receitas", "Despesas", "Pendentes", "Pagos")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { opt ->
                    FilterChip(
                        selected = selectedFilter == opt,
                        onClick = { selectedFilter = opt },
                        label = { Text(opt) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DrabatecPurplePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nenhum lançamento no momento", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredEntries, key = { it.id }) { item ->
                        FinancialCard(
                            entry = item,
                            currencyFormat = currencyFormat,
                            onTogglePaid = { onTogglePaid(item) },
                            onDelete = { itemToDelete = item.id }
                        )
                    }
                }
            }
        }
    }

    if (isAddDialogOpen) {
        FinancialFormDialog(
            onDismiss = { isAddDialogOpen = false },
            onSave = { saved ->
                onSaveEntry(saved)
                isAddDialogOpen = false
            }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = DrabatecError) },
            title = { Text("Excluir Lançamento") },
            text = { Text("Deseja realmente remover esta conta financeira?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteEntry(it) }
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
fun FinancialCard(
    entry: FinancialEntryEntity,
    currencyFormat: NumberFormat,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit
) {
    val isReceita = entry.type == "RECEITA"
    val color = if (isReceita) DrabatecGreenSuccess else DrabatecError

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = entry.isPaid,
                    onCheckedChange = { onTogglePaid() },
                    colors = CheckboxDefaults.colors(checkedColor = DrabatecPurplePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = entry.description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${entry.category} • Vencimento: ${entry.dueDate} • ${entry.paymentMethod}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isReceita) "+" else "-"} ${currencyFormat.format(entry.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (entry.isPaid) DrabatecGreenSuccess.copy(alpha = 0.15f) else DrabatecWarning.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (entry.isPaid) "Pago" else "Pendente",
                        color = if (entry.isPaid) DrabatecGreenSuccess else DrabatecWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = DrabatecError)
            }
        }
    }
}

@Composable
fun FinancialFormDialog(
    onDismiss: () -> Unit,
    onSave: (FinancialEntryEntity) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Serviços") }
    var amount by remember { mutableStateOf("100.00") }
    var type by remember { mutableStateOf("RECEITA") }
    var dueDate by remember { mutableStateOf("15/09/2026") }
    var isPaid by remember { mutableStateOf(true) }
    var paymentMethod by remember { mutableStateOf("PIX") }

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
                    text = "Novo Lançamento Financeiro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DrabatecPurpleDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == "RECEITA",
                        onClick = { type = "RECEITA" },
                        label = { Text("Receita (+)") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DrabatecGreenSuccess,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = type == "DESPESA",
                        onClick = { type = "DESPESA" },
                        label = { Text("Despesa (-)") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DrabatecError,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Valor (R$) *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Data Vencimento") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = { paymentMethod = it },
                        label = { Text("Forma Pagamento") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPaid,
                        onCheckedChange = { isPaid = it },
                        colors = CheckboxDefaults.colors(checkedColor = DrabatecPurplePrimary)
                    )
                    Text("Já foi pago / recebido")
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
                            val id = "FIN-${System.currentTimeMillis() % 100000}"
                            onSave(
                                FinancialEntryEntity(
                                    id = id,
                                    description = description.ifBlank { "Lançamento Geral" },
                                    category = category,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    type = type,
                                    dueDate = dueDate,
                                    isPaid = isPaid,
                                    paymentMethod = paymentMethod
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
