package com.example.myapplication.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Locale

enum class MetricType(val label: String, val unit: String) {
    TEMPERATURE("Temperatura", "°C"),
    HUMIDITY("Humedad", "%"),
}

enum class TimeRange(val label: String) {
    MIN_15("15 min"),
    MIN_30("30 min"),
    MIN_60("60 min"),
    DAY("Día"),
    WEEK("Semana"),
}

@Composable
fun ChartsScreen(
    modifier: Modifier = Modifier,
    currentTemp: Double = 4.0,
    currentHumidity: Double = 62.0,
) {
    var selectedMetric by remember { mutableStateOf(MetricType.TEMPERATURE) }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.MIN_30) }

    val readings = remember(selectedMetric, selectedTimeRange, currentTemp, currentHumidity) {
        generateDummyReadings(selectedMetric, selectedTimeRange, currentTemp, currentHumidity)
    }

    val minVal = readings.minOrNull() ?: 0.0
    val maxVal = readings.maxOrNull() ?: 0.0
    val avgVal = if (readings.isNotEmpty()) readings.average() else 0.0
    val limitThreshold = if (selectedMetric == MetricType.TEMPERATURE) 8.0 else 80.0
    val outOfRangeCount = readings.count { it > limitThreshold }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = "${selectedMetric.label.uppercase()} VS. TIEMPO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFC8E6C9),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EN VIVO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                MetricSegmentedSelector(
                    selectedMetric = selectedMetric,
                    onMetricSelected = { selectedMetric = it },
                )

                Spacer(modifier = Modifier.height(12.dp))

                TimeRangeSegmentedSelector(
                    selectedTimeRange = selectedTimeRange,
                    onRangeSelected = { selectedTimeRange = it },
                )

                Spacer(modifier = Modifier.height(20.dp))

                InteractiveLineChart(
                    readings = readings,
                    limitThreshold = limitThreshold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ChartLegendItem(color = Color(0xFF0277BD), label = selectedMetric.label)
                    ChartLegendItem(color = Color(0xFFC62828), label = "Sobre el límite")
                    ChartLegendItem(color = Color(0xFFEF6C00), label = "Puerta abierta")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Toca o pasa el cursor sobre el gráfico para ver cada lectura.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryValueCard(
                title = "MÍNIMA",
                value = String.format(Locale.US, "%.1f %s", minVal, selectedMetric.unit),
                modifier = Modifier.weight(1f),
            )
            SummaryValueCard(
                title = "MÁXIMA",
                value = String.format(Locale.US, "%.1f %s", maxVal, selectedMetric.unit),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryValueCard(
                title = "PROMEDIO",
                value = String.format(Locale.US, "%.1f %s", avgVal, selectedMetric.unit),
                modifier = Modifier.weight(1f),
            )
            SummaryValueCard(
                title = "FUERA DE RANGO",
                value = "$outOfRangeCount min",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun MetricSegmentedSelector(
    selectedMetric: MetricType,
    onMetricSelected: (MetricType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MetricType.entries.forEach { metric ->
                val isSelected = metric == selectedMetric
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) Color(0xFFB2EBF2) else Color.Transparent)
                        .clickable { onMetricSelected(metric) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF004D40) else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
fun TimeRangeSegmentedSelector(
    selectedTimeRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TimeRange.entries.forEach { range ->
                val isSelected = range == selectedTimeRange
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) Color(0xFFB2EBF2) else Color.Transparent)
                        .clickable { onRangeSelected(range) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = range.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF004D40) else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveLineChart(
    readings: List<Double>,
    limitThreshold: Double,
    modifier: Modifier = Modifier,
) {
    val lineColor = Color(0xFF0277BD)
    val limitLineColor = Color(0xFFC62828)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (readings.isEmpty()) return@Canvas

            val w = size.width
            val h = size.height
            val minReading = (readings.minOrNull() ?: 0.0).coerceAtMost(0.0)
            val maxReading = (readings.maxOrNull() ?: 10.0).coerceAtLeast(limitThreshold + 2.0)
            val range = (maxReading - minReading).coerceAtLeast(1.0)

            fun getY(valNum: Double): Float {
                val norm = ((valNum - minReading) / range).toFloat()
                return h - (norm * h * 0.8f) - (h * 0.1f)
            }

            val gridValues = listOf(0.0, 5.0, 10.0)
            gridValues.forEach { gridVal ->
                if (gridVal in minReading..maxReading) {
                    val y = getY(gridVal)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f,
                    )
                }
            }

            val limitY = getY(limitThreshold)
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            drawLine(
                color = limitLineColor,
                start = Offset(0f, limitY),
                end = Offset(w, limitY),
                strokeWidth = 3f,
                pathEffect = pathEffect,
            )

            val stepX = w / (readings.size - 1).coerceAtLeast(1)
            val path = Path()
            val fillPath = Path()

            readings.forEachIndexed { i, num ->
                val x = i * stepX
                val y = getY(num)
                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(w, h)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.35f),
                        lineColor.copy(alpha = 0.02f),
                    ),
                ),
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 4f),
            )

            val lastX = (readings.size - 1) * stepX
            val lastY = getY(readings.last())
            drawCircle(
                color = lineColor,
                radius = 7f,
                center = Offset(lastX, lastY),
            )
        }
    }
}

@Composable
fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun SummaryValueCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1),
            )
        }
    }
}

private fun generateDummyReadings(
    metric: MetricType,
    range: TimeRange,
    baseTemp: Double,
    baseHum: Double,
): List<Double> {
    val count = when (range) {
        TimeRange.MIN_15 -> 15
        TimeRange.MIN_30 -> 30
        TimeRange.MIN_60 -> 60
        TimeRange.DAY -> 24
        TimeRange.WEEK -> 14
    }

    val base = if (metric == MetricType.TEMPERATURE) baseTemp else baseHum
    val variation = if (metric == MetricType.TEMPERATURE) 0.3 else 1.5

    return List(count) { i ->
        val noise = (kotlin.math.sin(i.toDouble() * 0.5) * variation)
        (base + noise).coerceIn(0.0, 100.0)
    }
}

@Preview(showBackground = true)
@Composable
fun ChartsScreenPreview() {
    MyApplicationTheme {
        ChartsScreen()
    }
}
