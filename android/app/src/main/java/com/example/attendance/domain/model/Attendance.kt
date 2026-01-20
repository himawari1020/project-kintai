package com.example.attendance.domain.model

import java.util.Date

enum class AttendanceStatus {
    OFF,        // 勤務外
    WORKING,    // 勤務中
    LEFT        // 退勤済
}

data class Attendance(
    val id: String = "",
    val userId: String = "",
    val date: String = "", // YYYY-MM-DD
    val clockIn: Date? = null,
    val clockOut: Date? = null,
    val status: AttendanceStatus = AttendanceStatus.OFF,
    val workTimeMinutes: Int? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)
