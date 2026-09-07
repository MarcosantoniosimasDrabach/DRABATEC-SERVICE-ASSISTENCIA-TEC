package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

data class MeiMonth(
    val monthNumber: Int,
    val monthName: String,
    val dueDate: String,
    val amount: Double = 75.60,
    var isPaid: Boolean
)

@Composable
fun MeiPaymentScreen() {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    var copiedMessage by remember { mutableStateOf<String?>(null) }

    val monthsList = remember {
        mutableStateListOf(
            MeiMonth(1, "Janeiro / 2026", "20/02/2026", isPaid = true),
            MeiMonth(2, "Fevereiro / 2026", "20/03/2026", isPaid = true),
            MeiMonth(3, "Março / 2026", "20/04/2026", isPaid = true),
            MeiMonth(4, "Abril / 2026", "20/05/2026", isPaid = true),
            MeiMonth(5, "Maio / 2026", "20/06/2026", isPaid = true),
            MeiMonth(6, "Junho / 2026", "20/07/2026", isPaid = true),
            MeiMonth(7, "Julho / 2026", "20/08/2026", isPaid = true),
            MeiMonth(8, "Agosto / 2026", "20/09/2026", isPaid = true),
            MeiMonth(9, "Setembro / 2026", "20/10/2026", isPaid = false),
            MeiMonth(10, "Outubro / 2026", "20/11/2026", isPaid = false),
            MeiMonth(11, "Novembro / 2026", "20/12/2026", isPaid = false),
            MeiMonth(12, "Dezembro / 2026", "20/01/2027", isPaid = false)
        )
    }

    val totalPago = monthsList.filter { it.isPaid }.sumOf { it.amount }
    val totalPendente = monthsList.filter { !it.isPaid }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = DrabatecPurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guia DAS-MEI (Ano Calendário 2026)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DrabatecPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Controle das contribuições previdenciárias e tributárias da Drabatec Service.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DrabatecGreenSuccess.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pago até o momento", fontSize = 11.sp, color = DrabatecGreenSuccess)
                                Text(
                                    currencyFormat.format(totalPago),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DrabatecGreenSuccess
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DrabatecWarning.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("A pagar no ano", fontSize = 11.sp, color = DrabatecWarning)
                                Text(
                                    currencyFormat.format(totalPendente),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DrabatecWarning
                                )
                            }
                        }
                    }
                }
            }
        }

        if (copiedMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DrabatecPurpleLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = copiedMessage ?: "",
                        color = DrabatecPurpleDark,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = "Competências Mensais",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(monthsList) { month ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = month.isPaid,
                            onCheckedChange = { isChecked ->
                                val index = monthsList.indexOfFirst { it.monthNumber == month.monthNumber }
                                if (index != -1) {
                                    monthsList[index] = monthsList[index].copy(isPaid = isChecked)
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = DrabatecPurplePrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = month.monthName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vence em: ${month.dueDate} • Valor: ${currencyFormat.format(month.amount)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (month.isPaid) DrabatecGreenSuccess.copy(alpha = 0.15f) else DrabatecWarning.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (month.isPaid) "Pago" else "Pendente",
                                color = if (month.isPaid) DrabatecGreenSuccess else DrabatecWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                copiedMessage = "Chave PIX / Código DAS copiado para ${month.monthName}!"
                            }
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copiar Código",
                                tint = DrabatecPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
