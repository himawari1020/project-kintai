package com.example.attendance.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.domain.repository.AuthRepository
import com.example.attendance.domain.usecase.attendance.GetMonthlyAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private valgetMonthlyAttendanceUseCase: GetMonthlyAttendanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        val today = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        _state.update { it.copy(currentYearMonth = today) }
        loadAttendance(today)
    }

    fun loadAttendance(yearMonth: String) {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        _state.update { it.copy(isLoading = true, currentYearMonth = yearMonth) }

        getMonthlyAttendanceUseCase(userId, yearMonth).onEach { list ->
            _state.update { 
                it.copy(
                    attendanceList = list,
                    isLoading = false,
                    error = null
                ) 
            }
        }.launchIn(viewModelScope)
    }

    fun nextMonth() {
        val current = _state.value.currentYearMonth
        val next = changeMonth(current, 1)
        loadAttendance(next)
    }

    fun prevMonth() {
        val current = _state.value.currentYearMonth
        val prev = changeMonth(current, -1)
        loadAttendance(prev)
    }

    private fun changeMonth(yearMonth: String, amount: Int): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val date = sdf.parse(yearMonth) ?: Date()
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.MONTH, amount)
            return sdf.format(cal.time)
        } catch (e: Exception) {
            return yearMonth
        }
    }
}
