package com.example.attendance.domain.usecase.attendance

import com.example.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class ClockInUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.clockIn(userId)
    }
}
