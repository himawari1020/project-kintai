package com.example.attendance.domain.usecase.attendance

import com.example.attendance.domain.model.Attendance
import com.example.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    operator fun invoke(userId: String): Flow<Attendance?> {
        return repository.getTodayAttendance(userId)
    }
}
