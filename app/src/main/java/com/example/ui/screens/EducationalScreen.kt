package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CourseModule(
    val id: String,
    val title: String,
    val instructor: String,
    val duration: String,
    val description: String,
    val videoUrl: String,
    val category: String,
    var isCompleted: Boolean = false
)

@Composable
fun EducationalScreen() {
    val context = LocalContext.current

    val coursesList = remember {
        mutableStateListOf(
            CourseModule(
                id = "C01",
                title = "Manutenção e Reparo de Inversores de Frequência",
                instructor = "Drabatec Capacitação Técnica",
                duration = "45 min",
                description = "Diagnóstico de erro E04/E08, teste de bancada com ponta de prova osciloscópio, troca de módulo IGBT e circuitos de disparo optoacoplados.",
                videoUrl = "https://www.youtube.com/results?search_query=reparo+inversor+de+frequencia+curso",
                category = "Eletrônica Industrial",
                isCompleted = true
            ),
            CourseModule(
                id = "C02",
                title = "Rebobinamento e Diagnóstico de Motores Trifásicos",
                instructor = "Engenharia Aplicada Drabatec",
                duration = "1h 10min",
                description = "Cálculo de espiras, classe de isolamento H, teste de fuga com megômetro e fechamento estrela/triângulo.",
                videoUrl = "https://www.youtube.com/results?search_query=rebobinamento+motores+eletricos",
                category = "Máquinas Elétricas",
                isCompleted = true
            ),
            CourseModule(
                id = "C03",
                title = "Fontes Chaveadas Flyback e PFC Ativo",
                instructor = "Prof. Eletrônica de Potência",
                duration = "55 min",
                description = "Topologias de conversores chaveados, PWM TL494/UC3842, snubber RCD e testes com lâmpada série.",
                videoUrl = "https://www.youtube.com/results?search_query=reparo+fonte+chaveada+osciloscopio",
                category = "Eletrônica",
                isCompleted = false
            ),
            CourseModule(
                id = "C04",
                title = "Segurança em Instalações e Serviços em Eletricidade (NR-10)",
                instructor = "Equipe Segurança do Trabalho",
                duration = "1h 30min",
                description = "Procedimentos operacionais, bloqueio LOTO, EPI/EPC e desenergização segura de painéis.",
                videoUrl = "https://www.youtube.com/results?search_query=nr10+treinamento+eletricidade",
                category = "Normas Regulamentadoras",
                isCompleted = true
            ),
            CourseModule(
                id = "C05",
                title = "Manutenção Preventiva de Compressores Industriais",
                instructor = "Mecânica Técnica Drabatec",
                duration = "40 min",
                description = "Válvula de retenção, pressostato, troca de óleo ISO e alinhamento de polias.",
                videoUrl = "https://www.youtube.com/results?search_query=manutencao+compressor+chiaperini",
                category = "Pneumática",
                isCompleted = false
            )
        )
    }

    val completedCount = coursesList.count { it.isCompleted }
    val progressPercent = (completedCount.toFloat() / coursesList.size.toFloat())

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
                        Icon(Icons.Default.School, contentDescription = null, tint = DrabatecPurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Portal Educacional Drabatec",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DrabatecPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Links de aulas, treinamentos técnicos e gestão de estudos continuados para a equipe técnica.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progresso dos Estudos: $completedCount de ${coursesList.size} concluídos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(progressPercent * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DrabatecPurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = DrabatecPurplePrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Módulos de Aula Disponíveis (${coursesList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(coursesList) { course ->
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DrabatecPurpleLight
                        ) {
                            Text(
                                text = course.category,
                                color = DrabatecPurpleDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = course.isCompleted,
                                onCheckedChange = { isChecked ->
                                    val index = coursesList.indexOfFirst { it.id == course.id }
                                    if (index != -1) {
                                        coursesList[index] = coursesList[index].copy(isCompleted = isChecked)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = DrabatecPurplePrimary)
                            )
                            Text(
                                text = if (course.isCompleted) "Concluído" else "Em curso",
                                fontSize = 11.sp,
                                color = if (course.isCompleted) DrabatecGreenSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = course.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${course.instructor} • Duração: ${course.duration}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = course.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(course.videoUrl))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DrabatecPurplePrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Assistir Aula / Acessar Material")
                    }
                }
            }
        }
    }
}
