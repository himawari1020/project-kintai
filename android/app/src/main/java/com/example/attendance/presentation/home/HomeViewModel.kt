package com.example.attendance.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.domain.repository.AuthRepository
import com.example.attendance.domain.usecase.attendance.ClockInUseCase
import com.example.attendance.domain.usecase.attendance.ClockOutUseCase
import com.example.attendance.domain.usecase.attendance.GetTodayAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getTodayAttendanceUseCase: GetTodayAttendanceUseCase,
    private val clockInUseCase: ClockInUseCase,
    private val clockOutUseCase: ClockOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var timeUpdateJob: Job? = null

    init {
        startTimeUpdate()
        loadAttendance()
    }

    private fun startTimeUpdate() {
        timeUpdateJob?.cancel()
        timeUpdateJob = viewModelScope.launch {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            while (isActive) {
                _state.update { it.copy(currentTime = sdf.format(Date())) }
                delay(1000)
            }
        }
    }

    private fun loadAttendance() {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        _state.update { it.copy(isLoading = true) }

        getTodayAttendanceUseCase(userId).onEach { attendance ->
            _state.update { it.copy(attendance = attendance, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun clockIn() {
        val userId = authRepository.currentUserId ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = clockInUseCase(userId)
            if (result.isFailure) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to clock in"
                    ) 
                }
            } else {
                 _state.update { it.copy(isLoading = false, error = null) }
            }
        }
    }

    fun clockOut() {
        val userId = authRepository.currentUserId ?: return
        val attendanceId = state.value.attendance?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = clockOutUseCase(userId, attendanceId)
            if (result.isFailure) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to clock out"
                    ) 
                }
            } else {
                 _state.update { it.copy(isLoading = false, error = null) }
            }
        }
    }
}
