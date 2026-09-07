package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun TemperatureMonitorScreen(
    benchTemp: Float,
    ovenTemp: Float,
    transformerTemp: Float,
    isAlarmActive: Boolean
) {
    var selectedUnit by remember { mutableStateOf("°C") }

    fun convertTemp(celsius: Float): String {
        return when (selectedUnit) {
            "°F" -> String.format(java.util.Locale.US, "%.1f °F", celsius * 9 / 5 + 32)
            "K" -> String.format(java.util.Locale.US, "%.1f K", celsius + 273.15)
            else -> String.format(java.util.Locale.US, "%.1f °C", celsius)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                            Icon(Icons.Default.Thermostat, contentDescription = null, tint = DrabatecPurplePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Painel Térmico da Oficina",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DrabatecPurpleDark
                            )
                        }

                        // Unit Selector
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("°C", "°F", "K").forEach { u ->
                                FilterChip(
                                    selected = selectedUnit == u,
                                    onClick = { selectedUnit = u },
                                    label = { Text(u, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sensores de telemetria em tempo real para monitoramento da bancada de eletrônica, estufa de secagem de verniz e transformador de força.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sensor 1: Bancada de Testes
        item {
            TemperatureGaugeCard(
                name = "Bancada de Manutenção e Testes",
                location = "Laboratório de Eletrônica",
                currentTemp = benchTemp,
                formattedTemp = convertTemp(benchTemp),
                safeRange = "20°C a 30°C",
                status = if (benchTemp in 18f..32f) "Normal" else "Atenção",
                statusColor = if (benchTemp in 18f..32f) DrabatecGreenSuccess else DrabatecWarning,
                percentage = ((benchTemp - 15f) / (45f - 15f)).coerceIn(0f, 1f)
            )
        }

        // Sensor 2: Estufa de Secagem
        item {
            TemperatureGaugeCard(
                name = "Estufa de Secagem e Resina de Motores",
                location = "Cabine Térmica",
                currentTemp = ovenTemp,
                formattedTemp = convertTemp(ovenTemp),
                safeRange = "90°C a 125°C",
                status = if (ovenTemp <= 125f) "Operação Normal" else "ALERTA TÉRMICO",
                statusColor = if (ovenTemp <= 125f) DrabatecBlueAccent else DrabatecError,
                percentage = ((ovenTemp - 50f) / (150f - 50f)).coerceIn(0f, 1f)
            )
        }

        // Sensor 3: Transformador de Força
        item {
            TemperatureGaugeCard(
                name = "Transformador Principal da Oficina",
                location = "Subestação de Entrada",
                currentTemp = transformerTemp,
                formattedTemp = convertTemp(transformerTemp),
                safeRange = "30°C a 55°C",
                status = if (transformerTemp <= 55f) "Normal" else "Sobrecarga",
                statusColor = if (transformerTemp <= 55f) DrabatecGreenSuccess else DrabatecError,
                percentage = ((transformerTemp - 20f) / (80f - 20f)).coerceIn(0f, 1f)
            )
        }

        if (isAlarmActive) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DrabatecError.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = DrabatecError)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Atenção: Um ou mais sensores excederam os limites de temperatura segura de operação.",
                            color = DrabatecError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TemperatureGaugeCard(
    name: String,
    location: String,
    currentTemp: Float,
    formattedTemp: String,
    safeRange: String,
    status: String,
    statusColor: Color,
    percentage: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedTemp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = statusColor
                )

                Text(
                    text = "Faixa segura: $safeRange",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
