package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.model.ColdChainPayload
import com.example.myapplication.ui.monitor.DoorState
import com.example.myapplication.ui.monitor.SystemStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlin.math.cos
import kotlin.math.sin

/**
 * Registro histórico de lecturas individuales de sensores acumuladas en tiempo real.
 */
data class HistoricalReading(
    val temperature: Double,
    val humidity: Double,
    val isDoorOpen: Boolean,
    val timestampMs: Long = System.currentTimeMillis(),
)

/**
 * Modelo de Dominio limpio para representar el estado actual de la Cadena de Frío.
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
 */
interface ColdChainRepository {
    val dataFlow: StateFlow<ColdChainData>
    val historyFlow: StateFlow<List<HistoricalReading>>
    fun parseJsonPayload(jsonString: String): Result<ColdChainData>
    fun setCurrentScenarioJson(jsonString: String): Result<ColdChainData>
    fun stopListening()
}

/**
 * Implementación del repositorio con conexión en tiempo real a Firebase Realtime Database
 * e integración resiliente con el simulador local.
 */
class ColdChainRepositoryImpl : ColdChainRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val _dataFlow = MutableStateFlow(createDefaultData())
    override val dataFlow: StateFlow<ColdChainData> = _dataFlow.asStateFlow()

    private val _historyFlow = MutableStateFlow(generateInitialHistory())
    override val historyFlow: StateFlow<List<HistoricalReading>> = _historyFlow.asStateFlow()

    private val databaseReference = try {
        FirebaseDatabase.getInstance().getReference("lecturas")
    } catch (e: Exception) {
        Log.e(TAG, "Error inicializando Firebase Database: ${e.message}")
        null
    }

    private val firebaseListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists() || !snapshot.hasChildren()) return

            val readings = mutableListOf<HistoricalReading>()
            var latestData: ColdChainData? = null

            for (readingSnapshot in snapshot.children) {
                val tempVal = parseDoubleSafely(readingSnapshot.child("temperaturaC").value)
                    ?: parseDoubleSafely(readingSnapshot.child("temperatura").value)

                val humVal = parseDoubleSafely(readingSnapshot.child("humedadPct").value)
                    ?: parseDoubleSafely(readingSnapshot.child("humedad").value)

                val doorVal = parseBooleanSafely(readingSnapshot.child("puertaAbierta").value)
                    ?: parseBooleanSafely(readingSnapshot.child("puerta_abierta").value)

                val payload = ColdChainPayload(
                    temperaturaC = tempVal,
                    humedadPct = humVal,
                    puertaAbierta = doorVal,
                )

                val parsedData = mapPayloadToData(payload)

                if (parsedData.temperature != null && parsedData.humidity != null) {
                    readings.add(
                        HistoricalReading(
                            temperature = parsedData.temperature,
                            humidity = parsedData.humidity,
                            isDoorOpen = parsedData.doorState == DoorState.OPEN,
                        )
                    )
                    latestData = parsedData
                }
            }

            if (readings.isNotEmpty()) {
                _historyFlow.value = readings
            }

            latestData?.let {
                _dataFlow.value = it
            }

            Log.d(TAG, "Lecturas sincronizadas desde Firebase: ${readings.size}")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(TAG, "Error en tiempo real de Firebase: ${error.message}")
        }
    }

    init {
        try {
            databaseReference?.addValueEventListener(firebaseListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error al suscribir ValueEventListener en Firebase: ${e.message}")
        }
    }

    override fun stopListening() {
        try {
            databaseReference?.removeEventListener(firebaseListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error al desuscribir listener: ${e.message}")
        }
    }

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
            Log.e(TAG, "Error crítico al procesar JSON del sensor: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun setCurrentScenarioJson(jsonString: String): Result<ColdChainData> {
        val result = parseJsonPayload(jsonString)
        result.onSuccess { data ->
            _dataFlow.value = data
            val newReading = HistoricalReading(
                temperature = data.temperature ?: 4.0,
                humidity = data.humidity ?: 60.0,
                isDoorOpen = data.doorState == DoorState.OPEN,
            )
            _historyFlow.value = _historyFlow.value + newReading
        }
        return result
    }

    private fun parseDoubleSafely(value: Any?): Double? {
        return when (value) {
            is Double -> value
            is Float -> value.toDouble()
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    private fun parseBooleanSafely(value: Any?): Boolean? {
        return when (value) {
            is Boolean -> value
            is String -> value.equals("true", ignoreCase = true)
            is Number -> value.toInt() != 0
            else -> null
        }
    }

    private fun mapPayloadToData(payload: ColdChainPayload): ColdChainData {
        val doorState = when (payload.puertaAbiertaFinal) {
            true -> DoorState.OPEN
            false -> DoorState.CLOSED
            null -> DoorState.CLOSED
        }

        val temp = payload.tempFinal

        val systemStatus = when (payload.estadoSistema?.lowercase()) {
            "segura", "safe", "ok" -> SystemStatus.SAFE
            "puerta_abierta", "door_open", "warning" -> SystemStatus.DOOR_OPEN
            "alerta", "fuga", "critical" -> SystemStatus.COLD_LEAK_ALERT
            else -> {
                if (doorState == DoorState.OPEN) SystemStatus.DOOR_OPEN
                else if (temp != null && temp > 8.0) SystemStatus.COLD_LEAK_ALERT
                else SystemStatus.SAFE
            }
        }

        return ColdChainData(
            temperature = temp,
            humidity = payload.humedadFinal,
            doorState = doorState,
            systemStatus = systemStatus,
            timestamp = payload.timestamp ?: "En vivo",
        )
    }

    private fun createDefaultData() = ColdChainData(
        temperature = 4.2,
        humidity = 62.0,
        doorState = DoorState.CLOSED,
        systemStatus = SystemStatus.SAFE,
        timestamp = "Conectado a Firebase",
    )

    private fun generateInitialHistory(): List<HistoricalReading> {
        val now = System.currentTimeMillis()
        val minuteMs = 60 * 1000L
        val baseTemp = 4.0
        val baseHum = 62.0

        return List(30) { i ->
            val timeOffset = now - ((30 - i) * minuteMs)
            val tempNoise = (sin(i.toDouble() * 0.4) * 0.3)
            val humNoise = (cos(i.toDouble() * 0.3) * 1.2)
            HistoricalReading(
                temperature = baseTemp + tempNoise,
                humidity = baseHum + humNoise,
                isDoorOpen = false,
                timestampMs = timeOffset,
            )
        }
    }

    companion object {
        private const val TAG = "ColdChainRepository"
    }
}
