package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.StockItemEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInventoryScreen(
    stockList: List<StockItemEntity>,
    onSaveStockItem: (StockItemEntity) -> Unit,
    onAdjustQuantity: (StockItemEntity, Int) -> Unit,
    onDeleteStockItem: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<StockItemEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    val filteredStock = stockList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemToEdit = null
                    isAddDialogOpen = true
                },
                containerColor = DrabatecPurplePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Item")
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
                placeholder = { Text("Buscar peça, código ou categoria...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DrabatecPurplePrimary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stock Count Summary
            val lowStockCount = stockList.count { it.quantity <= it.minQuantity }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catálogo de Peças (${filteredStock.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (lowStockCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DrabatecError.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = DrabatecError,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$lowStockCount abaixo do mínimo",
                                color = DrabatecError,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredStock, key = { it.id }) { item ->
                    StockItemCard(
                        item = item,
                        currencyFormat = currencyFormat,
                        onIncrement = { onAdjustQuantity(item, 1) },
                        onDecrement = { onAdjustQuantity(item, -1) },
                        onEdit = {
                            itemToEdit = item
                            isAddDialogOpen = true
                        },
                        onDelete = { itemToDelete = item.id }
                    )
                }
            }
        }
    }

    if (isAddDialogOpen) {
        StockFormDialog(
            initialItem = itemToEdit,
            onDismiss = { isAddDialogOpen = false },
            onSave = { savedItem ->
                onSaveStockItem(savedItem)
                isAddDialogOpen = false
            }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = DrabatecError) },
            title = { Text("Excluir Item do Estoque") },
            text = { Text("Tem certeza que deseja remover esta peça do catálogo?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteStockItem(it) }
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
fun StockItemCard(
    item: StockItemEntity,
    currencyFormat: NumberFormat,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = item.quantity <= item.minQuantity

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DrabatecPurpleLight
                    ) {
                        Text(
                            text = item.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DrabatecPurpleDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isLowStock) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DrabatecError.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Estoque Baixo",
                            color = DrabatecError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Venda: ${currencyFormat.format(item.sellPrice)} (Custo: ${currencyFormat.format(item.costPrice)})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Mínimo recomendado: ${item.minQuantity} ${item.unit}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Quantity Counter with Plus/Minus
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(32.dp),
                        enabled = item.quantity > 0
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Diminuir", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "${item.quantity} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isLowStock) DrabatecError else DrabatecPurpleDark,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    FilledTonalIconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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

@Composable
fun StockFormDialog(
    initialItem: StockItemEntity?,
    onDismiss: () -> Unit,
    onSave: (StockItemEntity) -> Unit
) {
    var code by remember { mutableStateOf(initialItem?.code ?: "") }
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Eletrônica") }
    var quantity by remember { mutableStateOf(initialItem?.quantity?.toString() ?: "1") }
    var minQuantity by remember { mutableStateOf(initialItem?.minQuantity?.toString() ?: "3") }
    var costPrice by remember { mutableStateOf(initialItem?.costPrice?.toString() ?: "0.00") }
    var sellPrice by remember { mutableStateOf(initialItem?.sellPrice?.toString() ?: "0.00") }
    var unit by remember { mutableStateOf(initialItem?.unit ?: "UN") }

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
                    text = if (initialItem != null) "Editar Item" else "Cadastrar Peça / Produto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DrabatecPurpleDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Código *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Descrição / Nome da Peça *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Estoque Atual") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minQuantity,
                        onValueChange = { minQuantity = it },
                        label = { Text("Mínimo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        label = { Text("Custo (R$)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sellPrice,
                        onValueChange = { sellPrice = it },
                        label = { Text("Venda (R$)") },
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
                            val id = initialItem?.id ?: "PEC-${System.currentTimeMillis() % 100000}"
                            onSave(
                                StockItemEntity(
                                    id = id,
                                    code = code.ifBlank { "PEC-0" },
                                    name = name.ifBlank { "Peça Sem Nome" },
                                    category = category.ifBlank { "Geral" },
                                    quantity = quantity.toIntOrNull() ?: 0,
                                    minQuantity = minQuantity.toIntOrNull() ?: 2,
                                    costPrice = costPrice.toDoubleOrNull() ?: 0.0,
                                    sellPrice = sellPrice.toDoubleOrNull() ?: 0.0,
                                    unit = unit
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
