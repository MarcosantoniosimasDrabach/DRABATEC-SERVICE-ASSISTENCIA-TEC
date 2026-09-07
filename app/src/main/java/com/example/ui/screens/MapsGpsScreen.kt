package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppointmentEntity
import com.example.data.model.ServiceOrderEntity
import com.example.ui.theme.*

data class ClientRouteItem(
    val title: String,
    val clientName: String,
    val address: String,
    val phone: String,
    val serviceType: String
)

@Composable
fun MapsGpsScreen(
    orders: List<ServiceOrderEntity>,
    appointments: List<AppointmentEntity>
) {
    val context = LocalContext.current

    val destinations = mutableListOf(
        ClientRouteItem(
            title = "Sede Oficina Drabatec",
            clientName = "Matriz Drabatec Service",
            address = "Rua das Indústrias, 1020 - Centro Industrial",
            phone = "(11) 98765-4321",
            serviceType = "Base Operacional"
        ),
        ClientRouteItem(
            title = "Metalúrgica Progresso Ltda",
            clientName = "Eng. Carlos Mendes",
            address = "Av. Brasil, 4500 - Galpão 3 - Distrito Industrial",
            phone = "(11) 99123-4567",
            serviceType = "Manutenção de Inversor e Painel"
        ),
        ClientRouteItem(
            title = "Indústria Têxtil Santa Rita",
            clientName = "Marcos Roberto",
            address = "Rua Santa Rita, 280 - Vila Mariana",
            phone = "(11) 98877-6655",
            serviceType = "Rebobinamento Motor 15CV"
        ),
        ClientRouteItem(
            title = "Panificadora e Moenda Real",
            clientName = "Dona Helena",
            address = "Rua do Comércio, 142 - Centro",
            phone = "(11) 97711-2233",
            serviceType = "Compressor de Ar Industrial"
        )
    )

    // Add locations from active appointments
    appointments.forEach { apt ->
        if (apt.address.isNotBlank()) {
            destinations.add(
                ClientRouteItem(
                    title = "Atendimento Agendado",
                    clientName = apt.clientName,
                    address = apt.address,
                    phone = "",
                    serviceType = "${apt.serviceType} (${apt.scheduledDate} às ${apt.scheduledTime})"
                )
            )
        }
    }

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
                        Icon(Icons.Default.Map, contentDescription = null, tint = DrabatecPurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rotas & GPS Google Maps",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DrabatecPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Localização dos clientes e rotas de atendimento para os técnicos em campo. Toque em 'Iniciar Navegação' para abrir o GPS.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Destinos Cadastrados (${destinations.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(destinations) { item ->
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
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DrabatecPurpleLight
                        ) {
                            Text(
                                text = "GPS Ativo",
                                color = DrabatecPurpleDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cliente: ${item.clientName} • ${item.serviceType}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = DrabatecPurplePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.address,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (item.phone.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.phone}"))
                                    context.startActivity(callIntent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ligar", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(item.address)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    // Fallback to browser or any map app
                                    val genericMapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(genericMapIntent)
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrabatecPurplePrimary)
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Iniciar GPS", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
