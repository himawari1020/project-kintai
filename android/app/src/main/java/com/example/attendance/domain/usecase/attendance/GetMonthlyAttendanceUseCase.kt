package com.example.attendance.domain.usecase.attendance

import com.example.attendance.domain.model.Attendance
import com.example.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    operator fun invoke(userId: String, yearMonth: String): Flow<List<Attendance>> {
        return repository.getMonthlyAttendance(userId, yearMonth)
    }
}
