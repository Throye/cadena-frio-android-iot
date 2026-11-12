package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.data.repository.ColdChainData
import com.example.myapplication.data.repository.ColdChainRepository
import com.example.myapplication.data.repository.ColdChainRepositoryImpl
import com.example.myapplication.data.repository.HistoricalReading
import com.example.myapplication.data.repository.TestScenario
import com.example.myapplication.data.repository.TestScenarios
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Capa 3: ViewModel que gestiona el estado de la UI, los ajustes y los eventos de error.
 */
class DashboardViewModel(
    private val repository: ColdChainRepository = ColdChainRepositoryImpl(),
) : ViewModel() {

    val coldChainData: StateFlow<ColdChainData> = repository.dataFlow
    val historyData: StateFlow<List<HistoricalReading>> = repository.historyFlow

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    private val _selectedScenario = MutableStateFlow(TestScenarios.SCENARIOS.first())
    val selectedScenario: StateFlow<TestScenario> = _selectedScenario.asStateFlow()

    fun updateTempMaxLimit(limit: Double) {
        _appSettings.value = _appSettings.value.copy(tempMaxLimit = limit)
    }

    fun updateHumidityMinLimit(limit: Double) {
        _appSettings.value = _appSettings.value.copy(humidityMinLimit = limit)
    }

    fun updateHumidityMaxLimit(limit: Double) {
        _appSettings.value = _appSettings.value.copy(humidityMaxLimit = limit)
    }

    fun updateWarnHumidity(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(warnHumidity = enabled)
    }

    fun updatePushNotifications(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(pushNotificationsEnabled = enabled)
    }

    fun updateDoorAlert(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(doorAlertEnabled = enabled)
    }

    fun updateAlarmSound(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(alarmSoundEnabled = enabled)
    }

    fun resetToFactorySettings() {
        _appSettings.value = AppSettings()
    }

    fun selectScenario(scenario: TestScenario) {
        _selectedScenario.value = scenario
        applyJson(scenario.jsonContent)
    }

    fun applyJson(jsonString: String) {
        viewModelScope.launch {
            val result = repository.setCurrentScenarioJson(jsonString)
            result.onFailure { exception ->
                _errorMessage.emit("⚠️ Error en JSON: ${exception.localizedMessage ?: "Formato inválido"}. Se mantuvieron los datos anteriores.")
            }
        }
    }
}
