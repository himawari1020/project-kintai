package com.example.attendance.presentation.home

import com.example.attendance.domain.model.Attendance

data class HomeState(
    val attendance: Attendance? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentTime: String = ""
)
