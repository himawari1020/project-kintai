package com.example.attendance.domain.usecase.attendance

import com.example.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class ClockOutUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(userId: String, attendanceId: String): Result<Unit> {
        return repository.clockOut(userId, attendanceId)
    }
}
