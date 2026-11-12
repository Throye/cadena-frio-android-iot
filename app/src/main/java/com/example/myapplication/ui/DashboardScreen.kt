package com.example.myapplication.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.charts.ChartsScreen
import com.example.myapplication.ui.monitor.MonitorScreen
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

enum class DashboardTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    MONITOR(
        title = "Monitor",
        selectedIcon = Icons.Filled.Monitor,
        unselectedIcon = Icons.Outlined.Monitor,
    ),
    GRAFICOS(
        title = "Gráficos",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
    ),
    AJUSTES(
        title = "Ajustes",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.MONITOR) }
    val coldChainData by viewModel.coldChainData.collectAsState()
    val historyReadings by viewModel.historyData.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Cadena de Frío - ${selectedTab.title}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            NavigationBar {
                DashboardTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                            )
                        },
                        label = { Text(text = tab.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            DashboardTab.MONITOR -> {
                MonitorScreen(
                    systemStatus = coldChainData.systemStatus,
                    doorState = coldChainData.doorState,
                    temperature = coldChainData.temperature,
                    humidity = coldChainData.humidity,
                    maxTempLimit = appSettings.tempMaxLimit,
                    minHumidityLimit = appSettings.humidityMinLimit,
                    maxHumidityLimit = appSettings.humidityMaxLimit,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            DashboardTab.GRAFICOS -> {
                ChartsScreen(
                    currentTemp = coldChainData.temperature ?: 4.0,
                    currentHumidity = coldChainData.humidity ?: 62.0,
                    historyReadings = historyReadings,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            DashboardTab.AJUSTES -> {
                SettingsScreen(
                    settings = appSettings,
                    onTempMaxLimitChange = { viewModel.updateTempMaxLimit(it) },
                    onHumidityMinLimitChange = { viewModel.updateHumidityMinLimit(it) },
                    onHumidityMaxLimitChange = { viewModel.updateHumidityMaxLimit(it) },
                    onWarnHumidityChange = { viewModel.updateWarnHumidity(it) },
                    onPushNotificationsChange = { viewModel.updatePushNotifications(it) },
                    onDoorAlertChange = { viewModel.updateDoorAlert(it) },
                    onAlarmSoundChange = { viewModel.updateAlarmSound(it) },
                    onResetToFactory = { viewModel.resetToFactorySettings() },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MyApplicationTheme {
        DashboardScreen()
    }
}
