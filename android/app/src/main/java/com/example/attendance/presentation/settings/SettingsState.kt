package com.example.attendance.presentation.settings

data class SettingsState(
    val email: String = "",
    val version: String = "",
    val isLoading: Boolean = false
)
