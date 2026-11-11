package com.example.myapplication.data.repository

/**
 * Escenarios de prueba JSON editables para simular la llegada de datos de sensores IoT.
 */
data class TestScenario(
    val id: String,
    val name: String,
    val description: String,
    val jsonContent: String,
)

object TestScenarios {

    val SCENARIOS = listOf(
        TestScenario(
            id = "normal",
            name = "🟢 Estado Normal (Seguro)",
            description = "Temperatura a 3.8°C, puerta CERRADA, sistema en Cadena de Frío Segura.",
            jsonContent = """
                {
                    "temperaturaC": 3.8,
                    "humedadPct": 58.0,
                    "puertaAbierta": false,
                    "estado_sistema": "segura",
                    "timestamp": "Hace 2 min"
                }
            """.trimIndent(),
        ),
        TestScenario(
            id = "esp32_wokwi",
            name = "⚡ ESP32 Wokwi Hardware",
            description = "Formato directo del código C++ del ESP32 (temperaturaC, humedadPct, puertaAbierta).",
            jsonContent = """
                {
                    "temperaturaC": 6.0,
                    "humedadPct": 64.5,
                    "puertaAbierta": false
                }
            """.trimIndent(),
        ),
        TestScenario(
            id = "puerta_abierta",
            name = "🟠 Atención: Puerta Abierta",
            description = "Puerta ABIERTA detectada por más de 2 minutos. Temperatura subiendo.",
            jsonContent = """
                {
                    "temperatura": 7.4,
                    "humedad": 71.0,
                    "puerta_abierta": true,
                    "estado_sistema": "puerta_abierta",
                    "timestamp": "Hace 1 min"
                }
            """.trimIndent(),
        ),
        TestScenario(
            id = "alerta_fuga",
            name = "🔴 ¡Alerta de Fuga de Frío!",
            description = "Temperatura fuera de rango crítico (14.5°C). Alerta de Fuga de Frío activada.",
            jsonContent = """
                {
                    "temperatura": 14.5,
                    "humedad": 83.2,
                    "puerta_abierta": true,
                    "estado_sistema": "alerta",
                    "timestamp": "Ahora mismo"
                }
            """.trimIndent(),
        ),
        TestScenario(
            id = "json_incompleto",
            name = "⚠️ JSON Incompleto (Null Safety)",
            description = "JSON con campos faltantes para verificar que los fallbacks evitan que la app se caiga.",
            jsonContent = """
                {
                    "temperatura": 5.0
                }
            """.trimIndent(),
        ),
        TestScenario(
            id = "json_corrupto",
            name = "❌ JSON Corrupto (Manejo de Errores)",
            description = "Sintaxis JSON inválida para verificar la captura en try-catch y aviso en Logcat/UI.",
            jsonContent = """
                {
                    "temperatura": "INVALID_VALUE",
                    "puerta_abierta": true,
                    BROKEN_JSON_SYNTAX...
                }
            """.trimIndent(),
        ),
    )
}
