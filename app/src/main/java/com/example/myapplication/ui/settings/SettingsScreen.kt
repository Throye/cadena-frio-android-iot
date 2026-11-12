package com.example.myapplication.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.ui.theme.MyApplicationTheme

val TealAccent = Color(0xFF006666)

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onTempMaxLimitChange: (Double) -> Unit,
    onHumidityMinLimitChange: (Double) -> Unit,
    onHumidityMaxLimitChange: (Double) -> Unit,
    onWarnHumidityChange: (Boolean) -> Unit,
    onPushNotificationsChange: (Boolean) -> Unit,
    onDoorAlertChange: (Boolean) -> Unit,
    onAlarmSoundChange: (Boolean) -> Unit,
    onResetToFactory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Tarjeta 1: UMBRAL DE TEMPERATURA
        TemperatureThresholdCard(
            tempMaxLimit = settings.tempMaxLimit,
            onLimitChange = onTempMaxLimitChange,
        )

        // Tarjeta 2: RANGO DE HUMEDAD
        HumidityRangeCard(
            minHumidity = settings.humidityMinLimit,
            maxHumidity = settings.humidityMaxLimit,
            warnHumidity = settings.warnHumidity,
            onMinChange = onHumidityMinLimitChange,
            onMaxChange = onHumidityMaxLimitChange,
            onWarnChange = onWarnHumidityChange,
        )

        // Tarjeta 3: NOTIFICACIONES
        NotificationsCard(
            pushEnabled = settings.pushNotificationsEnabled,
            doorAlertEnabled = settings.doorAlertEnabled,
            alarmSoundEnabled = settings.alarmSoundEnabled,
            onPushChange = onPushNotificationsChange,
            onDoorChange = onDoorAlertChange,
            onAlarmChange = onAlarmSoundChange,
        )

        // Tarjeta 4: DATOS
        DataManagementCard(
            onResetToFactory = onResetToFactory,
        )
    }
}

@Composable
fun TemperatureThresholdCard(
    tempMaxLimit: Double,
    onLimitChange: (Double) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "UMBRAL DE TEMPERATURA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TealAccent,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Límite máximo permitido",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Sobre este valor se activa la alerta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "${tempMaxLimit.toInt()} °C",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = tempMaxLimit.toFloat(),
                onValueChange = { onLimitChange(it.toDouble()) },
                valueRange = 2f..15f,
                colors = SliderDefaults.colors(
                    thumbColor = TealAccent,
                    activeTrackColor = TealAccent,
                    inactiveTrackColor = TealAccent.copy(alpha = 0.2f),
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "2 °C", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "15 °C", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HumidityRangeCard(
    minHumidity: Double,
    maxHumidity: Double,
    warnHumidity: Boolean,
    onMinChange: (Double) -> Unit,
    onMaxChange: (Double) -> Unit,
    onWarnChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RANGO DE HUMEDAD",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TealAccent,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Humedad Mínima
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Humedad mínima",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Bajo este valor se advierte sequedad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "${minHumidity.toInt()} %",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent,
                )
            }

            Slider(
                value = minHumidity.toFloat(),
                onValueChange = { onMinChange(it.toDouble()) },
                valueRange = 20f..60f,
                colors = SliderDefaults.colors(
                    thumbColor = TealAccent,
                    activeTrackColor = TealAccent,
                    inactiveTrackColor = TealAccent.copy(alpha = 0.2f),
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Humedad Máxima
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Humedad máxima",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Sobre este valor se advierte exceso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "${maxHumidity.toInt()} %",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent,
                )
            }

            Slider(
                value = maxHumidity.toFloat(),
                onValueChange = { onMaxChange(it.toDouble()) },
                valueRange = 60f..95f,
                colors = SliderDefaults.colors(
                    thumbColor = TealAccent,
                    activeTrackColor = TealAccent,
                    inactiveTrackColor = TealAccent.copy(alpha = 0.2f),
                ),
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            )

            // Switch Advertir por humedad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Advertir por humedad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Fuera de rango o riesgo de condensación",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Switch(
                    checked = warnHumidity,
                    onCheckedChange = onWarnChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealAccent,
                    ),
                )
            }
        }
    }
}

@Composable
fun NotificationsCard(
    pushEnabled: Boolean,
    doorAlertEnabled: Boolean,
    alarmSoundEnabled: Boolean,
    onPushChange: (Boolean) -> Unit,
    onDoorChange: (Boolean) -> Unit,
    onAlarmChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "NOTIFICACIONES",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TealAccent,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 1: Push
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Notificaciones push",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Aviso emergente al detectar una alerta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Switch(
                    checked = pushEnabled,
                    onCheckedChange = onPushChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealAccent,
                    ),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            )

            // Item 2: Puerta abierta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Alertar por puerta abierta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Aviso amarillo y registro en el historial",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Switch(
                    checked = doorAlertEnabled,
                    onCheckedChange = onDoorChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealAccent,
                    ),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            )

            // Item 3: Sonido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sonido de alarma",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Pitido al entrar en alerta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Switch(
                    checked = alarmSoundEnabled,
                    onCheckedChange = onAlarmChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealAccent,
                    ),
                )
            }
        }
    }
}

@Composable
fun DataManagementCard(
    onResetToFactory: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DATOS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TealAccent,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 1: Valores de fábrica
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Valores de fábrica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Restablece todos los ajustes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                OutlinedButton(
                    onClick = onResetToFactory,
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        text = "Restablecer",
                        fontWeight = FontWeight.Bold,
                        color = TealAccent,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            )

            // Item 2: Versión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Versión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Prototipo UI · Semana 7",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MyApplicationTheme {
        SettingsScreen(
            settings = AppSettings(),
            onTempMaxLimitChange = {},
            onHumidityMinLimitChange = {},
            onHumidityMaxLimitChange = {},
            onWarnHumidityChange = {},
            onPushNotificationsChange = {},
            onDoorAlertChange = {},
            onAlarmSoundChange = {},
            onResetToFactory = {},
        )
    }
}
