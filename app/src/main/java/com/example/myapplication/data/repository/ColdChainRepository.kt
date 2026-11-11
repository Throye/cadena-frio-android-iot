package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.model.ColdChainPayload
import com.example.myapplication.ui.monitor.DoorState
import com.example.myapplication.ui.monitor.SystemStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * Modelo de Dominio limpio para representar el estado de la Cadena de Frío en la UI.
 */
data class ColdChainData(
    val temperature: Double?,
    val humidity: Double?,
    val doorState: DoorState,
    val systemStatus: SystemStatus,
    val timestamp: String,
)

/**
 * Capa 2: Interfaz del Repositorio de datos.
 * Desacopla la fuente de datos (JSON simulado / servidor IoT real) de las pantallas de Compose.
 */
interface ColdChainRepository {
    val dataFlow: StateFlow<ColdChainData>
    fun parseJsonPayload(jsonString: String): Result<ColdChainData>
    fun setCurrentScenarioJson(jsonString: String): Result<ColdChainData>
}

/**
 * Implementación robusta con captura try-catch y registro en Logcat.
 */
class ColdChainRepositoryImpl : ColdChainRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val _dataFlow = MutableStateFlow(createDefaultData())
    override val dataFlow: StateFlow<ColdChainData> = _dataFlow.asStateFlow()

    override fun parseJsonPayload(jsonString: String): Result<ColdChainData> {
        return try {
            if (jsonString.isBlank()) {
                throw IllegalArgumentException("El JSON de entrada está vacío o es nulo.")
            }
            val payload = jsonParser.decodeFromString<ColdChainPayload>(jsonString)
            val parsedData = mapPayloadToData(payload)
            Log.d(TAG, "JSON procesado exitosamente: $parsedData")
            Result.success(parsedData)
        } catch (e: Exception) {
            // Capa 2 Error Logcat: Registra la excepción completa sin cerrar la app
            Log.e(TAG, "Error crítico al procesar JSON del sensor: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun setCurrentScenarioJson(jsonString: String): Result<ColdChainData> {
        val result = parseJsonPayload(jsonString)
        result.onSuccess { data ->
            _dataFlow.value = data
        }
        return result
    }

    private fun mapPayloadToData(payload: ColdChainPayload): ColdChainData {
        val doorState = when (payload.puertaAbierta) {
            true -> DoorState.OPEN
            false -> DoorState.CLOSED
            null -> DoorState.CLOSED
        }

        val systemStatus = when (payload.estadoSistema?.lowercase()) {
            "segura", "safe", "ok" -> SystemStatus.SAFE
            "puerta_abierta", "door_open", "warning" -> SystemStatus.DOOR_OPEN
            "alerta", "fuga", "critical" -> SystemStatus.COLD_LEAK_ALERT
            else -> {
                if (doorState == DoorState.OPEN) SystemStatus.DOOR_OPEN
                else SystemStatus.SAFE
            }
        }

        return ColdChainData(
            temperature = payload.temperatura,
            humidity = payload.humedad,
            doorState = doorState,
            systemStatus = systemStatus,
            timestamp = payload.timestamp ?: "Ahora",
        )
    }

    private fun createDefaultData() = ColdChainData(
        temperature = 4.2,
        humidity = 62.0,
        doorState = DoorState.CLOSED,
        systemStatus = SystemStatus.SAFE,
        timestamp = "Reciente",
    )

    companion object {
        private const val TAG = "ColdChainRepository"
    }
}
