package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun SystemInfoDialog(
    isOpen: Boolean,
    currentUser: UserEntity?,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val integratedModules = listOf(
        "Conta Doméstica & Financeiro",
        "Simulador de Crédito & Juros",
        "Pagamento MEI (DAS)",
        "Links de Aulas & Treinamentos",
        "Registo (Entrada/Saída de OS)",
        "Gestão Acadêmica de Estudos",
        "Estoque e Venda (Peças)",
        "Preventivas Veiculares (Frota)",
        "Google Maps GPS & Roteamento",
        "Agenda Serviços & Atendimento",
        "Monitor de Temperatura & Telemetria",
        "Investimento_up & Rentabilidade"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DrabatecPurplePrimary)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Informações do Sistema",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    Text(
                        text = "DRABATEC SERVICE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DrabatecPurpleDark
                    )
                    Text(
                        text = "Sistema Integrado de Gestão Técnica v2.5",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // User Info Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DrabatecPurpleLight.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Sessão Atual:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DrabatecPurpleDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "👤 Usuário: ${currentUser?.username ?: "Convidado"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "🆔 ID: ${currentUser?.id ?: "N/A"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🔒 Nível: Administrador do Sistema",
                                fontSize = 11.sp,
                                color = DrabatecGreenSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Módulos Integrados Ativos:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    integratedModules.forEach { module ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(DrabatecGreenSuccess.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DrabatecGreenSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = module,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Banco de Dados Local: SQLite / Room Persistente • 100% Offline-First.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DrabatecPurplePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Fechar")
                    }
                }
            }
        }
    }
}
