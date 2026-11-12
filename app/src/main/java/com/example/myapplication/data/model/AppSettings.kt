package com.example.myapplication.data.model

/**
 * Modelo de datos para las configuraciones y ajustes configurables por el usuario.
 */
data class AppSettings(
    val tempMaxLimit: Double = 8.0,
    val humidityMinLimit: Double = 50.0,
    val humidityMaxLimit: Double = 85.0,
    val warnHumidity: Boolean = true,
    val pushNotificationsEnabled: Boolean = true,
    val doorAlertEnabled: Boolean = true,
    val alarmSoundEnabled: Boolean = false,
)
