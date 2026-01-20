package com.example.attendance.domain.repository

import com.example.attendance.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getTodayAttendance(userId: String): Flow<Attendance?>
    fun getMonthlyAttendance(userId: String, yearMonth: String): Flow<List<Attendance>>
    suspend fun clockIn(userId: String): Result<Unit>
    suspend fun clockOut(userId: String, attendanceId: String): Result<Unit>
}
