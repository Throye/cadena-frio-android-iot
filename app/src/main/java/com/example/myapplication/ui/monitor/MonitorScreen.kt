package com.example.myapplication.ui.monitor

import androidx.compose.foundation.Canvas
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
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
    temperature: Double? = null,
    humidity: Double? = null,
    recentAlerts: List<AlertItem> = listOf(
        AlertItem("1", "Puerta abierta detectada por más de 2 min", "Hace 2 min", isCritical = true),
        AlertItem("2", "Temperatura estabilizada a 4.2°C", "Hace 15 min"),
        AlertItem("3", "Advertencia: Variación de humedad detectada", "Hace 40 min", isCritical = true),
        AlertItem("4", "Sistema e interfaz iniciados correctamente", "Hace 1 hora"),
        AlertItem("5", "Conexión con estación IoT restablecida", "Hace 2 horas"),
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
        IndoorTemperatureCard(
            temperature = temperature,
            humidity = humidity,
        )

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
    temperature: Double?,
    humidity: Double?,
    modifier: Modifier = Modifier,
    maxLimit: Double = 8.0,
) {
    val tempVal = temperature ?: 4.2
    val inRange = tempVal <= maxLimit
    val badgeBg = if (inRange) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
    val badgeFg = if (inRange) Color(0xFF1B5E20) else Color(0xFFB71C1C)
    val badgeText = if (inRange) "EN RANGO" else "FUERA DE RANGO"

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header Row: TITULO e Indicador "EN RANGO"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEMPERATURA INTERIOR",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeBg,
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (temperature != null) {
                // Lectura principal de Temperatura e Indicador de Humedad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%.1f", temperature),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1),
                        )
                        Text(
                            text = " °C",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0D47A1),
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }

                    if (humidity != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                text = "Humedad: $humidity%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Subtítulo de Límite Máximo
                Text(
                    text = "Límite máximo permitido: ${maxLimit.toInt()} °C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Barra / Medidor de rango de temperatura
                TemperatureRangeGauge(
                    currentTemp = temperature,
                    maxLimit = maxLimit,
                )
            } else {
                Text(
                    text = "Sin datos de sensor asignados de momento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
fun TemperatureRangeGauge(
    currentTemp: Double,
    minTemp: Double = -10.0,
    maxTemp: Double = 25.0,
    maxLimit: Double = 8.0,
    modifier: Modifier = Modifier,
) {
    val fraction = ((currentTemp - minTemp) / (maxTemp - minTemp)).coerceIn(0.0, 1.0).toFloat()
    val limitFraction = ((maxLimit - minTemp) / (maxTemp - minTemp)).coerceIn(0.0, 1.0).toFloat()

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF29B6F6), // Azul claro (-10°C)
            Color(0xFF26A69A), // Turquesa (0°C)
            Color(0xFF66BB6A), // Verde (8°C)
            Color(0xFFFFA726), // Naranja (15°C)
            Color(0xFFEF5350), // Rojo (25°C)
        ),
    )

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            val totalWidth = maxWidth
            val thumbOffset = totalWidth * fraction
            val limitOffset = totalWidth * limitFraction

            // Barra con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(gradient),
            )

            // Línea punteada de límite (8°C)
            Canvas(
                modifier = Modifier.fillMaxSize(),
            ) {
                val limitX = limitOffset.toPx()
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                drawLine(
                    color = Color(0xFF333333),
                    start = Offset(limitX, 0f),
                    end = Offset(limitX, size.height),
                    strokeWidth = 4f,
                    pathEffect = pathEffect,
                )
            }

            // Indicador vertical negro de la temperatura actual
            Box(
                modifier = Modifier
                    .offset(x = (thumbOffset - 3.dp).coerceAtLeast(0.dp))
                    .width(6.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.Black),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Escala numérica (-10°, 0°, 10°, 20°, 25°)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "-10°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "0°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "10°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "20°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "25°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                if (alerts.size > 4) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = "${alerts.size} notificaciones",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState()),
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
