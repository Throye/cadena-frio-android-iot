package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Capa 1: Modelo DTO para la deserialización segura de JSON de IoT.
 * Soporta de forma transparente los nombres de campos del ESP32 Wokwi ("temperaturaC", "humedadPct", "puertaAbierta")
 * y nombres alternativos ("temperatura", "humedad", "puerta_abierta").
 */
@Serializable
data class ColdChainPayload(
    @SerialName("temperaturaC")
    val temperaturaC: Double? = null,

    @SerialName("temperatura")
    val temperatura: Double? = null,

    @SerialName("humedadPct")
    val humedadPct: Double? = null,

    @SerialName("humedad")
    val humedad: Double? = null,

    @SerialName("puertaAbierta")
    val puertaAbierta: Boolean? = null,

    @SerialName("puerta_abierta")
    val puertaAbiertaSnake: Boolean? = null,

    @SerialName("estado_sistema")
    val estadoSistema: String? = null,

    @SerialName("timestamp")
    val timestamp: String? = null,
) {
    val tempFinal: Double? get() = temperaturaC ?: temperatura
    val humedadFinal: Double? get() = humedadPct ?: humedad
    val puertaAbiertaFinal: Boolean? get() = puertaAbierta ?: puertaAbiertaSnake
}
