package com.example.attendance.presentation.history

import com.example.attendance.domain.model.Attendance

data class HistoryState(
    val attendanceList: List<Attendance> = emptyList(),
    val currentYearMonth: String = "", // YYYY-MM
    val isLoading: Boolean = false,
    val error: String? = null
)
