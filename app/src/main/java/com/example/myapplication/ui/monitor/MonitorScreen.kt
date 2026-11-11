package com.example.myapplication.ui.monitor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

enum class SystemStatus(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val icon: ImageVector,
) {
    SAFE(
        label = "Cadena de Frío Segura",
        containerColor = Color(0xFFE8F5E9),
        contentColor = Color(0xFF1B5E20),
        icon = Icons.Default.CheckCircle,
    ),
    DOOR_OPEN(
        label = "Atención: Puerta Abierta",
        containerColor = Color(0xFFFFF3E0),
        contentColor = Color(0xFFE65100),
        icon = Icons.Default.Warning,
    ),
    COLD_LEAK_ALERT(
        label = "¡Alerta de Fuga de Frío!",
        containerColor = Color(0xFFFFEBEE),
        contentColor = Color(0xFFC62828),
        icon = Icons.Default.AcUnit,
    ),
}

enum class DoorState(
    val label: String,
    val isWarning: Boolean,
) {
    CLOSED("CERRADA", isWarning = false),
    OPEN("ABIERTA", isWarning = true),
}

data class AlertItem(
    val id: String,
    val message: String,
    val timestamp: String,
    val isCritical: Boolean = false,
)

@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    systemStatus: SystemStatus = SystemStatus.SAFE,
    doorState: DoorState = DoorState.CLOSED,
    recentAlerts: List<AlertItem> = listOf(
        AlertItem("1", "Sistema iniciado correctamente", "Hace 10 min"),
        AlertItem("2", "Puerta abierta durante 2 min", "Hace 35 min", isCritical = true),
        AlertItem("3", "Temperatura estabilizada a 4°C", "Hace 1 hora"),
    ),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Tarjeta 1: Estado del Sistema
        SystemStatusCard(status = systemStatus)

        // Tarjeta 2: Temperatura Interior
        IndoorTemperatureCard()

        // Tarjeta 3: Puerta Frigorífica
        RefrigeratorDoorCard(doorState = doorState)

        // Tarjeta 4: Alertas Recientes
        RecentAlertsCard(alerts = recentAlerts)
    }
}

@Composable
fun SystemStatusCard(
    status: SystemStatus,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Estado del sistema",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = status.containerColor,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = status.icon,
                        contentDescription = null,
                        tint = status.contentColor,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = status.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = status.contentColor,
                    )
                }
            }
        }
    }
}

@Composable
fun IndoorTemperatureCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Temperatura interior",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sin datos de sensor asignados de momento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun RefrigeratorDoorCard(
    doorState: DoorState,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (doorState.isWarning) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
    val contentColor = if (doorState.isWarning) Color(0xFFE65100) else Color(0xFF1B5E20)
    val icon = if (doorState == DoorState.OPEN) Icons.Default.DoorFront else Icons.Default.DoorBack

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Puerta Frigorífica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = containerColor,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Estado actual:",
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor.copy(alpha = 0.8f),
                        )
                        Text(
                            text = doorState.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentAlertsCard(
    alerts: List<AlertItem>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Alertas recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (alerts.isEmpty()) {
                Text(
                    text = "No hay alertas registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    alerts.forEach { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = if (alert.isCritical) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (alert.isCritical) Color(0xFFE65100) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = alert.timestamp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonitorScreenPreview() {
    MyApplicationTheme {
        MonitorScreen()
    }
}
