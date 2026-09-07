package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CreditSimulationResult
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CreditSimulatorScreen(
    loanAmount: String,
    interestRate: String,
    installmentsCount: String,
    simulationResult: CreditSimulationResult?,
    onLoanAmountChange: (String) -> Unit,
    onInterestRateChange: (String) -> Unit,
    onInstallmentsCountChange: (String) -> Unit,
    onCalculate: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = DrabatecPurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulador de Crédito & Financiamento",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DrabatecPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = loanAmount,
                        onValueChange = {
                            onLoanAmountChange(it)
                            onCalculate()
                        },
                        label = { Text("Valor Financiado (R$)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = interestRate,
                            onValueChange = {
                                onInterestRateChange(it)
                                onCalculate()
                            },
                            label = { Text("Taxa Juros (% a.m.)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = installmentsCount,
                            onValueChange = {
                                onInstallmentsCountChange(it)
                                onCalculate()
                            },
                            label = { Text("Nº Parcelas (Meses)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCalculate,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DrabatecPurplePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recalcular Simulação")
                    }
                }
            }
        }

        // Result Card
        if (simulationResult != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DrabatecPurpleLight.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Resultado do Cálculo (Tabela Price)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DrabatecPurpleDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Valor da Parcela Mensal:", fontSize = 13.sp)
                            Text(
                                currencyFormat.format(simulationResult.monthlyPayment),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DrabatecPurpleDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pago ao Final:", fontSize = 13.sp)
                            Text(
                                currencyFormat.format(simulationResult.totalPaid),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total de Juros Pagos:", fontSize = 13.sp)
                            Text(
                                currencyFormat.format(simulationResult.totalInterest),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DrabatecError
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Detalhamento Mês a Mês",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            items(simulationResult.installments) { inst ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DrabatecPurpleLight
                        ) {
                            Text(
                                text = "Mês ${inst.number}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = DrabatecPurpleDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = currencyFormat.format(inst.payment),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Amortiz: ${currencyFormat.format(inst.principal)} • Juros: ${currencyFormat.format(inst.interest)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Saldo Devedor: ${currencyFormat.format(inst.balance)}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
