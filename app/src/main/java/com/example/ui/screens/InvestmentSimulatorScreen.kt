package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.InvestmentSimulationResult
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InvestmentSimulatorScreen(
    initialAmount: String,
    monthlyContribution: String,
    annualRate: String,
    periodMonths: String,
    result: InvestmentSimulationResult?,
    onInitialChange: (String) -> Unit,
    onMonthlyChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onPeriodChange: (String) -> Unit,
    onCalculate: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = DrabatecPurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Investimento_up - Simulador",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DrabatecPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Planeje o crescimento do patrimônio e reserva de emergência da oficina com juros compostos.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = initialAmount,
                            onValueChange = {
                                onInitialChange(it)
                                onCalculate()
                            },
                            label = { Text("Aporte Inicial (R$)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = monthlyContribution,
                            onValueChange = {
                                onMonthlyChange(it)
                                onCalculate()
                            },
                            label = { Text("Aporte Mensal (R$)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = annualRate,
                            onValueChange = {
                                onRateChange(it)
                                onCalculate()
                            },
                            label = { Text("Taxa Anual (% a.a)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = periodMonths,
                            onValueChange = {
                                onPeriodChange(it)
                                onCalculate()
                            },
                            label = { Text("Prazo (Meses)") },
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
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simular Rentabilidade")
                    }
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DrabatecPurpleLight.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Resultado da Projeção",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DrabatecPurpleDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Investido do Bolso:", fontSize = 13.sp)
                            Text(
                                currencyFormat.format(result.totalInvested),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Juros Ganhos (Lucro Líquido aprox):", fontSize = 13.sp)
                            Text(
                                currencyFormat.format(result.totalInterest),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DrabatecGreenSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Montante Bruto Acumulado:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                currencyFormat.format(result.grossTotal),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = DrabatecPurpleDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Comparison with savings account
                        val diff = result.grossTotal - result.savingsComparison
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DrabatecGreenSuccess.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Comparativo com a Poupança:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DrabatecGreenSuccess
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Na poupança você teria ${currencyFormat.format(result.savingsComparison)}. No Investimento_up você lucra ${currencyFormat.format(diff)} a mais!",
                                    fontSize = 11.sp,
                                    color = Color(0xFF196F3D)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
