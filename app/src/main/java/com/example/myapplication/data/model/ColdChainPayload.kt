package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Capa 1: Modelo DTO para la deserialización segura de JSON de IoT.
 * Todos los campos tienen valores por defecto o son anulables para evitar crashes si el JSON viene incompleto.
 */
@Serializable
data class ColdChainPayload(
    @SerialName("temperatura")
    val temperatura: Double? = null,

    @SerialName("humedad")
    val humedad: Double? = null,

    @SerialName("puerta_abierta")
    val puertaAbierta: Boolean? = null,

    @SerialName("estado_sistema")
    val estadoSistema: String? = null,

    @SerialName("timestamp")
    val timestamp: String? = null,
)
